package com.jicvn.proxy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class Application {

	private final static Logger logger = LoggerFactory.getLogger(Application.class);

	public static void main(String[] args) {
		SpringApplication.run(Application.class);
	}

	@Autowired
	private JobLauncher jobLauncher;
	
	@Autowired
	private Job handleIPv4PortScannerJob;
	
	@Bean
	public CommandLineRunner LauncherIPv4PortScannerJob() {
		return (args) -> {
			Thread thread = new Thread(()->{
				logger.info("IPv4 Port Scanner Job Launcher");
				try {
					jobLauncher.run(handleIPv4PortScannerJob, new JobParameters());
				} catch (Exception e) {
					e.printStackTrace();
				}
			});
			thread.setDaemon(true);
			thread.start();
		};
	}
}