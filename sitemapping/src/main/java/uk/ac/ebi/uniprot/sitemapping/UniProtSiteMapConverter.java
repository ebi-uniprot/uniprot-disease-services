package uk.ac.ebi.uniprot.sitemapping;

import java.util.Arrays;
import java.util.function.Function;
import java.util.stream.Collectors;

import uk.ac.ebi.uniprot.sitemapping.model.FeatureType;
import uk.ac.ebi.uniprot.sitemapping.model.UniProtSite;
import uk.ac.ebi.uniprot.sitemapping.model.UniProtSiteMap;
import uk.ac.ebi.uniprot.sitemapping.model.UniProtSiteMapRow;

/**
 *
 * @author jluo
 * @date: 03-Sep-2020
 *
 */

public class UniProtSiteMapConverter implements Function<UniProtSiteMapRow, UniProtSiteMap> {

	private static final String RS = "rs";

	@Override
	public UniProtSiteMap apply(UniProtSiteMapRow t) {
		UniProtSiteMap.UniProtSiteMapBuilder builder = UniProtSiteMap.builder();
		builder.accession(t.getAccession()).uniProtId(t.getUniProtId()).unirefId(t.getUnirefId())
				.sitePosition(t.getSitePosition())
				.positionInAlignment(t.getPositionInAlignment());

		String siteType = t.getSiteType();
		String tokens[] = siteType.split(";");
		for (String token : tokens) {
			if(token.isEmpty())
				continue;
			if (token.startsWith(RS)) {
				builder.dbSnp(token);
			} else {
				builder.featureType(FeatureType.type(token));
			}
		}
		String sites = t.getMappedSite();
		String mappedSites[] = sites.split(";");
		builder.mappedSites(Arrays.stream(mappedSites)
				.map(UniProtSite::of)
				.filter(val -> val != null)
				.collect(Collectors.toList()));
		return builder.build();
	}

}
