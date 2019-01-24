/*
 * Created by sahmad on 1/24/19 3:28 PM
 * UniProt Consortium.
 * Copyright (c) 2002-2019.
 *
 */

package uk.ac.ebi.uniprot.ds.dao;

import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import uk.ac.ebi.uniprot.ds.dao.impl.DiseaseDAOImpl;
import uk.ac.ebi.uniprot.ds.dao.impl.SynonymDAOImpl;
import uk.ac.ebi.uniprot.ds.model.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

public class SynonymDAOImplTest extends BaseTest {
    private SynonymDAO synonymDAO = new SynonymDAOImpl(BaseTest.em);
    private DiseaseDAO diseaseDAO = new DiseaseDAOImpl(BaseTest.em);

    private Synonym synonym;
    private Disease disease;
    private List<Synonym> synonyms;

    @AfterEach
    void cleanUp(){
        if(this.synonym != null){
            executeInsideTransaction(dao -> dao.delete(this.synonym), this.synonymDAO);
            this.synonym = null;
        }
        if(this.synonyms != null && !this.synonyms.isEmpty()){
            this.synonyms.forEach(syn -> executeInsideTransaction(dao -> dao.delete(syn), this.synonymDAO));
            this.synonyms = null;
        }
        if(this.disease != null){
            executeInsideTransaction(dao -> dao.delete(this.disease), this.diseaseDAO);
            this.disease = null;
        }
    }

    @Test
    void testCreateSynonym(){
        // create parent disease
        this.disease = DiseaseTest.createDiseaseObject();
        executeInsideTransaction(dao -> dao.createOrUpdate(this.disease), this.diseaseDAO);
        assertNotNull(this.disease.getId());

        String name = "SYN-" + GUID;
        this.synonym = new Synonym();
        this.synonym.setName(name);
        this.synonym.setDisease(this.disease);
        // save in the db
        executeInsideTransaction(dao -> dao.createOrUpdate(this.synonym), this.synonymDAO);
        assertNotNull(this.synonym.getId(), "Synonym could not be created");
        // get the Synonym and verify
        Optional<Synonym> optSD = this.synonymDAO.get(this.synonym.getId());
        verifySynonym(optSD, name);
    }

    @Test
    void testCreateUpdateDisease(){
        // create parent disease
        this.disease = DiseaseTest.createDiseaseObject();
        executeInsideTransaction(dao -> dao.createOrUpdate(this.disease), this.diseaseDAO);
        assertNotNull(this.disease.getId());

        String name = "SYN-" + GUID;
        this.synonym = new Synonym();
        this.synonym.setName(name);
        this.synonym.setDisease(this.disease);
        // save in the db
        executeInsideTransaction(dao -> dao.createOrUpdate(this.synonym), this.synonymDAO);
        assertNotNull(this.synonym.getId(), "Synonym could not be created");
        // get the Synonym and verify
        Optional<Synonym> optSD = this.synonymDAO.get(this.synonym.getId());
        verifySynonym(optSD, name);

        // update the name
        String newName = "Updated-" + name;
        this.synonym.setName(newName);
        executeInsideTransaction(dao -> dao.createOrUpdate(this.synonym), this.synonymDAO);
        // get the Synonym and verify
        Optional<Synonym> optSD1 = this.synonymDAO.get(this.synonym.getId());
        verifySynonym(optSD1, newName);
    }

    @Test
    void testDeleteSynonym(){
        // create parent disease
        this.disease = DiseaseTest.createDiseaseObject();
        executeInsideTransaction(dao -> dao.createOrUpdate(this.disease), this.diseaseDAO);
        assertNotNull(this.disease.getId());

        String name = "SYN-" + GUID;
        this.synonym = new Synonym();
        this.synonym.setName(name);
        this.synonym.setDisease(this.disease);
        // save in the db
        executeInsideTransaction(dao -> dao.createOrUpdate(this.synonym), this.synonymDAO);
        assertNotNull(this.synonym.getId(), "Synonym could not be created");

        // delete the synonym
        executeInsideTransaction(dao -> dao.delete(this.synonym), this.synonymDAO);

        // try to get the synonym, it should fail
        Optional<Synonym> optSyn = this.synonymDAO.get(this.synonym.getId());
        assertFalse(optSyn.isPresent());
        this.synonym = null;
        // get the disease, it should exist
        Optional<Disease> optDis = this.diseaseDAO.get(this.disease.getId());
        assertTrue(optDis.isPresent());

    }

    @Test
    void testGetSynonymsByDisease(){
        // create parent disease
        this.disease = DiseaseTest.createDiseaseObject();
        executeInsideTransaction(dao -> dao.createOrUpdate(this.disease), this.diseaseDAO);
        assertNotNull(this.disease.getId());

        // create 10 synonyms
        this.synonyms = new ArrayList<>();
        IntStream.range(1, 11).forEach(i -> this.synonyms.add(createSynonym(i)));

        // get synonyms by diseases
        List<Synonym> disSyns = this.synonymDAO.getSynonymsByDisease(this.disease);
        assertEquals(10, disSyns.size());
    }

    private Synonym createSynonym(int i) {
        String name = "SYN-"+ i + "-" + GUID;
        Synonym syn = new Synonym();
        syn.setName(name);
        syn.setDisease(this.disease);
        // save in the db
        executeInsideTransaction(dao -> dao.createOrUpdate(syn), this.synonymDAO);
        assertNotNull(syn.getId(), "Synonym could not be created");
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



}
