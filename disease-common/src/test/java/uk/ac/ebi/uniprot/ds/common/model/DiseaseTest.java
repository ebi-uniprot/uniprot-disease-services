/*
 * Created by sahmad on 07/02/19 11:00
 * UniProt Consortium.
 * Copyright (c) 2002-2019.
 *
 */

package uk.ac.ebi.uniprot.ds.common.model;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@Transactional
public class DiseaseTest extends BaseTest {
    private Disease disease;
    @AfterEach
    void cleanUp(){
        em.remove(disease);
    }

    @Test
    void testCreateDisease(){
        disease = createDiseaseObject();
        em.persist(disease);
        em.flush();
        Assertions.assertNotNull(disease.getId());
    }

    @Test
    void testDiseaseWithSynonyms(){
        this.disease = createDiseaseObject(BaseTest.random);
        // add synonym
        Synonym synonym = SynonymTest.createSynonymObject(BaseTest.random);
        this.disease.addSynonym(synonym);
        // create disease with a synonym
        em.persist(this.disease);
        em.flush();

        // verify the object id
        Assertions.assertNotNull(this.disease.getId());
        Assertions.assertEquals(1, this.disease.getSynonyms().size());
        Assertions.assertNotNull(this.disease.getSynonyms().get(0).getId());
    }

    @Test
    void testDiseaseSynonymRemove(){
        this.disease = createDiseaseObject(BaseTest.random);
        // add synonym
        Synonym synonym = SynonymTest.createSynonymObject(BaseTest.random);
        this.disease.addSynonym(synonym);
        // create disease with a synonym
        em.persist(this.disease);
        em.flush();

        // verify the object id
        Assertions.assertNotNull(this.disease.getId());
        Assertions.assertEquals(1, this.disease.getSynonyms().size());
        Assertions.assertNotNull(this.disease.getSynonyms().get(0).getId());

        // remove the synonym from disease
        this.disease.removeSynonym(synonym);
        em.persist(this.disease);
        em.flush();
        Assertions.assertNotNull(this.disease.getId());
        Assertions.assertEquals(0, this.disease.getSynonyms().size());
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
