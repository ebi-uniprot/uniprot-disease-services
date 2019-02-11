/*
 * Created by sahmad on 07/02/19 11:00
 * UniProt Consortium.
 * Copyright (c) 2002-2019.
 *
 */

package uk.ac.ebi.uniprot.ds.common.model;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityTransaction;
import java.util.Random;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@Transactional
public class InteractionTest extends BaseTest {

    private Interaction interaction;
    private Protein pr;

    @AfterEach
    void cleanUp(){
        em.remove(interaction);
        em.remove(pr);
    }

    @Test
    void testCreateInteraction(){
        String uuid = UUID.randomUUID().toString();
        this.pr = ProteinTest.createProteinObject(BaseTest.random);
        em.persist(this.pr);
        em.flush();
        assertNotNull(this.pr.getId(), "unable to create a protein");

        this.interaction = createInteractionObject(uuid);
        this.interaction.setProtein(pr);

        // persist
        em.persist(this.interaction);
        em.flush();

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
