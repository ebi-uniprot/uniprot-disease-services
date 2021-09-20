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
import uk.ac.ebi.uniprot.ds.common.model.DiseaseTest;
import uk.ac.ebi.uniprot.ds.common.model.Drug;

import static uk.ac.ebi.uniprot.ds.common.dao.DrugDAOGetByProteinAccessionTest.createDrugObject;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class DrugDAOGetByDiseaseIdTest {
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
    void testGetDrugsByDiseaseId() {
        // set up the data
        // 5 drugs to be used - suffixes of drugs
        String d0 = "0";
        String d1 = "1";
        String d2 = "2";
        String d3 = "3";
        String d4 = "4";
        // Disease Hierarchy
        // disease1
        //    |
        // disease2, disease3
        //    |
        // disease4
        // create disease 1
        Disease disease1 = DiseaseTest.createDiseaseObject();
        disease1.setName("disease1");disease1.setDiseaseId("disease1");
        Disease disease2 = DiseaseTest.createDiseaseObject();
        disease2.setName("disease2");disease2.setDiseaseId("disease2");
        Disease disease3 = DiseaseTest.createDiseaseObject();
        disease3.setName("disease3");disease3.setDiseaseId("disease3");
        Disease disease4 = DiseaseTest.createDiseaseObject();
        disease4.setName("disease4");disease4.setDiseaseId("disease4");
        disease2.setChildren(Collections.singletonList(disease4));
        disease1.setChildren(Arrays.asList(disease2, disease3));
        this.diseaseDAO.save(disease1);
        // disease 1 has two drugs with suffix d0 and d1
        this.drugDAO.save(createDrugObject(d0, disease1));
        this.drugDAO.save(createDrugObject(d1, disease1));
        // disease 3 has two drugs with suffix d2 and d1
        this.drugDAO.save(createDrugObject(d2, disease3));
        this.drugDAO.save(createDrugObject(d1, disease3));
        // disease 4 has one drug with suffix d3
        this.drugDAO.save(createDrugObject(d3, disease4));
        // drug with suffix 0 and 4 without disease
        this.drugDAO.save(createDrugObject(d0));
        this.drugDAO.save(createDrugObject(d4));// it should not come in the result

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

        // then get the drugs
        List<Object[]> drugs = this.drugDAO.getDrugsByDiseaseId("disease1");
        Assertions.assertEquals(5, drugs.size());
        List<String> drugNames = drugs.stream().map(arr -> (String) arr[0]).collect(Collectors.toList());
        Assertions.assertFalse(drugNames.contains("Name-4"));
        Assertions.assertTrue(drugNames.containsAll(Arrays.asList("Name-0", "Name-1", "Name-2", "Name-3")));
        List<String> diseaseNames = drugs.stream().map(arr -> (String) arr[10]).collect(Collectors.toList());
        Assertions.assertTrue(diseaseNames.contains("disease1"));
        Assertions.assertTrue(diseaseNames.contains("disease3"));
        Assertions.assertTrue(diseaseNames.contains("disease4"));
    }

    @Test
    void testGetDrugsByNonExistentDiseaseId() {
        List<Object[]> drugs = this.drugDAO.getDrugsByDiseaseId("random disease id");
        Assertions.assertTrue(drugs.isEmpty());
    }
}
