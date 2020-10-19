package uk.ac.ebi.uniprot.ds.importer.reader;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.FileNotFoundException;

import uk.ac.ebi.uniprot.ds.importer.reader.graph.OBOTerm;

/**
 * @author sahmad
 * @created 14/10/2020
 */
class MondoDiseaseReaderTest {

    @Test
    void testRead() throws FileNotFoundException {
        MondoDiseaseReader reader = new MondoDiseaseReader("src/test/resources/sample_mondo.obo");
        int expectedTermCount = 10;
        int actualTermCount = 0;
        OBOTerm term;
        while((term = reader.read()) != null){
            actualTermCount++;
            verifyMondoTerm(term);
        }
        Assertions.assertEquals(expectedTermCount, actualTermCount);
    }

    private void verifyMondoTerm(OBOTerm term) {
        Assertions.assertNotNull(term.getId());
        Assertions.assertNotNull(term.getName());
        Assertions.assertFalse(term.isObsolete());
        if(!term.getIsAs().isEmpty()){
            term.getIsAs().stream().forEach(isAs -> Assertions.assertTrue(isAs.startsWith("MONDO:")));
        }
        if(!term.getXrefs().isEmpty()){// in the form of OMIMS:1234
            term.getXrefs().stream().map(xref -> xref.split(":")).forEach(tokens -> Assertions.assertEquals(2, tokens.length));
        }

        if(!term.getAltIds().isEmpty()){
            term.getAltIds().stream().forEach(altId -> Assertions.assertTrue(altId.startsWith("MONDO:")));
        }
    }
}
