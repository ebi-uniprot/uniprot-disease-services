/*
 * Created by sahmad on 1/24/19 9:52 AM
 * UniProt Consortium.
 * Copyright (c) 2002-2019.
 *
 */

package uk.ac.ebi.uniprot.ds.dao;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.ac.ebi.uniprot.ds.model.Disease;
import uk.ac.ebi.uniprot.ds.model.DiseaseTest;

import java.util.*;
import java.util.stream.IntStream;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class DiseaseDAOImplTest {

    @Autowired
    private DiseaseDAO diseaseDAO;

    private Disease disease;
    private List<Disease> diseases;

    @AfterEach
    void cleanUp(){
        if(this.disease != null){
            this.diseaseDAO.delete(this.disease);
            this.disease = null;
        }

        if(this.diseases != null && !this.diseases.isEmpty()){
            this.diseases.forEach(disease -> this.diseaseDAO.delete(disease));
            this.diseases = null;
        }
    }

    @Test
    void testCreateDisease(){
        this.disease = this.diseaseDAO.save(DiseaseTest.createDiseaseObject());
        assertNotNull(this.disease.getId(), "Unable to save the disease");
    }

    @Test
    void testCreateUpdateDisease(){
        String random = UUID.randomUUID().toString();
        this.disease = createDisease();
        Long id = disease.getId();
        // update the disease
        String dId = "UDID-" + random;
        String dn = "UDN-" + random;
        String desc = "UDESC-" + random;
        String acr = "UACRONYM-" + random;
        this.disease.setDiseaseId(dId);
        this.disease.setName(dn);
        this.disease.setDesc(desc);
        this.disease.setAcronym(acr);
        this.disease = this.diseaseDAO.save(this.disease);

        // get the disease and verify
        Optional<Disease> optDis = this.diseaseDAO.findById(id);
        assertTrue(optDis.isPresent(), "unable to find the disease with id " + id);
        Disease sDis = optDis.get();
        assertEquals(id, sDis.getId());
        assertEquals(dId, sDis.getDiseaseId());
        assertEquals(dn, sDis.getName());
        assertEquals(desc, sDis.getDesc());
        assertEquals(acr, sDis.getAcronym());
    }

    @Test
    void testDeleteDisease(){
        // create the disease
        this.disease = createDisease();

        // delete the disease now
        this.diseaseDAO.delete(this.disease);
        // try to get the disease now
        Optional<Disease> optDisease = this.diseaseDAO.findById(this.disease.getId());
        assertFalse(optDisease.isPresent(), "Unable to delete the disease");
       this.disease= null;
    }

    @Test
    void testDeleteDiseaseByDiseaseId(){
        // create the disease
        this.disease = createDisease();

        // delete the disease now
        this.diseaseDAO.deleteByDiseaseId(this.disease.getDiseaseId());
        // try to get the disease now
        Optional<Disease> optDisease = this.diseaseDAO.findById(this.disease.getId());
        assertFalse(optDisease.isPresent(), "Unable to delete the disease by disease id");
        this.disease= null;
    }

    @Test
    void testGetDisease(){
        // create the disease
       this.disease = createDisease();
        // get the disease and verify
        Optional<Disease> optStoredDisease = this.diseaseDAO.findById(this.disease.getId());
        assertTrue(optStoredDisease.isPresent(), "unable to get the disease");

        Disease storedDisease = optStoredDisease.get();

        assertAll("disease values",
                () -> assertEquals(this.disease.getId(), storedDisease.getId()),
                () -> assertEquals(this.disease.getDiseaseId(), storedDisease.getDiseaseId()),
                () -> assertEquals(this.disease.getName(), storedDisease.getName()),
                () -> assertEquals(this.disease.getDesc(), storedDisease.getDesc()),
                () -> assertEquals(this.disease.getAcronym(), storedDisease.getAcronym()),
                () -> assertEquals(this.disease.getCreatedAt(), storedDisease.getCreatedAt()),
                () -> assertEquals(this.disease.getUpdatedAt(), storedDisease.getUpdatedAt())
                );
    }

    @Test
    void testDeleteById(){
        // create the disease
       this.disease = createDisease();

        this.diseaseDAO.deleteById(this.disease.getId());
        // try to get the disease now
        Optional<Disease> optDisease = this.diseaseDAO.findById(this.disease.getId());
        assertFalse(optDisease.isPresent(), "Unable to delete the disease");
       this.disease= null;
    }

    @Test
    void testGetAll(){//TODO fix pagination
        this.diseases = new ArrayList<>();
        // create 50 diseases
        IntStream.range(1, 51).forEach(i -> this.diseases.add(createDisease()));
        // get first 25 diseases
        List<Disease> first25 = this.diseaseDAO.findAll();
        assertTrue(first25.size() >= 50, "Unable to get first 25 records");
        // get last 25 diseases
        /*List<Disease> last25 = this.diseaseDAO.getAll(25, 25);
        assertEquals(25, last25.size(), "Unable to get last 25 records");
        // try to get again, it should return empty result
        List<Disease> nonExistent = this.diseaseDAO.getAll(50, 75);
        assertTrue(nonExistent.isEmpty(), "we should get empty list");*/
    }

    @Test
    void testDeleteNonExistentDisease(){
        EmptyResultDataAccessException exception = assertThrows(EmptyResultDataAccessException.class, () -> this.diseaseDAO.deleteById(new Random().nextLong()));
        assertTrue(exception.getMessage().contains("No class uk.ac.ebi.uniprot.ds.model.Disease entity with id"));
    }

    @Test
    void testGetNonExistentDisease(){
        long randId = (long) (Math.random()*100000);
        Optional<Disease> optDisease = this.diseaseDAO.findById(randId);
        assertFalse(optDisease.isPresent(), "Disease is found!");
    }

    private Disease createDisease() {
        String uuid = UUID.randomUUID().toString();
        Disease dis = DiseaseTest.createDiseaseObject(uuid);
        dis = this.diseaseDAO.save(dis);
        assertNotNull(dis.getId(), "Unable to save the disease");
        return dis;
    }
}
