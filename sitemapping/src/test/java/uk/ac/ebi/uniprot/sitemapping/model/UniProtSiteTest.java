package uk.ac.ebi.uniprot.sitemapping.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.regex.Matcher;

import org.junit.jupiter.api.Test;

/**
 *
 * @author jluo
 * @date: 03-Sep-2020
 *
*/

 class UniProtSiteTest {
	@Test
	void testOfWithNew() {
		String data ="Q6ZQ08-2|CNOT1_MOUSE:1203*";
		UniProtSite site = UniProtSite.of(data);
		assertEquals("Q6ZQ08-2", site.getAccession());
		assertEquals("CNOT1_MOUSE", site.getUniProtId());
		assertEquals(1203, site.getPosition());
		assertTrue(site.isNew());
	}
	
	@Test
	void testOfNoNew() {	
		String data ="Q6ZQ08|CNOT1_MOUSE:1203";
		UniProtSite site = UniProtSite.of(data);
		assertEquals("Q6ZQ08", site.getAccession());
		assertEquals("CNOT1_MOUSE", site.getUniProtId());
		assertEquals(1203, site.getPosition());
		assertFalse(site.isNew());
	}
	@Test
	void testOfWrongData() {	
		String data ="Q6ZQ08CNOT1_MOUSE:1203";
		UniProtSite site = UniProtSite.of(data);
		assertNull(site);
	}
}

