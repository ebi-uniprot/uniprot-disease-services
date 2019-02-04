/*
 * Created by sahmad on 23/01/19 16:41
 * UniProt Consortium.
 * Copyright (c) 2002-2019.
 *
 */

package uk.ac.ebi.uniprot.ds.model;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import javax.persistence.EntityTransaction;

public class SynonymTest extends BaseTest {
    private Synonym syn;
    private Disease disease;

    @AfterEach
    void cleanUp(){
        EntityTransaction txn = em.getTransaction();
        txn.begin();
        em.remove(syn);
        em.remove(disease);
        txn.commit();
    }

    @Disabled
    @Test
    void testCreateSynonym(){
        disease = DiseaseTest.createDiseaseObject();
        // create synonyms
        syn = new Synonym();
        syn.setName("Name-" + random);
        syn.setDisease(disease);

        EntityTransaction txn = em.getTransaction();
        txn.begin();
        em.persist(disease);
        em.persist(syn);
        txn.commit();
        Assertions.assertNotNull(syn.getId());
        Assertions.assertNotNull(syn.getDisease());
        Assertions.assertEquals(disease.getId(), syn.getDisease().getId());
    }

    public static Synonym createSynonymObject(String uuid){
        Synonym synonym = new Synonym();
        synonym.setName("Name-" + uuid);
        return synonym;
    }
}
