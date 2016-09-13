package com.avb.nhs.elasticsearch;

import com.avb.nhs.cache.NhsCache;
import com.avb.nhs.cache.NhsPage;
import com.google.gson.Gson;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.IndicesAdminClient;
import org.elasticsearch.search.SearchHit;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.elasticsearch.index.query.QueryBuilders.matchQuery;
import static org.elasticsearch.index.query.QueryBuilders.termQuery;

/**
 * Elasticsearch index for caching Nhs pages.
 */
public class EsIndex implements NhsCache {

	private final String indexName;
	private final String typeName;
	private final Client client;
	private final IndicesAdminClient indicesClient;
	private final Gson objectMapper;

	public EsIndex(String indexName, String typeName, Client client) {
		this.indexName = indexName;
		this.typeName = typeName;
		this.client = client;
		this.indicesClient = client.admin().indices();
		this.objectMapper = new Gson();
	}

	@Override
	public void createOrUpdatePage(NhsPage page) {
		Optional<NhsPage> existingPage = getPageByUrl(page.getUrl());
		if (existingPage.isPresent()) {
			String pageId = existingPage.get().getId();
			client.prepareUpdate(indexName, typeName, pageId)
					.setDoc(objectMapper.toJson(page))
					.get();
		} else {
			client.prepareIndex(indexName, typeName)
					.setSource(objectMapper.toJson(page))
					.get();
		}
		flush();
	}

	@Override
	public Optional<NhsPage> getPageByUrl(String value) {
		SearchResponse searchResponse = client.prepareSearch(indexName)
				.setTypes(typeName)
				.setQuery(termQuery("url", value))
				.get();

		SearchHit[] hits = searchResponse.getHits().getHits();
		if (hits.length > 0) {
			NhsPage page = objectMapper.fromJson(hits[0].getSourceAsString(), NhsPage.class);
			page.setId(hits[0].getId());
			return Optional.of(page);
		}
		return Optional.empty();
	}

	@Override
	public List<NhsPage> searchPages(String query) {
		SearchResponse searchResponse = client.prepareSearch(indexName)
				.setTypes(typeName)
				.setQuery(matchQuery("content", query))
				.get();

		SearchHit[] hits = searchResponse.getHits().getHits();

		return Arrays.stream(hits)
				.map(SearchHit::getSourceAsString)
				.map(source -> objectMapper.fromJson(source, NhsPage.class))
				.collect(Collectors.toList());
	}

	private void flush() {
		indicesClient.prepareFlush().get();
	}

	// ADMIN index methods, which can be extracted into a separate admin class

	public void clear() {
		dropIndex();
		createIndex();
	}

	public void createIndex() {
		indicesClient.prepareCreate(indexName).get();
		flush();
	}

	public void dropIndex() {
		indicesClient.prepareDelete(indexName).get();
	}

	public boolean isIndexExist() {
		return indicesClient.prepareExists(indexName).get().isExists();
	}

}
