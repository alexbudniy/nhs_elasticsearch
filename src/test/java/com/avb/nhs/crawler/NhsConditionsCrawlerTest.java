package com.avb.nhs.crawler;

import com.avb.nhs.cache.NhsCache;
import com.avb.nhs.cache.NhsPage;
import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.parser.HtmlParseData;
import edu.uci.ics.crawler4j.url.WebURL;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.ArgumentCaptor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by alex on 03/09/2016.
 */
@RunWith(JUnit4.class)
public class NhsConditionsCrawlerTest {

	private NhsConditionsCrawler crawler;

	private NhsCache nhsCache;

	private CrawlerConfig config;

	private final String CONTENT_PAGE = "<html><head><title>NHS Choices Syndication</title></head>"
			+ "<body><h1>Braces (dental)</h1><div>"
			+ "<a id=\"original\" href=\"http://www.nhs.uk/\">[Original article on NHS Choices website]</a></div>"
			+ "    <div id=\"content\">"
			+ "    <p><strong>Orthodontic treatment is most commonly used to improve the appearance.</strong></p>"
			+ "   </div>"
			+ "</body></html>";

	@Before
	public void setUp() {

		this.config = new CrawlerConfig();
		this.config.setBaseUrl("http://baseurl.com/articles");
		this.nhsCache = mock(NhsCache.class);
		this.crawler = new NhsConditionsCrawler(nhsCache, this.config);
	}

	@Test
	public void testIgnoresRepresentationsLinks() {

		WebURL webUrl = new WebURL();
		webUrl.setURL(this.config.getBaseUrl() + "?what=representations");

		Assert.assertFalse(crawler.shouldVisit(null, webUrl));
	}

	@Test
	public void testIgnoresUrlWhichIsNotSubPageOfBaseUrl() {

		WebURL webUrl = new WebURL();
		webUrl.setURL("http://baseurl.com/");

		Assert.assertFalse(crawler.shouldVisit(null, webUrl));
	}

	@Test
	public void testShouldVisitAnySubPagesOfBaseUrl() {

		WebURL webUrl = new WebURL();
		webUrl.setURL("http://baseurl.com/articles/some-topic");

		Assert.assertTrue(crawler.shouldVisit(null, webUrl));
	}

	@Test
	public void testShouldExtractPageCorrectly() {

		Page visitPage = mock(Page.class);
		when(visitPage.getWebURL()).thenReturn(mock(WebURL.class));

		HtmlParseData parserData = mock(HtmlParseData.class);
		when(parserData.getHtml()).thenReturn(CONTENT_PAGE);
		when(visitPage.getParseData()).thenReturn(parserData);

		crawler.visit(visitPage);

		ArgumentCaptor<NhsPage> pageCaptor = ArgumentCaptor.forClass(NhsPage.class);
		verify(nhsCache).createOrUpdatePage(pageCaptor.capture());

		NhsPage actualPage = pageCaptor.getValue();
		assertThat(actualPage.getTitle()).isEqualTo("Braces (dental)");
		assertThat(actualPage.getUrl()).isEqualTo("http://www.nhs.uk/");
		assertThat(actualPage.getContent()).isEqualTo("Orthodontic treatment is most commonly used to improve the appearance.");
	}
}