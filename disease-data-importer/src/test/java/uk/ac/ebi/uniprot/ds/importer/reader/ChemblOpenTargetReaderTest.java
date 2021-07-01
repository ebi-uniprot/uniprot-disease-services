package uk.ac.ebi.uniprot.ds.importer.reader;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import uk.ac.ebi.uniprot.ds.importer.model.ChemblEntry;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class ChemblOpenTargetReaderTest {
    private static final String SAMPLE_CHEMBL_FILE = "src/test/resources/sample_chembl_opentarget.json";
    private static ChemblOpenTargetReader READER;

    @BeforeAll
    static void setReader() throws IOException {
        READER = new ChemblOpenTargetReader(SAMPLE_CHEMBL_FILE);
    }
    @Test
    void testReadFile() throws Exception {
        ChemblEntry chemblEntry;
        int count = 0;
        while((chemblEntry = READER.read()) != null){
            verifyChemblEntry(chemblEntry);
            count++;
        }
        assertEquals(10, count);
    }

    private void verifyChemblEntry(ChemblEntry chemblEntry) {
        assertNotNull(chemblEntry.getChemblId(), "Chembl source id is null");
        assertNotNull(chemblEntry.getPhase(), "Phase is null");
        assertNotNull(chemblEntry.getTargetChemblId(), "Target chemblId is null");
    }
}