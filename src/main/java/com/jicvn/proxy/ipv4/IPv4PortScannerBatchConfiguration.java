package com.jicvn.proxy.ipv4;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.jicvn.proxy.entity.Proxy;
import com.jicvn.proxy.enums.ProxyStatusEnum;
import com.jicvn.proxy.repository.ProxyRepository;
import com.jicvn.proxy.utils.IntUtils;

@Configuration
@EnableBatchProcessing
public class IPv4PortScannerBatchConfiguration {

	private final static Logger logger = LoggerFactory.getLogger(IPv4PortScannerBatchConfiguration.class);

	@Resource
	private JobBuilderFactory jobBuilderFactory; 

	@Resource
	private StepBuilderFactory stepBuilderFactory; 

	@Autowired
	private ProxyRepository proxyRepository;
	
	static final Integer[] DEFAULT_PORTS = {9999, 80, 808, 8080, 8118, 18118, 53281};
	
	private static final int MAX_EACH = 100;

	@Bean
	public ItemReader<Proxy> getIPv4PortScannerReader() {
		IPv4PortScannerSolrPagingItemReader reader = new IPv4PortScannerSolrPagingItemReader(proxyRepository);
		try {
			reader.setPageSize(100);
			reader.setSaveState(true);
			reader.afterPropertiesSet();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return reader;
	}

	@Bean
	public ItemProcessor<Proxy, Proxy> getIPv4PortScannerProcessor() {
		return new ItemProcessor<Proxy, Proxy>() {
			@Override
			public Proxy process(Proxy proxy) throws Exception {
				return proxy;
			}
		};
	}

	@Bean
	public ItemWriter<Proxy> getIPv4PortScannerWriter() {
		return proxies -> {
			proxies.parallelStream()
			.peek(proxy->{
				proxy.setStatus(ProxyStatusEnum.WORKING.name());
				proxyRepository.save(proxy);
			})
			.forEach(proxy->{
				logger.info("扫描IP：" + proxy.getHost());
				Proxy proxy0 = Stream.of(proxy.getPort()).parallel().flatMap(port->{
					if(port == null)
						return Stream.of(DEFAULT_PORTS);
					
					if(port > 0xFFFF) {
						port = 0x0;
					}
					
					return IntStream.rangeClosed(port, port + MAX_EACH).boxed();
				})
				.filter(port->portIsOpen(proxy.getHost(), port)).limit(1).findFirst()
				.map(port->{
					proxy.setPort(port);
					proxy.setRequestCount(IntUtils.plusOne(proxy.getRequestCount()));
					proxy.setSuccessCount(IntUtils.plusOne(proxy.getSuccessCount()));
					proxy.setUpdateDatetime(IntUtils.currentTimeSecond());
					proxy.setStatus(ProxyStatusEnum.SUCCESS.name());
					return proxy;
				})
				.orElseGet(()->{
					proxy.setPort(IntUtils.valueOf(proxy.getPort()) + MAX_EACH);
					proxy.setRequestCount(IntUtils.plusOne(proxy.getRequestCount()));
					proxy.setUpdateDatetime(IntUtils.currentTimeSecond());
					proxy.setStatus(ProxyStatusEnum.FAILURE.name());
					return proxy;
				});
				proxyRepository.save(proxy0);
			});
		};
	}

	private boolean portIsOpen(String host, Integer port) {
		
		if(port == null) {
			throw new IllegalArgumentException("port is null:" + port);
		}
		
		if (port < 0 || port > 0xFFFF) {
			throw new IllegalArgumentException("port out of range:" + port);
		}
		
		try(Socket soc = new Socket()) {
			soc.connect(new InetSocketAddress(host, port) , 200);
			return true;
		} catch (IOException e) {
		}
		return false;
	}

	@Bean
	public Job handleIPv4PortScannerJob() {
		return jobBuilderFactory //
				.get("handleIPv4PortScannerJob") //
				.incrementer(new RunIdIncrementer()) //
				.start(handleIPv4PortScannerStep()) // start是JOB执行的第一个step
				.build();
	}

	@Bean
	public Step handleIPv4PortScannerStep() {
		return stepBuilderFactory //
				.get("handleIPv4PortScannerStep") //
				.<Proxy, Proxy>chunk(100) //
				.faultTolerant() //
				.retryLimit(3) //
				.retry(Exception.class) //
				.skipLimit(3) //
				.skip(Exception.class) // 捕捉到异常就重试,重试3次还是异常,JOB就停止并标志失败
				.reader(getIPv4PortScannerReader()) // 指定ItemReader
				.processor(getIPv4PortScannerProcessor()) // 指定ItemProcessor
				.writer(getIPv4PortScannerWriter()) // 指定ItemWriter
				.build();
	}
}
