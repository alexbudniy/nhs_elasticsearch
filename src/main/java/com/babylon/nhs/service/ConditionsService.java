package com.babylon.nhs.service;

import java.util.List;

/**
 * Service interface to search for Nhs condition pages using search queries.
 */
public interface ConditionsService {

	List<ConditionPage> searchPages(String searchRequest);

}
