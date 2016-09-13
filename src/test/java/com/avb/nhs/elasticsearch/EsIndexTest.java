package com.avb.nhs.elasticsearch;

import com.avb.nhs.cache.NhsPage;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.inject.Inject;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertTrue;

/**
 * Created by alex on 03/09/2016.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class EsIndexTest {

	@Inject
	private EsIndex esIndex;

	@Before
	public void setUp() {
		esIndex.clear();
	}

	@Test
	public void testSavePage() {

		NhsPage expectedPage = new NhsPage("Test title", "http://test.com");
		expectedPage.setContent("Some test content");
		esIndex.createOrUpdatePage(expectedPage);

		Optional<NhsPage> actualPage = esIndex.getPageByUrl(expectedPage.getUrl());
		assertTrue(actualPage.isPresent());
		assertThat(expectedPage).isEqualToIgnoringGivenFields(actualPage.get(), "id");
	}

	@Test
	public void testUpdatePage() {

		NhsPage expectedPage = new NhsPage("Test title", "http://test.com");
		expectedPage.setContent("Some test content");
		esIndex.createOrUpdatePage(expectedPage);

		expectedPage.setTitle("Updated title");
		expectedPage.setContent("Updated content");
		esIndex.createOrUpdatePage(expectedPage);

		Optional<NhsPage> actualPage = esIndex.getPageByUrl(expectedPage.getUrl());
		assertThat(expectedPage).isEqualToIgnoringGivenFields(actualPage.get(), "id");
	}

	@Test
	public void testSearchPageByContent() {

		NhsPage page1 = new NhsPage("Knock knees", "http://knees.nhs.com");
		page1.setContent("A person with knock knees has a large gap between their feet when they're standing "
				+ "with their knees together.");
		esIndex.createOrUpdatePage(page1);

		NhsPage page2 = new NhsPage("Genetics", "http://genetics.nhs.com");
		page2.setContent("Genes are packaged in bundles called chromosomes. In humans, each cell in the body contains "
				+ "23 pairs of chromosomes - 46 in total.");
		esIndex.createOrUpdatePage(page2);

		List<NhsPage> actualPages = esIndex.searchPages("Something with knee?");
		assertThat(actualPages).hasSize(1);
		assertThat(actualPages.get(0)).isEqualToIgnoringGivenFields(page1, "id");

		actualPages = esIndex.searchPages("What about chromosomes?");
		assertThat(actualPages).hasSize(1);
		assertThat(actualPages.get(0)).isEqualToIgnoringGivenFields(page2, "id");

		actualPages = esIndex.searchPages("Anything about a dog?");
		assertThat(actualPages).hasSize(0);
	}

}