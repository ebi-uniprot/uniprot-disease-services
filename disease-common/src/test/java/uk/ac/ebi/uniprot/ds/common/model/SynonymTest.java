/*
 * Created by sahmad on 07/02/19 11:00
 * UniProt Consortium.
 * Copyright (c) 2002-2019.
 *
 */

package uk.ac.ebi.uniprot.ds.common.model;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityTransaction;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@Transactional
public class SynonymTest extends BaseTest {
    private Synonym syn;
    private Disease disease;

    @AfterEach
    void cleanUp(){
        em.remove(syn);
        em.remove(disease);
    }

    @Test
    void testCreateSynonym(){
        disease = DiseaseTest.createDiseaseObject();
        // create synonyms
        syn = new Synonym();
        syn.setName("Name-" + random);
        syn.setDisease(disease);

        em.persist(disease);
        em.persist(syn);
        em.flush();
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
