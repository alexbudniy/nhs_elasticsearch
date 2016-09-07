package com.babylon.nhs.service.impl;

import com.babylon.nhs.service.ConditionPage;
import com.babylon.nhs.cache.NhsCache;
import com.babylon.nhs.cache.NhsPage;
import com.babylon.nhs.service.ConditionsService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.inject.Inject;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;


/**
 * Nhs search conditions service implementation. Uses nhs pages cache to load pages and
 * convert them into DTO objects.
 */
@Service
public class ConditionsServiceImpl implements ConditionsService {

	private NhsCache nhsCache;

	@Inject
	public ConditionsServiceImpl(NhsCache nhsCache) {
		this.nhsCache = nhsCache;
	}

	@Override
	public List<ConditionPage> searchPages(String searchRequest) {
		if (StringUtils.isEmpty(searchRequest)) {
			return Collections.emptyList();
		}
		List<NhsPage> pages = nhsCache.searchPages(searchRequest);
		return pages.stream().map(this::toConditionPage).collect(Collectors.toList());
	}

	private ConditionPage toConditionPage(NhsPage nhsPage) {
		return new ConditionPage(nhsPage.getTitle(), nhsPage.getUrl(), nhsPage.getContent());
	}

}
