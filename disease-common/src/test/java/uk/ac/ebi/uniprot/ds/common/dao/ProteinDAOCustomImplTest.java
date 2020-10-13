package uk.ac.ebi.uniprot.ds.common.dao;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import uk.ac.ebi.uniprot.ds.common.model.Disease;
import uk.ac.ebi.uniprot.ds.common.model.DiseaseProtein;
import uk.ac.ebi.uniprot.ds.common.model.DiseaseTest;
import uk.ac.ebi.uniprot.ds.common.model.Protein;
import uk.ac.ebi.uniprot.ds.common.model.ProteinTest;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class ProteinDAOCustomImplTest {
    @Autowired
    private ProteinDAO proteinDAO;
    @Autowired
    private DiseaseDAO diseaseDAO;

    @Autowired
    private DiseaseProteinDAO diseaseProteinDAO;

    @AfterEach
    public void cleanUp(){
        this.diseaseProteinDAO.deleteAll();
        this.diseaseDAO.deleteAll();
        this.proteinDAO.deleteAll();
    }

    @Test
    void testGetProteinsByDiseaseId(){
        // create diseases with proteins and verify the result
        // disease1(2 proteins (P1, P2))
        //   |
        // disease2(no protein) -- disease3 (2 proteins (P2, P3))
        //    |
        // disease4(2 proteins (P1, P4))
        Protein p1 = this.proteinDAO.save(ProteinTest.createProteinObject("P1"));
        Protein p2 = this.proteinDAO.save(ProteinTest.createProteinObject("P2"));
        Protein p3 = this.proteinDAO.save(ProteinTest.createProteinObject("P3"));
        Protein p4 = this.proteinDAO.save(ProteinTest.createProteinObject("P4"));
        // Protein without any disease association
        Protein p5 = this.proteinDAO.save(ProteinTest.createProteinObject("P5"));
        Disease disease1 = DiseaseTest.createDiseaseObject();
        disease1.setName("disease1");disease1.setDiseaseId("disease1");
        DiseaseProtein dp1 = new DiseaseProtein(disease1, p1, false);
        disease1.getDiseaseProteins().add(dp1);
        DiseaseProtein dp2 = new DiseaseProtein(disease1, p2, false);
        disease1.getDiseaseProteins().add(dp2);
        Disease disease2 = DiseaseTest.createDiseaseObject();
        disease2.setName("disease2");disease2.setDiseaseId("disease2");
        Disease disease3 = DiseaseTest.createDiseaseObject();
        disease3.setName("disease3");disease3.setDiseaseId("disease3");
        DiseaseProtein d3p2 = new DiseaseProtein(disease3, p2, false);
        disease3.getDiseaseProteins().add(d3p2);
        DiseaseProtein d3p3 = new DiseaseProtein(disease3, p3, false);
        disease3.getDiseaseProteins().add(d3p3);
        Disease disease4 = DiseaseTest.createDiseaseObject();
        disease4.setName("disease4");disease4.setDiseaseId("disease4");
        DiseaseProtein d4p1 = new DiseaseProtein(disease4, p1, false);
        disease4.getDiseaseProteins().add(d4p1);
        DiseaseProtein d4p4 = new DiseaseProtein(disease4, p4, false);
        disease4.getDiseaseProteins().add(d4p4);
        disease2.setChildren(Collections.singletonList(disease4));
        disease1.setChildren(Arrays.asList(disease2, disease3));
        this.diseaseDAO.save(disease1);
        // get the hierarchy
        List<Object[]> parentChildren = this.diseaseDAO.getParentAndItsDescendents(disease1.getId());
        Assertions.assertNotNull(parentChildren);
        Assertions.assertEquals(4, parentChildren.size());

        //store in ds_disease_descendents table
        for (Object[] parentChild : parentChildren) {
            Assertions.assertEquals(disease1.getId(), parentChild[0]);
            int count = this.diseaseDAO.insertDiseaseIdAndDescendentId((Long) parentChild[0], (Long) parentChild[1]);
            Assertions.assertEquals(1, count);
        }

        // the result should have P1, P2, P3, P4 but not P5
        List<Protein> proteins = this.proteinDAO.getProteinsByDiseaseId("disease1");
        Assertions.assertFalse(proteins.isEmpty());
        Assertions.assertEquals(4, proteins.size());
        List<String> accessions = Arrays.asList("ACC-P1", "ACC-P2", "ACC-P3", "ACC-P4");
        List<String> returnedAccessions = proteins.stream().map(Protein::getAccession).collect(Collectors.toList());
        Assertions.assertTrue(returnedAccessions.containsAll(accessions));
    }

    @Test
    void testGetProteinsByNonExistingDiseaseId(){
        List<Protein> proteins = this.proteinDAO.getProteinsByDiseaseId("random disease id");
        Assertions.assertTrue(proteins.isEmpty());
    }
}
