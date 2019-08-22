package com.jicvn.proxy.entity;

import org.apache.solr.client.solrj.beans.Field;
import org.springframework.data.annotation.Id;
import org.springframework.data.solr.core.mapping.SolrDocument;

//LAST UPDATE	IP ADDRESS	PORT	ANONYMITY LEVEL	COUNTRY	CITY	UPTIME (L/D)	RESPONSE TIMES

@SolrDocument(collection = "proxy")
public class Proxy {

	@Id
	@Field
	private String id;

	@Field
	private Integer cityCode;

	@Field
	private String host;
	
	@Field
	private Integer port;

	// 匿名类型
	@Field
	private String anonyLevel;

	// 协议 socket，http，https
	@Field
	private String protocol;

	// 更新时间
	@Field
	private Integer updateDatetime;

	// 响应时间
	@Field
	private Integer responseTime;

	// 累计请求次数
	@Field
	private Integer requestCount;

	// 成功次数
	@Field
	private Integer successCount;

	@Field
	private String status;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Integer getCityCode() {
		return cityCode;
	}

	public void setCityCode(Integer cityCode) {
		this.cityCode = cityCode;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public Integer getPort() {
		return port;
	}

	public void setPort(Integer port) {
		this.port = port;
	}

	public String getAnonyLevel() {
		return anonyLevel;
	}

	public void setAnonyLevel(String anonyLevel) {
		this.anonyLevel = anonyLevel;
	}

	public String getProtocol() {
		return protocol;
	}

	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}

	public Integer getUpdateDatetime() {
		return updateDatetime;
	}

	public void setUpdateDatetime(Integer updateDatetime) {
		this.updateDatetime = updateDatetime;
	}

	public Integer getResponseTime() {
		return responseTime;
	}

	public void setResponseTime(Integer responseTime) {
		this.responseTime = responseTime;
	}

	public Integer getRequestCount() {
		return requestCount;
	}

	public void setRequestCount(Integer requestCount) {
		this.requestCount = requestCount;
	}

	public Integer getSuccessCount() {
		return successCount;
	}

	public void setSuccessCount(Integer successCount) {
		this.successCount = successCount;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

}
