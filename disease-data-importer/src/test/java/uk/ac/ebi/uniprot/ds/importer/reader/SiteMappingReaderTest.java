package uk.ac.ebi.uniprot.ds.importer.reader;

import org.junit.jupiter.api.Test;
import org.springframework.batch.item.ParseException;
import uk.ac.ebi.uniprot.ds.common.model.SiteMapping;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author lgonzales
 * @since 10/09/2020
 */
class SiteMappingReaderTest {

    private static final String SAMPLE_SITE_MAPPING_FILE = "src/test/resources/sample_site_mapping.dat";
    private static final String SAMPLE_SITE_MAPPING_INVALID_FILE = "src/test/resources/sample_site_mapping_invalid.dat";

    @Test
    void testCanReadAllFile() throws Exception {
        SiteMappingReader reader = new SiteMappingReader(SAMPLE_SITE_MAPPING_FILE);
        SiteMapping siteMapping;
        int count = 0;
        while((siteMapping = reader.read()) != null){
            verifySiteMappingObj(siteMapping);
            count++;
        }

        assertEquals(14, count);
    }

    @Test
    void testInvalidReadFile() throws Exception {
        SiteMappingReader reader = new SiteMappingReader(SAMPLE_SITE_MAPPING_INVALID_FILE);
        ParseException error = assertThrows(ParseException.class, reader::read);
        assertNotNull(error);
        assertEquals("Unable to parse protein info 'INVALID'", error.getMessage());
    }

    @Test
    void testFirstLineSiteMappingFile() throws Exception {
        //Line Sample
        //rs1085307712	Q9Y243-2|AKT3_HUMAN:161	162	UniRef90_A0A5F9ZHU3	Q9WUA6-2|AKT3_MOUSE:161*
        SiteMappingReader reader = new SiteMappingReader(SAMPLE_SITE_MAPPING_FILE);
        SiteMapping siteMapping = reader.read();
        verifySiteMappingObj(siteMapping);
        assertEquals("rs1085307712", siteMapping.getSiteType(), "Site Type is invalid");
        assertEquals("Q9Y243-2", siteMapping.getAccession(), "accession is invalid");
        assertEquals("AKT3_HUMAN", siteMapping.getProteinId(), "Protein id is invalid");
        assertEquals(Long.valueOf(161), siteMapping.getSitePosition(), "Site Position is invalid");
        assertEquals(Long.valueOf(162), siteMapping.getPositionInAlignment(), "Position in Alignment is invalid");
        assertEquals("UniRef90_A0A5F9ZHU3", siteMapping.getUnirefId(), "Uniref Id is invalid");
        assertEquals("Q9WUA6-2|AKT3_MOUSE:161*", siteMapping.getMappedSite(), "Mapped Site is invalid");
    }

    private void verifySiteMappingObj(SiteMapping siteMapping) {
        assertNotNull(siteMapping, "Site Mapping is null");
        assertNotNull(siteMapping.getAccession(), "accession is null");
        assertNotNull(siteMapping.getProteinId(), "Protein id is null");
        assertNotNull(siteMapping.getPositionInAlignment(), "Position in Alignment is null");
        assertNotNull(siteMapping.getSitePosition(), "Site Position is null");
        assertNotNull(siteMapping.getUnirefId(), "Uniref Id is null");
        assertNotNull(siteMapping.getMappedSite(), "Mapped Site is null");
    }
}