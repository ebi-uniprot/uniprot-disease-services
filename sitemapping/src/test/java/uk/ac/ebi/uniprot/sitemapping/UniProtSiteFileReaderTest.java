package uk.ac.ebi.uniprot.sitemapping;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;

import uk.ac.ebi.uniprot.sitemapping.model.UniProtSiteMapRow;

/**
 *
 * @author jluo
 * @date: 03-Sep-2020
 *
*/

 class UniProtSiteFileReaderTest {
	@Test
	void testParseDataFile() throws Exception {
		String filename= "/site_mapping.dat";
		String fullpath =UniProtSiteFileReaderTest.class.getResource(filename).getFile();
		List<UniProtSiteMapRow> rows =UniProtSiteFileReader.read(fullpath);
		
		assertEquals(16118, rows.size());
		
	}
}

