/*
 * Created by sahmad on 07/02/19 10:56
 * UniProt Consortium.
 * Copyright (c) 2002-2019.
 *
 */

package uk.ac.ebi.uniprot.ds.common.dao;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import uk.ac.ebi.uniprot.ds.common.model.Disease;
import uk.ac.ebi.uniprot.ds.common.model.DiseaseTest;
import uk.ac.ebi.uniprot.ds.common.model.Drug;
import uk.ac.ebi.uniprot.ds.common.model.DrugEvidence;
import uk.ac.ebi.uniprot.ds.common.model.Protein;
import uk.ac.ebi.uniprot.ds.common.model.ProteinCrossRef;
import uk.ac.ebi.uniprot.ds.common.model.ProteinCrossRefTest;
import uk.ac.ebi.uniprot.ds.common.model.ProteinTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class DrugDAOTest {
    @Autowired
    private DrugDAO drugDAO;
    @Autowired
    private ProteinDAO proteinDAO;

    @Autowired
    private DiseaseDAO diseaseDAO;

    private Drug drug;
    private Protein protein;
    private Disease disease;
    private String uuid;

    @BeforeEach
    void setUp(){
        this.uuid = UUID.randomUUID().toString();
    }

    @AfterEach
    void cleanUp(){
        if(this.drug != null){
            this.drugDAO.deleteById(this.drug.getId());
            this.drug = null;
        }

        if(this.protein != null){
            this.proteinDAO.deleteById(this.protein.getId());
            this.protein = null;
        }
        if(this.disease != null){
            this.diseaseDAO.deleteById(this.disease.getId());
            this.disease = null;
        }
    }

    @Test
    void createDrug(){
        this.drug = createDrugObject(this.uuid, null);
        this.drugDAO.save(this.drug);
        // get the drug and verify
        Optional<Drug> optDrug = this.drugDAO.findById(this.drug.getId());
        assertTrue(optDrug.isPresent());
        verifyDrug(this.drug, optDrug.get());
    }

    @Test
    void testCreateDrugWithProteinXRef(){
        // create protein with protein xref
        this.protein = ProteinTest.createProteinObject(this.uuid);
        ProteinCrossRef xref = ProteinCrossRefTest.createProteinCrossRefObject(this.uuid);
        xref.setProtein(protein);
        protein.setProteinCrossRefs(Arrays.asList(xref));
        this.proteinDAO.save(protein);

        // create a drug with protein xref
        this.drug = createDrugObject(this.uuid, xref);
        this.drugDAO.save(this.drug);
        // get the drug
        Optional<Drug> optDrug = this.drugDAO.findById(this.drug.getId());
        assertTrue(optDrug.isPresent());
        verifyDrug(this.drug, optDrug.get());
        assertEquals(this.drug.getProteinCrossRef().getId(), optDrug.get().getProteinCrossRef().getId());
    }

    @Test
    void testCreateDrugWithProteinXRefAndDrugEvidences(){
        // create protein with protein xref
        this.protein = ProteinTest.createProteinObject(this.uuid);
        ProteinCrossRef xref = ProteinCrossRefTest.createProteinCrossRefObject(this.uuid);
        xref.setProtein(protein);
        protein.setProteinCrossRefs(Arrays.asList(xref));
        this.proteinDAO.save(protein);

        // create a drug with protein xref
        this.drug = createDrugObject(this.uuid, xref);

        // create couple of drug evidences
        DrugEvidence ev1 = new DrugEvidence("refType1", "refUrl1", this.drug);
        DrugEvidence ev2 = new DrugEvidence("refType2", "refUrl2", this.drug);
        this.drug.setDrugEvidences(Arrays.asList(ev1, ev2));
        this.drugDAO.save(this.drug);
        // get the drug
        Optional<Drug> optDrug = this.drugDAO.findById(this.drug.getId());
        assertTrue(optDrug.isPresent());
        verifyDrug(this.drug, optDrug.get());
        assertEquals(this.drug.getProteinCrossRef().getId(), optDrug.get().getProteinCrossRef().getId());
    }

    @Test
    void testCreateDrugWithDisease(){
        this.disease = DiseaseTest.createDiseaseObject(this.uuid);
        this.diseaseDAO.save(disease);

        // create a drug with disease
        this.drug = createDrugObject(this.uuid, null);
        this.drug.setDisease(this.disease);
        this.drugDAO.save(this.drug);
        // get the drug
        Optional<Drug> optDrug = this.drugDAO.findById(this.drug.getId());
        assertTrue(optDrug.isPresent());
        verifyDrug(this.drug, optDrug.get());
        assertEquals(this.drug.getDisease().getId(), optDrug.get().getDisease().getId());
    }

    @Test
    void testGetDrugsByProtein(){
        // create protein with protein xref
        this.protein = ProteinTest.createProteinObject(this.uuid);
        ProteinCrossRef xref = ProteinCrossRefTest.createProteinCrossRefObject(this.uuid);
        xref.setProtein(protein);
        protein.setProteinCrossRefs(Arrays.asList(xref));
        this.proteinDAO.save(protein);
        this.disease = DiseaseTest.createDiseaseObject(this.uuid);
        this.diseaseDAO.save(disease);
        // create a drug with protein xref
        this.drug = createDrugObject(this.uuid, xref);
        this.drug.setDisease(this.disease);
        this.drugDAO.save(this.drug);

        List<Object[]> drugs = this.drugDAO.getDrugsByProtein(this.protein.getAccession());
        assertFalse(drugs.isEmpty());
        assertEquals(1, drugs.size());
        assertEquals(this.protein.getAccession(), (String) drugs.get(0)[8]);
    }

    private void verifyDrug(Drug actual, Drug expected) {
        assertEquals(actual.getId(), expected.getId());
        assertEquals(actual.getName(), expected.getName());
        assertEquals(actual.getSourceType(), expected.getSourceType());
        assertEquals(actual.getSourceId(), expected.getSourceId());
        assertEquals(actual.getMoleculeType(), expected.getMoleculeType());
        assertEquals(actual.getCreatedAt(), expected.getCreatedAt());
        assertEquals(actual.getUpdatedAt(), expected.getUpdatedAt());
        assertEquals(actual.getClinicalTrialPhase(), expected.getClinicalTrialPhase());
        assertEquals(actual.getClinicalTrialLink(), expected.getClinicalTrialLink());
        assertEquals(actual.getMechanismOfAction(), expected.getMechanismOfAction());
        assertEquals(actual.getChemblDiseaseId(), expected.getChemblDiseaseId());
        // verify the drug evidence
        if(actual.getDrugEvidences() != null){
            assertEquals(actual.getDrugEvidences().size(), expected.getDrugEvidences().size());
            expected.getDrugEvidences().forEach(ev -> assertEquals(drug.getId(), ev.getDrug().getId()));

        }
    }

    public static Drug createDrugObject(String rand, ProteinCrossRef xref){
        Drug.DrugBuilder bl = Drug.builder();
        String name = "Name-" + rand;
        String sourceType = "type-" + rand;
        String sourceid = "id-" + rand;
        String moleculeType = "mol-" + rand;
        Integer phase = 3;
        String link = "https://www.example.com/something1234";
        String mechnismOfAction = "this is a sample message";
        String efoDiseaseId = "http://www.example.com/EFO_0002345";
        bl.name(name).sourceType(sourceType).sourceId(sourceid);
        bl.moleculeType(moleculeType).proteinCrossRef(xref);
        bl.clinicalTrialPhase(phase).clinicalTrialLink(link).mechanismOfAction(mechnismOfAction);
        bl.chemblDiseaseId(efoDiseaseId);
        return bl.build();
    }

}
