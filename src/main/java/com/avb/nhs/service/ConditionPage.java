package com.avb.nhs.service;

/**
 * DTO object which represents nhs condition page.
 */
public class ConditionPage {

	private String title;
	private String url;
	private String content;

	public ConditionPage(String title, String url, String content) {
		this.title = title;
		this.url = url;
		this.content = content;
	}

	public String getTitle() {
		return title;
	}

	public String getUrl() {
		return url;
	}

	public String getContent() {
		return content;
	}
}
