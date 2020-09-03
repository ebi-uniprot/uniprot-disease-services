package uk.ac.ebi.uniprot.sitemapping.model;

import java.util.regex.Matcher;

import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author jluo
 * @date: 03-Sep-2020
 *
*/
@Data
@Builder
@Slf4j
public class UniProtSiteMapRow {
	private final String accession;
	private final String uniProtId;
	private final int sitePosition;
	private final int positionInAlignment;
	private final String unirefId;
	private final String siteType;
	private final String mappedSite;
	
	public static UniProtSiteMapRow of(String row) {
		String[] tokens = row.split("\t");
		if(tokens.length<5) {
			log.warn( "[" + row +"]" +" FAILED TO PARSE.");
			return null;
		}
		UniProtSiteMapRowBuilder builder =builder();
		builder.siteType(tokens[0])
		.positionInAlignment(Integer.parseInt(tokens[2]))
		.unirefId(tokens[3])
		.mappedSite(tokens[4]);
		Matcher matcher =UniProtSite.SITE_REG_PATTERN.matcher(tokens[1]);
		if(matcher.matches()) {
			builder.accession(matcher.group(1))
			.uniProtId(matcher.group(6))
			.sitePosition(Integer.parseInt(matcher.group(7)));
		}else {
			log.warn( "[" + row +"]" +" FAILED TO PARSE.");
			return null;
		}
		
		return builder.build();
	}
}

