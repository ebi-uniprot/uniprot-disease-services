package uk.ac.ebi.uniprot.sitemapping.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

/**
 *
 * @author jluo
 * @date: 03-Sep-2020
 *
*/

class UniProtSiteMapRowTest {
	@Test
	void testOf() {
		String data="rs147931650	O00170|AIP_HUMAN:101	104	UniRef90_O00170	Q5FWY5|AIP_RAT:101*;O08915|AIP_MOUSE:101*";
		UniProtSiteMapRow row = UniProtSiteMapRow.of(data);
		assertNotNull(row);
		assertEquals("rs147931650", row.getSiteType());
		assertEquals(101, row.getSitePosition());
		assertEquals(104, row.getPositionInAlignment());
		assertEquals("O00170", row.getAccession());
		assertEquals("AIP_HUMAN", row.getUniProtId());
		assertEquals("UniRef90_O00170", row.getUnirefId());
		assertEquals("Q5FWY5|AIP_RAT:101*;O08915|AIP_MOUSE:101*", row.getMappedSite());
		
	}
	@Test
	void testOf2() {
		String data="rs137853185;Natural variant	Q8WZ04|TOMT_HUMAN:81	82	UniRef90_A1Y9I9	A1Y9I9|TOMT_MOUSE:48;Q8WZ04-2|TOMT_HUMAN:41*";
		UniProtSiteMapRow row = UniProtSiteMapRow.of(data);
		assertNotNull(row);
		assertEquals("rs137853185;Natural variant", row.getSiteType());
		assertEquals(81, row.getSitePosition());
		assertEquals(82, row.getPositionInAlignment());
		assertEquals("Q8WZ04", row.getAccession());
		assertEquals("TOMT_HUMAN", row.getUniProtId());
		assertEquals("UniRef90_A1Y9I9", row.getUnirefId());
		assertEquals("A1Y9I9|TOMT_MOUSE:48;Q8WZ04-2|TOMT_HUMAN:41*", row.getMappedSite());
		
	}
}

