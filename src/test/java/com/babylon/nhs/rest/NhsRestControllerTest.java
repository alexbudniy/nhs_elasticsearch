package com.babylon.nhs.rest;

import com.babylon.nhs.cache.NhsPage;
import com.babylon.nhs.elasticsearch.EsIndex;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import javax.inject.Inject;

import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

/**
 * Created by alex on 02/09/2016.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@WebAppConfiguration
public class NhsRestControllerTest {

	@Inject
	private WebApplicationContext webApplicationContext;

	@Inject
	private EsIndex conditionsIndex;

	private MockMvc mockMvc;

	@Before
	public void setup() throws Exception {
		this.mockMvc = webAppContextSetup(webApplicationContext).build();

		NhsPage nhsPage = new NhsPage( "Test page", "http://test.com");
		nhsPage.setContent("Some interesting content about London doctors");

		conditionsIndex.createOrUpdatePage(nhsPage);
	}

	@Test
	public void testSearchDocument() throws Exception {
		mockMvc.perform(get("/nhs/conditions/search?q=london doctors"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", hasSize(1)))
				.andExpect(jsonPath("$[0].title").value("Test page"))
				.andExpect(jsonPath("$[0].url").value("http://test.com"))
				.andExpect(jsonPath("$[0].content").value("Some interesting content about London doctors"));
	}

	@Test
	public void testSearchEmptyRequest() throws Exception {
		mockMvc.perform(get("/nhs/conditions/search?q="))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", hasSize(0)));
	}

}
