package com.avb.nhs.service.impl;

import com.avb.nhs.cache.NhsPage;
import com.avb.nhs.elasticsearch.EsIndex;
import com.avb.nhs.service.ConditionPage;
import com.avb.nhs.elasticsearch.EsConfig;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by alex on 03/09/2016.
 */
@RunWith(JUnit4.class)
public class ConditionsServiceImplTest {

	public static final String TYPE_NAME = "conditions";
	public static final String SEARCH_QUERY = "London";

	private ConditionsServiceImpl nhsService;

	private EsIndex conditionsIndex;

	@Before
	public void setUp() throws IOException {

		conditionsIndex = mock(EsIndex.class);
		EsConfig esConfig = new EsConfig();
		esConfig.setTypeName(TYPE_NAME);
		nhsService = new ConditionsServiceImpl(conditionsIndex);
	}

	@Test
	public void testSearchPages() {

		List<NhsPage> pages = new ArrayList<>();
		NhsPage nhsPage = new NhsPage("London NHS", "http://test.com");
		nhsPage.setContent("Some interesting content");
		pages.add(nhsPage);
		nhsPage = new NhsPage("UK NHS", "http://test.com");
		nhsPage.setContent("More interesting content");
		pages.add(nhsPage);

		when(conditionsIndex.searchPages(SEARCH_QUERY)).thenReturn(pages);

		List<ConditionPage> actualConditionPages = nhsService.searchPages(SEARCH_QUERY);

		List<ConditionPage> expectedConditionPages = pages.stream()
				.map(page -> new ConditionPage(page.getTitle(), page.getUrl(), page.getContent()))
				.collect(Collectors.toList());

		assertThat(actualConditionPages).hasSize(2);
		assertThat(actualConditionPages).usingFieldByFieldElementComparator().containsAll(expectedConditionPages);
	}

}