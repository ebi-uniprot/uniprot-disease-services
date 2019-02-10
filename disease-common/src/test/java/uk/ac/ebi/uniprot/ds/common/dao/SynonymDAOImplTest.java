/*
 * Created by sahmad on 07/02/19 10:56
 * UniProt Consortium.
 * Copyright (c) 2002-2019.
 *
 */

package uk.ac.ebi.uniprot.ds.common.dao;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.ac.ebi.uniprot.ds.common.model.Disease;
import uk.ac.ebi.uniprot.ds.common.model.DiseaseTest;
import uk.ac.ebi.uniprot.ds.common.model.Synonym;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class SynonymDAOImplTest{
    @Autowired
    private SynonymDAO synonymDAO;
    @Autowired
    private DiseaseDAO diseaseDAO;

    private Synonym synonym;
    private Disease disease;
    private List<Synonym> synonyms;

    @AfterEach
    void cleanUp(){
        if(this.synonym != null){
            this.synonymDAO.delete(this.synonym);
            this.synonym = null;
        }
        if(this.synonyms != null && !this.synonyms.isEmpty()){
            this.synonyms.forEach(syn -> this.synonymDAO.delete(syn));
            this.synonyms = null;
        }
        if(this.disease != null){
            this.diseaseDAO.delete(this.disease);
            this.disease = null;
        }
    }

    @Test
    void testCreateSynonym(){
        // create parent disease
        this.disease = DiseaseTest.createDiseaseObject();
        this.diseaseDAO.save(this.disease);
        assertNotNull(this.disease.getId());
        String GUID = UUID.randomUUID().toString();
        String name = "SYN-" + GUID;
        this.synonym = new Synonym();
        this.synonym.setName(name);
        this.synonym.setDisease(this.disease);
        // save in the db
        this.synonymDAO.save(this.synonym);
        verifyCreation(this.synonym);
        // get the Synonym and verify
        Optional<Synonym> optSD = this.synonymDAO.findById(this.synonym.getId());
        verifySynonym(optSD, name);
    }

    @Test
    void testCreateUpdateDisease(){
        // create parent disease
        this.disease = DiseaseTest.createDiseaseObject();
        this.diseaseDAO.save(this.disease);
        assertNotNull(this.disease.getId());
        String GUID = UUID.randomUUID().toString();
        String name = "SYN-" + GUID;
        this.synonym = new Synonym();
        this.synonym.setName(name);
        this.synonym.setDisease(this.disease);
        // save in the db
        this.synonymDAO.save(this.synonym);
        verifyCreation(this.synonym);
        // get the Synonym and verify
        Optional<Synonym> optSD = this.synonymDAO.findById(this.synonym.getId());
        verifySynonym(optSD, name);

        // update the name
        String newName = "Updated-" + name;
        this.synonym.setName(newName);
        this.synonymDAO.save(this.synonym);
        // get the Synonym and verify
        Optional<Synonym> optSD1 = this.synonymDAO.findById(this.synonym.getId());
        verifySynonym(optSD1, newName);
    }

    @Test
    void testDeleteSynonym(){
        // create parent disease
        this.disease = DiseaseTest.createDiseaseObject();
        this.diseaseDAO.save(this.disease);
        assertNotNull(this.disease.getId());
        String GUID = UUID.randomUUID().toString();
        String name = "SYN-" + GUID;
        this.synonym = new Synonym();
        this.synonym.setName(name);
        this.synonym.setDisease(this.disease);
        // save in the db
        this.synonymDAO.save(this.synonym);
        verifyCreation(this.synonym);
        // delete the synonym
        this.synonymDAO.delete(this.synonym);

        // try to get the synonym, it should fail
        Optional<Synonym> optSyn = this.synonymDAO.findById(this.synonym.getId());
        assertFalse(optSyn.isPresent());
        this.synonym = null;
        // get the disease, it should exist
        Optional<Disease> optDis = this.diseaseDAO.findById(this.disease.getId());
        assertTrue(optDis.isPresent());

    }

    @Test
    void testGetSynonymsByDisease(){
        // create parent disease
        this.disease = DiseaseTest.createDiseaseObject();
        this.diseaseDAO.save(this.disease);
        assertNotNull(this.disease.getId());

        // create 10 synonyms
        this.synonyms = new ArrayList<>();
        IntStream.range(1, 11).forEach(i -> this.synonyms.add(createSynonym(i)));

        // get synonyms by diseases
        List<Synonym> disSyns = this.synonymDAO.findAllByDisease(this.disease);
        assertEquals(10, disSyns.size());
    }

    private Synonym createSynonym(int i) {
        String GUID = UUID.randomUUID().toString();
        String name = "SYN-"+ i + "-" + GUID;
        Synonym syn = new Synonym();
        syn.setName(name);
        syn.setDisease(this.disease);
        // save in the db
        this.synonymDAO.save(syn);
        verifyCreation(syn);
        return syn;
    }

    private void verifySynonym( Optional<Synonym> optSD, String name) {
        assertTrue(optSD.isPresent(), "Unable to get the synonym");
        assertEquals(this.synonym.getId(), optSD.get().getId());
        assertEquals(name, optSD.get().getName());
        assertNotNull(optSD.get().getCreatedAt());
        assertNotNull(optSD.get().getUpdatedAt());
        Disease sd = this.synonym.getDisease();
        assertNotNull(sd, "Disease is null for synonym");
        assertEquals(this.disease, sd);
    }

    private void verifyCreation(Synonym synonym) {
        assertNotNull(synonym.getId(), "Synonym could not be created");
    }

}
