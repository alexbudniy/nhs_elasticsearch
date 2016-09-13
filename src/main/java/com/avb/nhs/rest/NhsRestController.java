package com.avb.nhs.rest;

import com.avb.nhs.service.ConditionPage;
import com.avb.nhs.service.ConditionsService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;
import java.util.List;

/**
 * REST api to search for Nhs medical conditions pages.
 */
@RestController
@RequestMapping("/nhs/conditions")
public class NhsRestController {

	private ConditionsService conditionsService;

	@Inject
	public NhsRestController(ConditionsService conditionsService) {
		this.conditionsService = conditionsService;
	}

	@RequestMapping(value = "/search", method = RequestMethod.GET)
	public List<ConditionPage> search(@RequestParam(value="q") String searchQuery) {
		return conditionsService.searchPages(searchQuery);
	}
}