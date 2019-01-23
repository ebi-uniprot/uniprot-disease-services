/*
 * Created by sahmad on 23/01/19 14:39
 * UniProt Consortium.
 * Copyright (c) 2002-2019.
 *
 */

package uk.ac.ebi.uniprot.ds.model;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import javax.persistence.EntityTransaction;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ProteinTest extends BaseTest{
    private Protein protein;
    private Disease disease;



    @AfterEach
    void cleanUp(){
        EntityTransaction txn = em.getTransaction();
        txn.begin();
        em.remove(protein);
        em.remove(disease);
        txn.commit();
    }

    @Test
    void testCreateProtein() {
        // create the disease object
        disease = DiseaseTest.createDiseaseObject();

        // create protein
        protein = createProteinObject();

        Set<Disease> diseases = new HashSet<>();
        diseases.add(disease);
        protein.setDiseases(diseases);

        EntityTransaction txn = em.getTransaction();
        txn.begin();
        em.persist(protein);
        txn.commit();

        assertNotNull(protein.getId(), "unable to create the protein record");
        assertNotNull(disease.getId(), "unable to create the disease");
    }

    public static Protein createProteinObject() {

        // create protein
        Protein protein = new Protein();
        String pId = "PID-" + random;
        String pn = "PN-" + random;
        String acc = "ACC-" + random;
        String gene = "GENE-" + random;
        String pDesc = "PDESC-" + random;

        protein.setProteinId(pId);
        protein.setName(pn);
        protein.setAccession(acc);
        protein.setGene(gene);
        protein.setDesc(pDesc);
        return protein;
    }

}
