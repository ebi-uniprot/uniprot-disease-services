package uk.ac.ebi.uniprot.ds.common.dao;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import uk.ac.ebi.uniprot.ds.common.model.Disease;
import uk.ac.ebi.uniprot.ds.common.model.DiseaseTest;
import uk.ac.ebi.uniprot.ds.common.model.Drug;
import uk.ac.ebi.uniprot.ds.common.model.DrugEvidence;
import uk.ac.ebi.uniprot.ds.common.model.Protein;
import uk.ac.ebi.uniprot.ds.common.model.ProteinCrossRef;
import uk.ac.ebi.uniprot.ds.common.model.ProteinCrossRefTest;
import uk.ac.ebi.uniprot.ds.common.model.ProteinTest;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class DrugDAOGetByProteinAccessionTest {
    @Autowired
    private DrugDAO drugDAO;
    @Autowired
    private ProteinDAO proteinDAO;
    @Autowired
    private DiseaseDAO diseaseDAO;

    @AfterEach
    void cleanUp() {
        this.drugDAO.deleteAll();
        this.proteinDAO.deleteAll();
        this.diseaseDAO.deleteAll();
    }

    @Test
    void testGetDrugsByProtein() {
        // set up the data
        // create 5 proteins each with 2 xrefs
        List<Protein> proteins = new ArrayList<>();
        List<ProteinCrossRef> xrefs = new ArrayList<>();
        IntStream.range(0, 5).forEach(i -> {
            Protein protein = ProteinTest.createProteinObject(UUID.randomUUID().toString());
            ProteinCrossRef xref1 = ProteinCrossRefTest.createProteinCrossRefObject();
            xref1.setProtein(protein);
            ProteinCrossRef xref2 = ProteinCrossRefTest.createProteinCrossRefObject();
            xref2.setProtein(protein);
            protein.setProteinCrossRefs(Arrays.asList(xref1, xref2));
            this.proteinDAO.save(protein);
            xrefs.add(xref1);
            xrefs.add(xref2);
            proteins.add(protein);
        });

        // create 7 diseases
        List<Disease> diseases = new ArrayList<>();
        IntStream.range(0, 7).forEach(i -> {
            Disease disease = DiseaseTest.createDiseaseObject(UUID.randomUUID().toString());
            this.diseaseDAO.save(disease);
            diseases.add(disease);
        });
        // 5 drugs to be used
        String d0 = UUID.randomUUID().toString();
        String d1 = UUID.randomUUID().toString();
        String d2 = UUID.randomUUID().toString();
        String d3 = UUID.randomUUID().toString();
        String d4 = UUID.randomUUID().toString();

        // create drugs with or without cross ref
        Drug drug1 = createDrugObject(d0, xrefs.get(0));// with evidences
        DrugEvidence ev11 = new DrugEvidence(d0 + "refType1", d0 + "refUrl1", drug1);
        DrugEvidence ev12 = new DrugEvidence(d0 + "refType2", d0 + "refUrl2", drug1);
        drug1.setDrugEvidences(Arrays.asList(ev11, ev12));
        this.drugDAO.save(drug1);
        Drug drug2 = createDrugObject(d1, xrefs.get(1));// with evidences
        DrugEvidence ev21 = new DrugEvidence(d1 + "refType1", d1 + "refUrl1", drug2);
        DrugEvidence ev22 = new DrugEvidence(d1 + "refType2", d1 + "refUrl2", drug2);
        drug2.setDrugEvidences(Arrays.asList(ev21, ev22));
        this.drugDAO.save(drug2);
        Drug drug3 = createDrugObject(d2);// without evidences
        this.drugDAO.save(drug3);
        // create drugs under disease with or without evidences
        Drug drug4 = createDrugObject(d0, diseases.get(0));// without evidences
        this.drugDAO.save(drug4);
        Drug drug5 = createDrugObject(d1, diseases.get(1));//// with evidences
        DrugEvidence ev51 = new DrugEvidence(d1 + "refType1", d1 + "refUrl1", drug5);
        DrugEvidence ev52 = new DrugEvidence(d1 + "refType2", d1 + "refUrl2", drug5);
        drug5.setDrugEvidences(Arrays.asList(ev51, ev52));
        this.drugDAO.save(drug5);
        Drug drug6 = createDrugObject(d2, diseases.get(2));// without evidences
        this.drugDAO.save(drug6);
        Drug drug7 = createDrugObject(d3);// with evidences
        DrugEvidence ev71 = new DrugEvidence(d3 + "refType1", d3 + "refUrl1", drug7);
        DrugEvidence ev72 = new DrugEvidence(d3 + "refType2", d3 + "refUrl2", drug7);
        drug7.setDrugEvidences(Arrays.asList(ev71, ev72));
        this.drugDAO.save(drug7);
        // drugs without protein and disease
        Drug drug8 = createDrugObject(d4);// without evidences
        this.drugDAO.save(drug8);
        Drug drug9 = createDrugObject(d0);// with evidences
        DrugEvidence ev91 = new DrugEvidence(d0 + "refType1", d0 + "refUrl1", drug9);
        DrugEvidence ev92 = new DrugEvidence(d0 + "refType2", d0 + "refUrl2", drug9);
        drug9.setDrugEvidences(Arrays.asList(ev91, ev92));
        this.drugDAO.save(drug9);
        // data set up complete
        // then verify
        List<Drug> drugs = this.drugDAO.getDrugsByProtein(proteins.get(0).getAccession());
        Assertions.assertNotNull(drugs);
        Assertions.assertEquals(5, drugs.size());
        // get the cross refs from drugs and verify they match with protein with accession
        List<ProteinCrossRef> drugXrefs = drugs.stream().filter(drug -> Objects.nonNull(drug.getProteinCrossRef()))
                .map(Drug::getProteinCrossRef).collect(Collectors.toList());
        List<ProteinCrossRef> proteinXrefs = proteins.get(0).getProteinCrossRefs();
        Assertions.assertTrue(drugXrefs.containsAll(proteinXrefs));
        List<String> drugNamesForProtein = drugs.stream().filter(drug -> Objects.nonNull(drug.getProteinCrossRef()))
                .map(drug -> drug.getName()).collect(Collectors.toList());
        // all drugs should have name from one of the above list
        drugs.stream().forEach(drug -> Assertions.assertTrue(drugNamesForProtein.contains(drug.getName())));
        // we should have few disease from drugs also
        List<Disease> drugDiseases = drugs.stream().filter(drug -> Objects.nonNull(drug.getDisease())).map(drug -> drug.getDisease())
                .collect(Collectors.toList());
        Assertions.assertFalse(drugDiseases.isEmpty());
    }

    @Test
    void testGetDrugsByNonExistentProtein() {
        List<Drug> drugs = this.drugDAO.getDrugsByProtein("random accession");
        Assertions.assertTrue(drugs.isEmpty());
    }

    public static Drug createDrugObject(String rand, Disease disease) {
        Drug drug = createDrugObject(rand);
        drug.setDisease(disease);
        return drug;
    }

    public static Drug createDrugObject(String rand, ProteinCrossRef xref) {
        Drug drug  = createDrugObject(rand);
        drug.setProteinCrossRef(xref);
        return drug;
    }

    public static Drug createDrugObject(String rand) {
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
        bl.moleculeType(moleculeType);
        bl.clinicalTrialPhase(phase).clinicalTrialLink(link).mechanismOfAction(mechnismOfAction);
        bl.chemblDiseaseId(efoDiseaseId);
        return bl.build();
    }
}
