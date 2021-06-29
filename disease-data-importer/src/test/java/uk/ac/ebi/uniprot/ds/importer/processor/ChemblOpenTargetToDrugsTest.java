package uk.ac.ebi.uniprot.ds.importer.processor;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import uk.ac.ebi.uniprot.ds.common.common.SourceType;
import uk.ac.ebi.uniprot.ds.common.dao.ProteinCrossRefDAO;
import uk.ac.ebi.uniprot.ds.common.model.Disease;
import uk.ac.ebi.uniprot.ds.common.model.Drug;
import uk.ac.ebi.uniprot.ds.common.model.Protein;
import uk.ac.ebi.uniprot.ds.common.model.ProteinCrossRef;
import uk.ac.ebi.uniprot.ds.importer.model.ChemblEntry;
import uk.ac.ebi.uniprot.ds.importer.writer.DiseaseWriterTest;

/**
 * @author sahmad
 * @created 19/10/2020
 */
@ExtendWith(SpringExtension.class)
public class ChemblOpenTargetToDrugsTest {

    private static final String OMIM_TO_EFO = "uniprot/omim2efo.txt";
    private ChemblOpenTargetToDrugs processor;
    private RestTemplate restTemplate;
    @MockBean
    private ProteinCrossRefDAO proteinCrossRefDAO;

    @BeforeEach
    void setUp() throws IOException {
        restTemplate = new RestTemplate();
        processor = new ChemblOpenTargetToDrugs(OMIM_TO_EFO, restTemplate);
    }

    @Test
    void testDrugWithoutHumDiseaseAndProtein() throws Exception {
        List<ProteinCrossRef> emptyList = Collections.emptyList();
        Mockito.when(this.proteinCrossRefDAO.findAllByDbType(SourceType.ChEMBL.name())).thenReturn(emptyList);
        processor.setProteinCrossRefDAO(this.proteinCrossRefDAO);
        ChemblEntry chemblEntry = createChemblOpenTarget();
        List<Drug> drugs = processor.process(chemblEntry);
        Assertions.assertEquals(1, drugs.size());
        verifyDrug(drugs.get(0));
        Assertions.assertEquals("http://www.ebi.ac.uk/efo/EFO_0000731", drugs.get(0).getChemblDiseaseId());
        Assertions.assertNull(drugs.get(0).getDisease());
        Assertions.assertNull(drugs.get(0).getProteinCrossRef());
    }

    @Test
    void testDrugWithHumDisease() throws Exception {
        // when
        Disease disease = DiseaseWriterTest.createDiseaseByDiseaseName("leukemia (disease)");
        Map<String, Disease> nameDisease = new HashMap<>();
        nameDisease.put("EFO:0000731", disease);
        processor.setDiseaseNameToDiseaseMap(nameDisease);
        List<ProteinCrossRef> emptyList = Collections.emptyList();
        Mockito.when(this.proteinCrossRefDAO.findAllByDbType(SourceType.ChEMBL.name())).thenReturn(emptyList);
        processor.setProteinCrossRefDAO(this.proteinCrossRefDAO);
        ChemblEntry openTarget = createChemblOpenTarget();
        List<Drug> drugs = processor.process(openTarget);
        Assertions.assertFalse(drugs.isEmpty());
        Assertions.assertEquals(1, drugs.size());
        verifyDrug(drugs.get(0));
        Assertions.assertEquals("http://www.ebi.ac.uk/efo/EFO_0000731", drugs.get(0).getChemblDiseaseId());
        Assertions.assertNotNull(drugs.get(0).getDisease());
        Assertions.assertEquals(disease, drugs.get(0).getDisease());
        Assertions.assertNull(drugs.get(0).getProteinCrossRef());
    }

    @Test
    void testDrugWithProtein() throws Exception {
        ProteinCrossRef xref = createProteinCrossRefObject();
        xref.setPrimaryId("CHEMBL208");
        List<ProteinCrossRef> xrefs = Arrays.asList(xref);
        Mockito.when(this.proteinCrossRefDAO.findAllByDbType(SourceType.ChEMBL.name())).thenReturn(xrefs);
        processor.setProteinCrossRefDAO(this.proteinCrossRefDAO);
        ChemblEntry openTarget = createChemblOpenTarget();
        List<Drug> drugs = processor.process(openTarget);
        Assertions.assertFalse(drugs.isEmpty());
        Assertions.assertEquals(1, drugs.size());
        Drug drug = drugs.get(0);
        Assertions.assertEquals("http://www.ebi.ac.uk/efo/EFO_0000731", drug.getChemblDiseaseId());
        // verify protein cross ref
        Assertions.assertEquals(xref, drug.getProteinCrossRef());
    }

    private void verifyDrug(Drug drug){
        Assertions.assertNotNull(drug);
        Assertions.assertNotNull("PF-05019702", drug.getName());
        Assertions.assertEquals(SourceType.ChEMBL.name(), drug.getSourceType());
        Assertions.assertEquals("CHEMBL3545239", drug.getSourceId());
        Assertions.assertEquals("Small molecule", drug.getMoleculeType());
        Assertions.assertEquals(Integer.valueOf(1), drug.getClinicalTrialPhase());
        Assertions.assertEquals(0, drug.getDrugEvidences().size());
        Assertions.assertEquals("Progesterone receptor antagonist", drug.getMechanismOfAction());
        Assertions.assertEquals("http://www.ebi.ac.uk/efo/EFO_0000731", drug.getChemblDiseaseId());
        Assertions.assertEquals("https://clinicaltrials.gov/search?id=%22NCT00427544%22OR%22NCT00444704%22OR%22NCT00543790%22", drug.getClinicalTrialLink());
        Assertions.assertNotNull(drug.getCreatedAt());
        Assertions.assertNotNull(drug.getUpdatedAt());
    }

    /*
    {"urls":[{"niceName":"ClinicalTrials","url":"https://clinicaltrials.gov/search?id=%22NCT00005780%22"}],"targetFromSourceId":"P04350",
    "datatypeId":"known_drug","studyStartDate":"2000-06-01","diseaseFromSource":"Lymphoma, Mantle Cell","datasourceId":"chembl","clinicalPhase":2,
    "drugId":"CHEMBL501867","clinicalStatus":"Active, not recruiting","diseaseFromSourceMappedId":"EFO_1001469","targetFromSource":"CHEMBL2095182"}
     */

    ChemblEntry createChemblOpenTarget(){
        ChemblEntry.ChemblEntryBuilder builder = ChemblEntry.builder();
        builder.chemblId("CHEMBL3545239");
        builder.diseaseUrl(ChemblEntry.convertToUrl("EFO_0000731"));
        builder.phase(1);
        builder.targetChemblId("CHEMBL208");
        builder.status("Completed");
        builder.clinicalTrialLink("https://clinicaltrials.gov/search?id=%22NCT00427544%22");
        return builder.build();
    }

    private ProteinCrossRef createProteinCrossRefObject() {
        String uuid = UUID.randomUUID().toString();
        ProteinCrossRef proteinCrossRef = new ProteinCrossRef();
        String pId = "PID-" + uuid;
        String desc = "DESC-" + uuid;
        String type = "TYPE-" + uuid;
        String iid = "IID-" + uuid;
        String t = "T-" + uuid;
        String f = "F-" + uuid;
        proteinCrossRef.setPrimaryId(pId);
        proteinCrossRef.setDescription(desc);
        proteinCrossRef.setDbType(type);
        proteinCrossRef.setIsoformId(iid);
        proteinCrossRef.setThird(t);
        proteinCrossRef.setFourth(f);
        proteinCrossRef.setProtein(createProtein());
        return proteinCrossRef;
    }

    public static Protein createProtein() {
        String random = UUID.randomUUID().toString();
        // create protein
        Protein protein = new Protein();
        String pId = "PID-" + random;
        String pn = "PN-" + random;
        String acc = "ACC-" + random;
        String gene = "GENE-" + random;
        String pDesc = "PDESC-" + random;

        protein.setProteinId(pId);
        protein.setName(pn);
        protein.setAccession(acc);
        protein.setGene(gene);
        protein.setDesc(pDesc);
        return protein;
    }
}
