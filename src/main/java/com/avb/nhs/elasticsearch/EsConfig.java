package com.avb.nhs.elasticsearch;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Config properties for elasticsearch index. Values are loaded from application.yml
 */
@Component
@ConfigurationProperties(prefix="elasticsearch")
public class EsConfig {

	private String dbPath;

	private String nodeName;

	private String indexName;

	private String typeName;

	public String getDbPath() {
		return dbPath;
	}

	public String getNodeName() {
		return nodeName;
	}

	public String getIndexName() {
		return indexName;
	}

	public String getTypeName() {
		return typeName;
	}

	public void setDbPath(String dbPath) {
		this.dbPath = dbPath;
	}

	public void setNodeName(String nodeName) {
		this.nodeName = nodeName;
	}

	public void setIndexName(String indexName) {
		this.indexName = indexName;
	}

	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}
}
