package com.jicvn.proxy.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.solr.repository.SolrCrudRepository;

import com.jicvn.proxy.entity.Proxy;

public interface ProxyRepository extends SolrCrudRepository<Proxy, String> {

	Boolean existsByHost(String host);
	
	Page<Proxy> findByStatusIsNull(Pageable pageable);
	
	List<Proxy> findByStatus(String status);
}
