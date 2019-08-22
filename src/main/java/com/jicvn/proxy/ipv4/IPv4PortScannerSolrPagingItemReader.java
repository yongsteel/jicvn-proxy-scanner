package com.jicvn.proxy.ipv4;

import java.util.concurrent.CopyOnWriteArrayList;

import org.springframework.batch.item.database.AbstractPagingItemReader;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.solr.core.query.SolrPageRequest;

import com.jicvn.proxy.entity.Proxy;
import com.jicvn.proxy.repository.ProxyRepository;

public class IPv4PortScannerSolrPagingItemReader extends AbstractPagingItemReader<Proxy> {
	
	private ProxyRepository repository;
	
	public IPv4PortScannerSolrPagingItemReader(ProxyRepository repository) {
		this.repository = repository;
	}

	@Override
	protected void doReadPage() {
		Pageable pageable = new SolrPageRequest(getPage(), getPageSize());
		Page<Proxy> page = repository.findByStatusIsNull(pageable);
		
		if (results == null) {
			results = new CopyOnWriteArrayList<>();
		}
		else {
			results.clear();
		}
		
		page.forEach(results::add);
	}

	@Override
	protected void doJumpToPage(int itemIndex) {
		
	}

}
