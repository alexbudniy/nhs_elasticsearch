package com.babylon.nhs.crawler;

import com.babylon.nhs.cache.NhsCache;
import com.babylon.nhs.cache.NhsPage;
import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.parser.HtmlParseData;
import edu.uci.ics.crawler4j.url.WebURL;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

/**
 * Crawler handler that determines which pages to crawl and stores pages with content into cache.
 * It crawls NHS syndication pages from here: {@see http://v1.syndication.nhschoices.nhs.uk/conditions}
 * For more details visit {@see http://www.nhs.uk/aboutNHSChoices/professionals/syndication/Pages/Webservices.aspx}
 */
@Component
public class NhsConditionsCrawler extends WebCrawler {

	static final Logger LOG = LoggerFactory.getLogger(NhsConditionsCrawler.class);

	private NhsCache nhsCache;
	private CrawlerConfig crawlerConfig;

	@Inject
	public NhsConditionsCrawler(NhsCache nhsCache, CrawlerConfig nhsConfig) {
		this.nhsCache = nhsCache;
		this.crawlerConfig = nhsConfig;
	}

	/**
	 * Visit only sub-pages of base page and ignore any links to "representations" page.
	 */
	@Override
	public boolean shouldVisit(Page referringPage, WebURL url) {
		String href = url.getURL().toLowerCase();
		return href.startsWith(crawlerConfig.getBaseUrl()) && !href.contains("representations");
	}

	@Override
	public void visit(Page page) {
		LOG.debug("Visiting page {}", page.getWebURL().getURL());

		if (page.getParseData() instanceof HtmlParseData) {
			HtmlParseData htmlParseData = (HtmlParseData) page.getParseData();

			String html = htmlParseData.getHtml();
			Document doc = Jsoup.parseBodyFragment(html);

			Elements originalLinkElem = doc.getElementsByAttributeValue("id", "original");
			if (originalLinkElem.size() > 0) {
				LOG.debug("Saving page {} into cache", page.getWebURL().getURL());

				Elements captionElem = doc.getElementsByTag("h1");
				Elements contentElem = doc.getElementsByAttributeValue("id", "content");

				NhsPage nhsPage = new NhsPage(captionElem.text(), originalLinkElem.attr("href"));
				nhsPage.setContent(contentElem.text());

				nhsCache.createOrUpdatePage(nhsPage);
			}
		}
	}
}
