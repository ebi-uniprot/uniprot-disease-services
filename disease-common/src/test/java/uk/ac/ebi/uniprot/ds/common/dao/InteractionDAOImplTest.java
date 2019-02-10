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
import uk.ac.ebi.uniprot.ds.common.model.Interaction;
import uk.ac.ebi.uniprot.ds.common.model.InteractionTest;
import uk.ac.ebi.uniprot.ds.common.model.Protein;
import uk.ac.ebi.uniprot.ds.common.model.ProteinTest;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class InteractionDAOImplTest{

    @Autowired
    private InteractionDAO interactionDAO;
    @Autowired
    private ProteinDAO proteinDAO;

    private Interaction interaction;
    private List<Interaction> interactionList;

    private Protein protein;

    @AfterEach
    void cleanUp(){
        if(this.interaction != null){
            this.interactionDAO.deleteById(this.interaction.getId());
            this.interaction = null;
        }

        if(this.interactionList != null && !this.interactionList.isEmpty()){
            this.interactionList.forEach(i -> this.interactionDAO.deleteById(i.getId()));
            this.interactionList = null;
        }


        if(this.protein != null){
            this.proteinDAO.delete(this.protein);
            this.protein = null;

        }

    }

    @Test
    void createInteractionWithoutProtein(){
        this.interaction = InteractionTest.createInteractionObject(UUID.randomUUID().toString());
        this.interactionDAO.save(this.interaction);
        assertNotNull(this.interaction.getId(), "unable to create interaction");

        // get and verify the interaction
        Optional<Interaction> optInter = this.interactionDAO.findById(this.interaction.getId());
        assertTrue(optInter.isPresent(), "unable to get the interaction");
        verifyInteraction(this.interaction, optInter.get());
    }

    @Test
    void testCreateMultipleInteractionsWithAProtein(){
        // create a protein
        this.protein = ProteinTest.createProteinObject(UUID.randomUUID().toString());
        this.proteinDAO.save(this.protein);
        assertNotNull(this.protein.getId(), "unable to create the protein");

        // create 10 interactions
        this.interactionList = new ArrayList<>();
        IntStream.range(1, 11).forEach(i -> {
            Interaction storedInt = InteractionTest.createInteractionObject(UUID.randomUUID().toString());
            storedInt.setProtein(this.protein);
            this.interactionDAO.save(storedInt);
            assertNotNull(storedInt.getId(), "unable to create interaction");
            this.interactionList.add(storedInt);
        });

        // get interactions by protein
        List<Interaction> storedProteins = this.interactionDAO.findAllByProtein(this.protein);
        assertFalse(storedProteins.isEmpty(), "unable to get list of interactions");
        assertEquals(10, storedProteins.size());
    }

    private void verifyInteraction(Interaction actual, Interaction expected) {
        assertEquals(actual.getId(), expected.getId());
        assertEquals(actual.getType(), expected.getType());
        assertEquals(actual.getAccession(), expected.getAccession());
        assertEquals(actual.getGene(), expected.getGene());
        assertEquals(actual.getExperimentCount(), expected.getExperimentCount());
        assertEquals(actual.getFirstInteractor(), expected.getFirstInteractor());
        assertEquals(actual.getSecondInteractor(), expected.getSecondInteractor());
        assertEquals(actual.getProtein(), expected.getProtein());
        assertEquals(actual.getCreatedAt(), expected.getCreatedAt());
        assertEquals(actual.getUpdatedAt(), expected.getUpdatedAt());
    }

}
