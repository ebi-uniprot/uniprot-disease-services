/*
 * Created by sahmad on 07/02/19 11:00
 * UniProt Consortium.
 * Copyright (c) 2002-2019.
 *
 */

package uk.ac.ebi.uniprot.ds.common.model;

import org.junit.jupiter.api.*;
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
        if(this.syn != null) {
            em.remove(syn);
        }
        if(this.disease != null) {
            em.remove(disease);
        }
    }

    @Test
    void testCreateSynonym(){
        disease = DiseaseTest.createDiseaseObject();
        // create synonyms
        syn = new Synonym();
        syn.setName("Name-" + random);
        syn.setSource(disease.getSource());
        syn.setDisease(disease);

        em.persist(disease);
        em.persist(syn);
        em.flush();
        Assertions.assertNotNull(syn.getId());
        Assertions.assertNotNull(syn.getDisease());
        Assertions.assertEquals(disease.getId(), syn.getDisease().getId());
    }

    @Test
    void testEquals(){
        Disease dis = DiseaseTest.createDiseaseObject(random);
        Synonym.SynonymBuilder blr = Synonym.builder();
        blr.name("name").source("abcd").disease(dis);
        Synonym syn1 = blr.build();

        // create another one
        Disease dis1 = DiseaseTest.createDiseaseObject(random);
        Synonym.SynonymBuilder blr1 = Synonym.builder();
        blr1.name("name").source("abcd").disease(dis1);
        Synonym syn2 = blr1.build();

        Assertions.assertTrue(syn1.equals(syn2));
    }

    public static Synonym createSynonymObject(String uuid, Disease disease){
        Synonym synonym = new Synonym();
        String source = "SRC-" + uuid;
        synonym.setName("Name-" + uuid);
        synonym.setSource(source);
        synonym.setDisease(disease);
        return synonym;
    }
}
