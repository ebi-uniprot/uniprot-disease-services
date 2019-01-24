/*
 * Created by sahmad on 23/01/19 16:24
 * UniProt Consortium.
 * Copyright (c) 2002-2019.
 *
 */

package uk.ac.ebi.uniprot.ds.model;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.persistence.EntityTransaction;

public class DiseaseTest extends BaseTest {

    private Disease disease;
    @AfterEach
    void cleanUp(){
        EntityTransaction txn = em.getTransaction();
        txn.begin();
        em.remove(disease);
        txn.commit();
    }

    @Test
    void testCreateDisease(){
        disease = createDiseaseObject();
        EntityTransaction txn = em.getTransaction();
        txn.begin();
        em.persist(disease);
        txn.commit();
        Assertions.assertNotNull(disease.getId());
    }

    public static Disease createDiseaseObject(int random) {
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
        return createDiseaseObject(BaseTest.random);
    }
}
