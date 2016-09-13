package com.avb.nhs.crawler;

import edu.uci.ics.crawler4j.crawler.CrawlConfig;
import edu.uci.ics.crawler4j.crawler.CrawlController;
import edu.uci.ics.crawler4j.fetcher.PageFetcher;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtConfig;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

/**
 * Crawler service, sets up a crawler, registers crawler handler {@link NhsConditionsCrawler} and starts
 * scheduled crawling.
 */
@Component
@EnableScheduling
public class CrawlerService {

	static final Logger LOG = LoggerFactory.getLogger(CrawlerService.class);

	private static final String API_KEY_PARAM = "?apikey=";

	private NhsConditionsCrawler crawler;
	private CrawlerConfig crawlerConfig;
	private CrawlController controller;

	@Inject
	public CrawlerService(NhsConditionsCrawler crawler, CrawlerConfig crawlerConfig) {
		this.crawler = crawler;
		this.crawlerConfig = crawlerConfig;
	}

	@PostConstruct
	public void init() throws Exception {

		CrawlConfig config = getCrawlConfig(crawlerConfig);

		PageFetcher pageFetcher = new PageFetcher(config);
		RobotstxtConfig robotstxtConfig = new RobotstxtConfig();
		robotstxtConfig.setEnabled(false);
		RobotstxtServer robotstxtServer = new RobotstxtServer(robotstxtConfig, pageFetcher);

		controller = new CrawlController(config, pageFetcher, robotstxtServer);
		controller.addSeed(getSeedUrl(crawlerConfig));
	}

	private String getSeedUrl(CrawlerConfig crawlerConfig) {
		return String.join("", crawlerConfig.getStartUrl(), API_KEY_PARAM, crawlerConfig.getApiKey());
	}

	private CrawlConfig getCrawlConfig(CrawlerConfig crawlerConfig) {

		CrawlConfig config = new CrawlConfig();
		config.setCrawlStorageFolder(crawlerConfig.getWorkFolder());
		config.setPolitenessDelay(crawlerConfig.getCrawlDelay());
		config.setUserAgentString(crawlerConfig.getAgent());
		return config;
	}

	@Scheduled(fixedRate = 24*60*60*1000)
	public void runCrawler() throws Exception {
		if (!crawlerConfig.isEnabled()) {
			return;
		}
		LOG.info("Started crawling..");
		controller.start(() -> crawler, crawlerConfig.getNumOfThreads());
		LOG.info("Finished crawling");
	}

}
