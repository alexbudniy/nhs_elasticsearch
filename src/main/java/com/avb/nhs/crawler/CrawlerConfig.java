package com.avb.nhs.crawler;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Config properties for web crawler. Values are loaded from application.yml
 */
@Component
@ConfigurationProperties(prefix="crawler")
public class CrawlerConfig {

	private String apiKey;

	private String agent;

	private int numOfThreads;

	private int crawlDelay;

	private String workFolder;

	private String baseUrl;

	private String startUrl;

	private Boolean enabled;

	public String getApiKey() {
		return apiKey;
	}

	public String getAgent() {
		return agent;
	}

	public int getNumOfThreads() {
		return numOfThreads;
	}

	public int getCrawlDelay() {
		return crawlDelay;
	}

	public String getWorkFolder() {
		return workFolder;
	}

	public String getBaseUrl() {
		return baseUrl;
	}

	public Boolean isEnabled() {
		return enabled;
	}

	public String getStartUrl() {
		return startUrl;
	}

	public void setApiKey(String apiKey) {
		this.apiKey = apiKey;
	}

	public void setAgent(String agent) {
		this.agent = agent;
	}

	public void setNumOfThreads(int numOfThreads) {
		this.numOfThreads = numOfThreads;
	}

	public void setCrawlDelay(int crawlDelay) {
		this.crawlDelay = crawlDelay;
	}

	public void setWorkFolder(String workFolder) {
		this.workFolder = workFolder;
	}

	public void setBaseUrl(String baseUrl) {
		this.baseUrl = baseUrl;
	}

	public void setEnabled(Boolean enabled) {
		this.enabled = enabled;
	}

	public void setStartUrl(String startUrl) {
		this.startUrl = startUrl;
	}
}
