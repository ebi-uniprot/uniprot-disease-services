/*
 * Created by sahmad on 23/01/19 16:10
 * UniProt Consortium.
 * Copyright (c) 2002-2019.
 *
 */

package uk.ac.ebi.uniprot.ds.model;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import javax.persistence.EntityTransaction;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class InteractionTest extends BaseTest {

    private Interaction in;
    private Protein pr;

    @AfterEach
    void cleanUp(){
        EntityTransaction txn = em.getTransaction();
        txn.begin();
        em.remove(in);
        em.remove(pr);
        txn.commit();
    }

    @Test
    void testCreateInteraction(){
        pr = createProtein();
        in = new Interaction();
        String type = "TYPE-" + random;
        String gene = "G-" + random;
        int count = random;
        String first = "F-" + random;
        String second = "S-" + random;
        in.setType(type);
        in.setGene(gene);
        in.setAccession("ACC-" + random);
        in.setExperimentCount(count);
        in.setFirstInteractor(first);
        in.setSecondInteractor(second);
        in.setProtein(pr);

        // persist
        EntityTransaction txn = em.getTransaction();
        txn.begin();
        em.persist(in);
        txn.commit();

        assertNotNull(in.getId(), "unable to create the interaction record");
        assertNotNull(in.getProtein());
    }

    private Protein createProtein() {
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

        EntityTransaction txn = em.getTransaction();
        txn.begin();
        em.persist(protein);
        txn.commit();

        assertNotNull(protein.getId(), "unable to create the protein record");
        return protein;
    }
}
