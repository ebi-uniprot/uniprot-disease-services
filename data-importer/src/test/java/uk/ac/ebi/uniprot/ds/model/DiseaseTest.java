/*
 * Created by sahmad on 23/01/19 16:24
 * UniProt Consortium.
 * Copyright (c) 2002-2019.
 *
 */

package uk.ac.ebi.uniprot.ds.model;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.ebi.uniprot.ds.dao.DiseaseDAO;

import javax.persistence.EntityTransaction;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class DiseaseTest extends BaseTest {

    private Disease disease;
    @AfterEach
    void cleanUp(){
        EntityTransaction txn = em.getTransaction();
        txn.begin();
        em.remove(disease);
        txn.commit();
    }

    @Disabled
    @Test
    void testCreateDisease(){
        disease = createDiseaseObject();
        EntityTransaction txn = em.getTransaction();
        txn.begin();
        em.persist(disease);
        txn.commit();
        Assertions.assertNotNull(disease.getId());
    }

    public static Disease createDiseaseObject(String random) {
        Disease disease = new Disease();
        String dId = "DID-" + random;
        String dn = "DN-" + random;
        String desc = "DESC-" + random;
        String acr = "ACRONYM-" + random;
        disease.setDiseaseId(dId);
        disease.setName(dn);
        disease.setDesc(desc);
        disease.setAcronym(acr);
        return disease;
    }

    public static Disease createDiseaseObject() {
        return createDiseaseObject(String.valueOf(BaseTest.random));
    }
}
