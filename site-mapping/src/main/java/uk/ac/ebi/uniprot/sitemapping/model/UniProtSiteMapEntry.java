package uk.ac.ebi.uniprot.sitemapping.model;

import java.util.List;

import lombok.Builder;
import lombok.Data;
import lombok.Singular;

/**
 *
 * @author jluo
 * @date: 03-Sep-2020
 *
*/
@Data
@Builder
public class UniProtSiteMapEntry {
	private final String accessions;
	@Singular private final List<UniProtSiteMap> uniProtSiteMaps;
}

