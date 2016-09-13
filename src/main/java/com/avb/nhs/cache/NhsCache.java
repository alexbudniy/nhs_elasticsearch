package com.avb.nhs.cache;

import java.util.List;
import java.util.Optional;

/**
 * Nhs pages cache service interface.
 */
public interface NhsCache {

	void createOrUpdatePage(NhsPage page);

	Optional<NhsPage> getPageByUrl(String value);

	List<NhsPage> searchPages(String query);
}
