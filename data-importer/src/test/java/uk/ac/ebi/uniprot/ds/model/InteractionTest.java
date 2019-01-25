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

import java.util.Random;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class InteractionTest extends BaseTest {

    private Interaction interaction;
    private Protein pr;

    @AfterEach
    void cleanUp(){
        EntityTransaction txn = em.getTransaction();
        txn.begin();
        em.remove(interaction);
        em.remove(pr);
        txn.commit();
    }

    @Test
    void testCreateInteraction(){
        String uuid = UUID.randomUUID().toString();
        this.pr = ProteinTest.createProteinObject(String.valueOf(random));
        EntityTransaction txn = em.getTransaction();
        txn.begin();
        em.persist(this.pr);
        txn.commit();
        assertNotNull(this.pr.getId(), "unable to create a protein");

        this.interaction = createInteractionObject(uuid);
        this.interaction.setProtein(pr);

        // persist
        txn = em.getTransaction();
        txn.begin();
        em.persist(this.interaction);
        txn.commit();

        assertNotNull(this.interaction.getId(), "unable to create the interaction record");
        assertNotNull(this.interaction.getProtein());
    }


    public static Interaction createInteractionObject(String random) {
        Interaction inter = new Interaction();
        String type = "TYPE-" + random;
        String gene = "G-" + random;
        int count = new Random().nextInt();
        String first = "F-" + random;
        String second = "S-" + random;
        inter.setType(type);
        inter.setGene(gene);
        inter.setAccession("ACC-" + random);
        inter.setExperimentCount(count);
        inter.setFirstInteractor(first);
        inter.setSecondInteractor(second);
        return inter;
    }
}
