package uk.ac.ebi.uniprot.sitemapping.model;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author jluo
 * @date: 02-Sep-2020
 *
*/
@Data
@Builder
@Slf4j
public class UniProtSite{
	private final String accession;
	private final String uniProtId;
	private final int position;
	private final boolean isNew;	
	
	@Override
	public String toString() {
		StringBuilder sb=new StringBuilder();
		sb.append(accession).append("|").append(uniProtId).append(":").append(position);
		if(isNew)
			sb.append("*");
		return sb.toString();
	}
	 private static final String UNIPROTKB_ACCESSION_REGEX =
	            "([OPQ][0-9][A-Z0-9]{3}[0-9]|[A-NR-Z]([0-9][A-Z][A-Z0-9]{2}){1,2}[0-9])(-[0-9]+)?";
	 private static final String SITE_REG = "("+ UNIPROTKB_ACCESSION_REGEX+")(\\|)([^:]+):([0-9]+)(\\*)?";
	 public static final Pattern SITE_REG_PATTERN = Pattern.compile(SITE_REG);
	
	public static UniProtSite of(String data) {
		Matcher matcher =UniProtSite.SITE_REG_PATTERN.matcher(data);
		if(matcher.matches()) {
			UniProtSiteBuilder builder= builder();
			builder.accession(matcher.group(1))
			.uniProtId(matcher.group(6))
			.position(Integer.parseInt(matcher.group(7)))
			.isNew((matcher.group(8)!=null));
			return builder.build();
		}else {
			log.warn( "[" + data +"]" +" FAILED TO PARSE.");
			return null;
		}
		
	}
}

