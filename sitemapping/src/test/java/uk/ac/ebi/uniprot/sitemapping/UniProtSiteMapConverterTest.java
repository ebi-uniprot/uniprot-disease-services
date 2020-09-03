package uk.ac.ebi.uniprot.sitemapping;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

import uk.ac.ebi.uniprot.sitemapping.model.FeatureType;
import uk.ac.ebi.uniprot.sitemapping.model.UniProtSite;
import uk.ac.ebi.uniprot.sitemapping.model.UniProtSiteMap;
import uk.ac.ebi.uniprot.sitemapping.model.UniProtSiteMapRow;
import uk.ac.ebi.uniprot.sitemapping.model.UniProtSiteMapRow.UniProtSiteMapRowBuilder;

/**
 *
 * @author jluo
 * @date: 03-Sep-2020
 *
*/

class UniProtSiteMapConverterTest {
	@Test
	void testConverter() {
		//rs141976414;Natural variant	O00303|EIF3F_HUMAN:232	237	UniRef90_O00303	Q9DCH4|EIF3F_MOUSE:236*;O88559-2|MEN1_MOUSE:184
		UniProtSiteMapRowBuilder builder = UniProtSiteMapRow.builder();
		builder.accession("O00303")
		.uniProtId("EIF3F_HUMAN")
		.sitePosition(232)
		.positionInAlignment(237)
		.unirefId("UniRef90_O00303")
		.siteType("rs141976414;Natural variant")
		.mappedSite("Q9DCH4|EIF3F_MOUSE:236*;O88559-2|MEN1_MOUSE:184");
	
		UniProtSiteMapRow row= builder.build();
		UniProtSiteMapConverter converter = new UniProtSiteMapConverter();
		UniProtSiteMap siteMap = converter.apply(row);
		assertEquals("O00303", siteMap.getAccession());
		assertEquals("EIF3F_HUMAN", siteMap.getUniProtId());
		assertEquals("UniRef90_O00303", siteMap.getUnirefId());
		assertEquals(232, siteMap.getSitePosition());
		assertEquals(237, siteMap.getPositionInAlignment());
		assertEquals(Arrays.asList(FeatureType.VARIANT), siteMap.getFeatureTypes());
		assertEquals(Arrays.asList("rs141976414"), siteMap.getDbSnps());
		UniProtSite site1=
		UniProtSite.builder()
		.accession("Q9DCH4")
		.uniProtId("EIF3F_MOUSE")
		.position(236)
		.isNew(true)
		.build();
		UniProtSite site2=
				UniProtSite.builder()
				.accession("O88559-2")
				.uniProtId("MEN1_MOUSE")
				.position(184)
				.isNew(false)
				.build();
		assertEquals(Arrays.asList(site1, site2), siteMap.getMappedSites());
		
	}
	@Test
	void testParseDataFile() throws Exception {
		String filename= "/site_mapping.dat";
		String fullpath =UniProtSiteFileReaderTest.class.getResource(filename).getFile();
		List<UniProtSiteMapRow> rows =UniProtSiteFileReader.read(fullpath);
		
		assertEquals(16118, rows.size());
		UniProtSiteMapConverter converter =new UniProtSiteMapConverter();
		List<UniProtSiteMap> siteMaps=
		rows.stream().map(converter).collect(Collectors.toList());
		assertEquals(16118, siteMaps.size());
	}
}

