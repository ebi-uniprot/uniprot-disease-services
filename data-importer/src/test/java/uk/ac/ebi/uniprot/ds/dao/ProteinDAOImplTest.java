/*
 * Created by sahmad on 1/24/19 8:41 PM
 * UniProt Consortium.
 * Copyright (c) 2002-2019.
 *
 */

package uk.ac.ebi.uniprot.ds.dao;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import uk.ac.ebi.uniprot.ds.dao.impl.DiseaseDAOImpl;
import uk.ac.ebi.uniprot.ds.dao.impl.ProteinDAOImpl;
import uk.ac.ebi.uniprot.ds.model.*;

import java.util.*;
import java.util.stream.IntStream;

public class ProteinDAOImplTest extends BaseTest {
    private ProteinDAO proteinDAO = new ProteinDAOImpl(BaseTest.em);
    private DiseaseDAO diseaseDAO = new DiseaseDAOImpl(BaseTest.em);
    private Protein protein;
    private Set<Disease> diseases;

    @AfterEach
    void cleanUp(){
        if(this.protein != null){
            executeInsideTransaction(dao -> dao.delete(this.protein), this.proteinDAO);
            this.protein = null;
        }

        if(this.diseases != null && !this.diseases.isEmpty()){
            this.diseases.forEach(disease -> executeInsideTransaction(dao -> dao.delete(disease), this.diseaseDAO));
            this.diseases = null;
        }
    }

    @Test
    void createProteinWithDiseases(){
        this.protein = ProteinTest.createProteinObject(UUID.randomUUID().toString());
        // create 5 diseases
        this.diseases = new HashSet<>();
        IntStream.range(1, 6).forEach(i -> this.diseases.add(createDisease(new Random().nextInt())));

        this.protein.setDiseases(this.diseases);
        executeInsideTransaction(dao -> dao.createOrUpdate(this.protein), this.proteinDAO);
        assertNotNull(this.protein.getId(), "unable to create protein");

        // get the disease
        Optional<Protein> storedProtein = this.proteinDAO.get(this.protein.getId());
        assertTrue(storedProtein.isPresent(), "unable to get the protein");
        verifyProtein(this.protein, storedProtein.get());
        verifyDiseases(storedProtein.get().getDiseases());
    }

    @Test
    void testGetProteinById(){
        this.protein = ProteinTest.createProteinObject(UUID.randomUUID().toString());
        executeInsideTransaction(dao -> dao.createOrUpdate(this.protein), this.proteinDAO);
        assertNotNull(this.protein.getId());

        Optional<Protein> optProtein = this.proteinDAO.getProteinById(this.protein.getProteinId());
        assertTrue(optProtein.isPresent());
        verifyProtein(this.protein, optProtein.get());
    }

    @Test
    void testGetProteinByAccession(){
        this.protein = ProteinTest.createProteinObject(UUID.randomUUID().toString());
        executeInsideTransaction(dao -> dao.createOrUpdate(this.protein), this.proteinDAO);
        assertNotNull(this.protein.getId());

        Optional<Protein> optProtein = this.proteinDAO.getProteinByAccession(this.protein.getAccession());
        assertTrue(optProtein.isPresent());
        verifyProtein(this.protein, optProtein.get());
    }

    @Test
    void testGetNonExistentProteinById(){
        String randomPID = UUID.randomUUID().toString() + new Random().nextInt();
        Optional<Protein> optProtein = this.proteinDAO.getProteinById(randomPID);
        assertFalse(optProtein.isPresent());
    }

    @Test
    void testGetNonExistentProteinByAccession(){
        String randomAcc = UUID.randomUUID().toString() + new Random().nextInt();
        Optional<Protein> optProtein = this.proteinDAO.getProteinByAccession(randomAcc);
        assertFalse(optProtein.isPresent());
    }


    private void verifyDiseases(Set<Disease> diseases) {
        assertEquals(5, diseases.size());
        diseases.forEach(disease -> assertNotNull(disease.getId()));
    }

    private void verifyProtein(Protein actual, Protein expected) {
        assertEquals(actual.getId(), expected.getId());
        assertEquals(actual.getProteinId(), expected.getProteinId());
        assertEquals(actual.getName(), expected.getName());
        assertEquals(actual.getAccession(), expected.getAccession());
        assertEquals(actual.getDesc(), expected.getDesc());
        assertEquals(actual.getGene(), expected.getGene());
        assertEquals(actual.getCreatedAt(), expected.getCreatedAt());
        assertEquals(actual.getUpdatedAt(), expected.getUpdatedAt());
    }

    private Disease createDisease(int nextInt) {
        Disease dis = DiseaseTest.createDiseaseObject(nextInt);
        return dis;
    }
}
