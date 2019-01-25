/*
 * Created by sahmad on 1/25/19 9:38 AM
 * UniProt Consortium.
 * Copyright (c) 2002-2019.
 *
 */

package uk.ac.ebi.uniprot.ds.dao;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import uk.ac.ebi.uniprot.ds.dao.impl.InteractionDAOImpl;
import uk.ac.ebi.uniprot.ds.dao.impl.ProteinDAOImpl;
import uk.ac.ebi.uniprot.ds.model.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

public class InteractionDAOImplTest extends BaseTest {
    private InteractionDAO interactionDAO = new InteractionDAOImpl(BaseTest.em);
    private ProteinDAO proteinDAO = new ProteinDAOImpl(BaseTest.em);
    private Interaction interaction;
    private List<Interaction> interactionList;

    private Protein protein;

    @AfterEach
    void cleanUp(){
        if(this.interaction != null){
            executeInsideTransaction(dao -> dao.delete(this.interaction), this.interactionDAO);
            this.interaction = null;
        }

        if(this.interactionList != null && !this.interactionList.isEmpty()){
            this.interactionList.forEach(i -> executeInsideTransaction(dao -> dao.delete(i), this.interactionDAO));
            this.interactionList = null;
        }


        if(this.protein != null){
            executeInsideTransaction(dao -> dao.delete(this.protein), this.proteinDAO);
            this.protein = null;

        }

    }

    @Test
    void createInteractionWithoutProtein(){
        this.interaction = InteractionTest.createInteractionObject(UUID.randomUUID().toString());
        executeInsideTransaction(dao -> dao.createOrUpdate(this.interaction), this.interactionDAO);
        assertNotNull(this.interaction.getId(), "unable to create interaction");

        // get and verify the interaction
        Optional<Interaction> optInter = this.interactionDAO.get(this.interaction.getId());
        assertTrue(optInter.isPresent(), "unable to get the interaction");
        verifyInteraction(this.interaction, optInter.get());
    }

    @Test
    void testCreateMultipleInteractionsWithAProtein(){
        // create a protein
        this.protein = ProteinTest.createProteinObject(UUID.randomUUID().toString());
        executeInsideTransaction(dao -> dao.createOrUpdate(this.protein), this.proteinDAO);
        assertNotNull(this.protein.getId(), "unable to create the protein");

        // create 10 interactions
        this.interactionList = new ArrayList<>();
        IntStream.range(1, 11).forEach(i -> {
            Interaction storedInt = InteractionTest.createInteractionObject(UUID.randomUUID().toString());
            storedInt.setProtein(this.protein);
            executeInsideTransaction(dao -> dao.createOrUpdate(storedInt), this.interactionDAO);
            assertNotNull(storedInt.getId(), "unable to create interaction");
            this.interactionList.add(storedInt);
        });

        // get interactions by protein
        List<Interaction> storedProteins = this.interactionDAO.getInteractionsByProtein(this.protein);
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
