package com.babylon.nhs.elasticsearch;

import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.node.Node;
import org.elasticsearch.node.NodeBuilder;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

/**
 * Factory to create embedded Elasticsearch node and index. Loads template with mapping
 * for indexing page content for full text search.
 */
@Component
public class ElasticIndexFactoryBean implements FactoryBean<EsIndex>, DisposableBean {

	private EsConfig esConfig;
	private Node node;

	@Inject
	public ElasticIndexFactoryBean(EsConfig esConfig) {
		this.esConfig = esConfig;
	}

	@Override
	public EsIndex getObject() throws Exception {
		createEsNode();
		loadTemplates();
		return createEsIndex();
	}

	@Override
	public Class getObjectType() {
		return EsIndex.class;
	}

	@Override
	public boolean isSingleton() {
		return true;
	}

	private void createEsNode() {
		Settings nodeSettings = Settings.settingsBuilder()
				.put("node.name", esConfig.getNodeName())
				.put("path.home", esConfig.getDbPath())
				.put("http.enabled", false)
				.build();

		node = NodeBuilder.nodeBuilder()
				.settings(nodeSettings)
				.data(true)
				.local(true)
				.node();
	}

	private void loadTemplates() throws IOException {
		node.client()
				.admin()
				.indices()
				.preparePutTemplate(esConfig.getIndexName())
				.setSource(readConditionsTemplate())
				.get();
	}

	private String readConditionsTemplate() throws IOException {
		Resource resource = new ClassPathResource("conditions_template.json");
		try (BufferedReader buffer = new BufferedReader(new InputStreamReader(resource.getInputStream()))) {
			return buffer.lines().collect(Collectors.joining("\n"));
		}
	}

	public EsIndex createEsIndex() {
		EsIndex esIndex = new EsIndex(esConfig.getIndexName(), esConfig.getTypeName(), node.client());
		if (!esIndex.isIndexExist()) {
			esIndex.createIndex();
		}
		return esIndex;
	}

	@Override
	public void destroy() throws Exception {
		node.client().close();
		node.close();
	}
}