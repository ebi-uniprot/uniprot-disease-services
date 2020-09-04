package uk.ac.ebi.uniprot.sitemapping.model;

import java.util.List;

import lombok.Builder;
import lombok.Data;
import lombok.Singular;

/**
 *
 * @author jluo
 * @date: 02-Sep-2020
 *
*/
@Data
@Builder
public class UniProtSiteMap {
	private final String accession;
	private final String uniProtId;
	private final int sitePosition;
	private final int positionInAlignment;
	@Singular private final List<FeatureType> featureTypes;
	@Singular private final List<String> dbSnps;
	@Singular private final List<UniProtSite> mappedSites;
	private final String unirefId;
}

