package uk.ac.ebi.uniprot.ds.importer.processor;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import uk.ac.ebi.uniprot.ds.common.common.SourceType;
import uk.ac.ebi.uniprot.ds.common.dao.ProteinCrossRefDAO;
import uk.ac.ebi.uniprot.ds.common.model.Disease;
import uk.ac.ebi.uniprot.ds.common.model.Drug;
import uk.ac.ebi.uniprot.ds.common.model.DrugEvidence;
import uk.ac.ebi.uniprot.ds.common.model.Protein;
import uk.ac.ebi.uniprot.ds.common.model.ProteinCrossRef;
import uk.ac.ebi.uniprot.ds.importer.model.ChemblOpenTarget;
import uk.ac.ebi.uniprot.ds.importer.util.Constants;
import uk.ac.ebi.uniprot.ds.importer.writer.DiseaseWriterTest;

/**
 * @author sahmad
 * @created 19/10/2020
 */
@ExtendWith(SpringExtension.class)
public class ChemblOpenTargetToDrugsTest {
    private static final String OMIM_TO_EFO = "uniprot/omim2efo.txt";
    private static ChemblOpenTargetToDrugs processor;
    @MockBean
    private ProteinCrossRefDAO proteinCrossRefDAO;

    @BeforeAll
    static void setReader() throws IOException {
        processor = new ChemblOpenTargetToDrugs(OMIM_TO_EFO);
    }

    @Test
    void testDrugWithoutHumDiseaseAndProtein() throws Exception {
        List<ProteinCrossRef> emptyList = Collections.emptyList();
        Mockito.when(this.proteinCrossRefDAO.findAllByDbType(SourceType.ChEMBL.name())).thenReturn(emptyList);
        processor.setProteinCrossRefDAO(this.proteinCrossRefDAO);
        ChemblOpenTarget openTarget = createChemblOpenTarget();
        List<Drug> drugs = processor.process(openTarget);
        Assertions.assertFalse(drugs.isEmpty());
        Assertions.assertEquals(1, drugs.size());
        verifyDrug(drugs.get(0));
        Assertions.assertEquals("sample disease id efo url", drugs.get(0).getChemblDiseaseId());
        Assertions.assertNull(drugs.get(0).getDisease());
        Assertions.assertNull(drugs.get(0).getProteinCrossRef());
    }

    @Test
    void testDrugWithHumDisease() throws Exception {
        // when
        Disease disease = DiseaseWriterTest.createDiseaseByDiseaseName("leukemia (disease)");
        Map<String, Disease> nameDisease = new HashMap<>();
        nameDisease.put("OMIM:101850", disease);
        processor.setDiseaseNameToDiseaseMap(nameDisease);
        List<ProteinCrossRef> emptyList = Collections.emptyList();
        Mockito.when(this.proteinCrossRefDAO.findAllByDbType(SourceType.ChEMBL.name())).thenReturn(emptyList);
        processor.setProteinCrossRefDAO(this.proteinCrossRefDAO);
        ChemblOpenTarget openTarget = createChemblOpenTarget();
        openTarget.setDiseaseId("http://www.ebi.ac.uk/efo/EFO_1000758");
        List<Drug> drugs = processor.process(openTarget);
        Assertions.assertFalse(drugs.isEmpty());
        Assertions.assertEquals(1, drugs.size());
        verifyDrug(drugs.get(0));
        Assertions.assertEquals("http://www.ebi.ac.uk/efo/EFO_1000758", drugs.get(0).getChemblDiseaseId());
        Assertions.assertNotNull(drugs.get(0).getDisease());
        Assertions.assertEquals(disease, drugs.get(0).getDisease());
        Assertions.assertNull(drugs.get(0).getProteinCrossRef());
    }

    @Test
    void testDrugWithProtein() throws Exception {
        ProteinCrossRef xref = createProteinCrossRefObject();
        xref.setPrimaryId("sample target url".toUpperCase());
        List<ProteinCrossRef> xrefs = Arrays.asList(xref);
        Mockito.when(this.proteinCrossRefDAO.findAllByDbType(SourceType.ChEMBL.name())).thenReturn(xrefs);
        processor.setProteinCrossRefDAO(this.proteinCrossRefDAO);
        ChemblOpenTarget openTarget = createChemblOpenTarget();
        List<Drug> drugs = processor.process(openTarget);
        Assertions.assertFalse(drugs.isEmpty());
        Assertions.assertEquals(1, drugs.size());
        Drug drug = drugs.get(0);
        Assertions.assertEquals("sample disease id efo url", drug.getChemblDiseaseId());
        verifyDrug(drugs.get(0));
        Assertions.assertEquals("sample disease id efo url", drug.getChemblDiseaseId());
        Assertions.assertNull(drug.getDisease());
        Assertions.assertNull(drug.getDisease());
        // verify protein cross ref
        Assertions.assertEquals(xref, drug.getProteinCrossRef());
    }

    @Test
    void testDrugWithAmbiguousEFOToOMIMMapping() throws Exception {
        // when
        Disease disease = DiseaseWriterTest.createDiseaseByDiseaseName("leukemia (disease)");
        Map<String, Disease> nameDisease = new HashMap<>();
        nameDisease.put("OMIM:101850", disease);
        processor.setDiseaseNameToDiseaseMap(nameDisease);
        List<ProteinCrossRef> emptyList = Collections.emptyList();
        Mockito.when(this.proteinCrossRefDAO.findAllByDbType(SourceType.ChEMBL.name())).thenReturn(emptyList);
        processor.setProteinCrossRefDAO(this.proteinCrossRefDAO);
        ChemblOpenTarget openTarget = createChemblOpenTarget();
        openTarget.setDiseaseId("http://www.ebi.ac.uk/efo/EFO_0000095");
        List<Drug> drugs = processor.process(openTarget);
        Assertions.assertFalse(drugs.isEmpty());
        Assertions.assertEquals(1, drugs.size());
        verifyDrug(drugs.get(0));
        Assertions.assertEquals("http://www.ebi.ac.uk/efo/EFO_0000095", drugs.get(0).getChemblDiseaseId());
        Assertions.assertNull(drugs.get(0).getDisease());
        Assertions.assertNull(drugs.get(0).getProteinCrossRef());
    }

    
    private void verifyDrug(Drug drug){
        Assertions.assertNotNull(drug);
        Assertions.assertNotNull("sample molecule name", drug.getName());
        Assertions.assertEquals(SourceType.ChEMBL.name(), drug.getSourceType());
        Assertions.assertEquals("sample source url", drug.getSourceId());
        Assertions.assertEquals("sample molecule type", drug.getMoleculeType());
        Assertions.assertEquals(Integer.valueOf(2), drug.getClinicalTrialPhase());
        Assertions.assertEquals(4, drug.getDrugEvidences().size());
        drug.getDrugEvidences().stream().map(DrugEvidence::getRefType).forEach(str -> Assertions.assertEquals(Constants.PUBMED_STR, str));
        drug.getDrugEvidences().stream().map(DrugEvidence::getDrug).map(Drug::getName)
                .forEach(str -> Assertions.assertEquals("sample molecule name", str));
        List<String> evidenceUrls = drug.getDrugEvidences().stream().map(DrugEvidence::getRefUrl).collect(Collectors.toList());
        Assertions.assertEquals(Arrays.asList("ev1", "ev2", "ev3", "ev4"), evidenceUrls);
        Assertions.assertNotNull(drug.getCreatedAt());
        Assertions.assertNotNull(drug.getUpdatedAt());
    }

    ChemblOpenTarget createChemblOpenTarget(){
        ChemblOpenTarget.ChemblOpenTargetBuilder builder = ChemblOpenTarget.builder();
        builder.chemblTargetUrl("sample target url");
        builder.moleculeType("sample molecule type");
        builder.moleculeName("sample molecule name");
        builder.chemblSourceUrl("sample source url");
        builder.clinicalTrialPhase(2);
        builder.clinicalTrialLink("sample trial link");
        builder.mechOfAction("sample mech of action");
        builder.diseaseId("sample disease id efo url");
        List<String> evidences = Arrays.asList("ev1", "ev2", "ev3", "ev4");
        builder.drugEvidences(evidences);
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
