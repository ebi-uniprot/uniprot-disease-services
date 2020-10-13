package uk.ac.ebi.uniprot.ds.importer.reader;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import uk.ac.ebi.uniprot.ds.importer.model.ChemblOpenTarget;
import uk.ac.ebi.uniprot.ds.importer.reader.ChemblOpenTargetReader;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class ChemblOpenTargetReaderTest {

    private static final String SAMPLE_CHEMBL_FILE = "src/test/resources/sample_chembl_opentarget.json";
    private static ChemblOpenTargetReader READER;

    @BeforeAll
    static void setReader() throws IOException {
        READER = new ChemblOpenTargetReader(SAMPLE_CHEMBL_FILE);
    }
    @Test
    void testReadFile() throws Exception {
        ChemblOpenTarget openTargetObj;
        int count = 0;
        while((openTargetObj = READER.read()) != null){
            verifyOpenTargetObj(openTargetObj);
            count++;
        }

        assertEquals(10, count);
    }

    private void verifyOpenTargetObj(ChemblOpenTarget openTarget) {

        assertNotNull(openTarget.getChemblSourceUrl(), "Chembl source id is null");
        assertNotNull(openTarget.getChemblTargetUrl(), "Chembl Target id is null");
        assertNotNull(openTarget.getMoleculeName(), "molecule Name is null");
        assertNotNull(openTarget.getMoleculeType(), "molecule type is null");

        if(!"INDOMETHACIN".equalsIgnoreCase(openTarget.getMoleculeName())
                && !"METOPROLOL".equalsIgnoreCase(openTarget.getMoleculeName())
                && !"ACETAZOLAMIDE".equalsIgnoreCase(openTarget.getMoleculeName())) {
            assertNotNull(openTarget.getClinicalTrialLink(), "clinical trial link is null");
        }

        assertNotNull(openTarget.getClinicalTrialPhase(), "clinical trial phase is null");
        assertNotNull(openTarget.getMechOfAction(), "mech of action is null");

        if(!"INDOMETHACIN".equalsIgnoreCase(openTarget.getMoleculeName())
                && !"BICALUTAMIDE".equalsIgnoreCase(openTarget.getMoleculeName())
                && !"METOPROLOL".equalsIgnoreCase(openTarget.getMoleculeName())
                && !"SIROLIMUS".equalsIgnoreCase(openTarget.getMoleculeName())) {
            assertNotNull(openTarget.getDrugEvidences(), "Drug evidences is null");
        }
    }

}