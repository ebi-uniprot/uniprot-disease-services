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
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import uk.ac.ebi.uniprot.ds.common.model.Disease;
import uk.ac.ebi.uniprot.ds.common.model.DiseaseTest;

/**
 * @author sahmad
 * @created 06/10/2020
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class DiseaseDAOCustomImplTest {

    @Autowired
    private DiseaseDAO diseaseDAO;

    @AfterEach
    void cleanUp(){
        this.diseaseDAO.truncateDiseaseDescendent();
        this.diseaseDAO.truncateDiseaseRelation();
        this.diseaseDAO.deleteAll();
    }

    /*  Create the below relationships and test
        disease1
           |
        disease2, disease3
           |
        disease4
           |
        disease5
     */
    @Test
    void testGetDiseaseAndItsChildren() {
        // when
        List<String> diseaseIds = Arrays.asList("disease1", "disease2", "disease3", "disease4", "disease5");
        Disease root = createDiseaseHierarchy();
        Assertions.assertNotNull(root);
        Assertions.assertNotNull(root.getId());
        // get the flattened hierarchy
        List<Object[]> parentChildren = this.diseaseDAO.getParentAndItsDescendents(root.getId());
        Assertions.assertNotNull(parentChildren);
        Assertions.assertEquals(5, parentChildren.size());

        //store in ds_disease_descendents table
        for (Object[] parentChild : parentChildren) {
            Assertions.assertEquals(root.getId(), parentChild[0]);
            int count = this.diseaseDAO.insertDiseaseIdAndDescendentId((Long) parentChild[0], (Long) parentChild[1]);
            Assertions.assertEquals(1, count);
        }

        // then get the disease and its descendents
        List<Disease> diseaseAndItsDesc = this.diseaseDAO.getDiseaseAndItsChildren(root.getDiseaseId());
        Assertions.assertNotNull(diseaseAndItsDesc);
        Assertions.assertEquals(5, diseaseAndItsDesc.size());
        List<String> names = diseaseAndItsDesc.stream().map(Disease::getDiseaseId).collect(Collectors.toList());
        Assertions.assertTrue(names.containsAll(diseaseIds));
    }

    // create a disease without any child
    // that disease should be returned
    @Test
    void testGetDiseaseAndItsChildrenWithoutAnyChild(){
        Disease disease = DiseaseTest.createDiseaseObject();
        disease.setDiseaseId("diseaseWithoutChild");
        disease.setName("diseaseWithoutChild");
        this.diseaseDAO.save(disease);
        // get the flattened hierarchy
        List<Object[]> parentDescendents = this.diseaseDAO.getParentAndItsDescendents(disease.getId());
        Assertions.assertNotNull(parentDescendents);
        Assertions.assertEquals(1, parentDescendents.size());
        //store in ds_disease_descendents table
        Assertions.assertEquals(disease.getId(), parentDescendents.get(0)[0]);
        int count = this.diseaseDAO.insertDiseaseIdAndDescendentId((Long) parentDescendents.get(0)[0], (Long) parentDescendents.get(0)[1]);
        Assertions.assertEquals(1, count);
        // then get the disease and its descendents
        List<Disease> diseaseAndItsDesc = this.diseaseDAO.getDiseaseAndItsChildren(disease.getDiseaseId());
        Assertions.assertNotNull(diseaseAndItsDesc);
        Assertions.assertEquals(1, diseaseAndItsDesc.size());
        String name = diseaseAndItsDesc.get(0).getDiseaseId();
        Assertions.assertEquals(name, disease.getDiseaseId());
    }

    @Test
    void testGetDiseaseAndItsChildrenWithInvalidDiseaseId(){
        List<Disease> diseaseAndItsDesc = this.diseaseDAO.getDiseaseAndItsChildren("some random name");
        Assertions.assertNotNull(diseaseAndItsDesc);
        Assertions.assertTrue(diseaseAndItsDesc.isEmpty());
    }

    @Test
    void testGetDiseaseAndItsChildrenWithNullDiseaseId(){
        List<Disease> diseaseAndItsDesc = this.diseaseDAO.getDiseaseAndItsChildren(null);
        Assertions.assertNotNull(diseaseAndItsDesc);
        Assertions.assertTrue(diseaseAndItsDesc.isEmpty());
    }

    private Disease createDiseaseHierarchy() {
        // create root
        Disease disease1 = DiseaseTest.createDiseaseObject();
        disease1.setName("disease1");
        disease1.setDiseaseId("disease1");
        // create child1
        Disease disease2 = DiseaseTest.createDiseaseObject();
        disease2.setName("disease2");
        disease2.setDiseaseId("disease2");
        // add disease2's child
        Disease disease4 = DiseaseTest.createDiseaseObject();
        disease4.setName("disease4");
        disease4.setDiseaseId("disease4");
        disease2.setChildren(Collections.singletonList(disease4));
        // add disease4's child
        Disease disease5 = DiseaseTest.createDiseaseObject();
        disease5.setName("disease5");
        disease5.setDiseaseId("disease5");
        disease4.setChildren(Collections.singletonList(disease5));
        // create child2
        Disease disease3 = DiseaseTest.createDiseaseObject();
        disease3.setName("disease3");
        disease3.setDiseaseId("disease3");
        // add disease1's child
        List<Disease> children = new ArrayList<>();
        children.add(disease2);
        children.add(disease3);
        disease1.setChildren(children);
        this.diseaseDAO.save(disease1);
        return disease1;
    }
}
