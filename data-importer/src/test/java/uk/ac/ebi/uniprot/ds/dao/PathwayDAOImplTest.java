/*
 * Created by sahmad on 1/25/19 11:23 AM
 * UniProt Consortium.
 * Copyright (c) 2002-2019.
 *
 */

package uk.ac.ebi.uniprot.ds.dao;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import uk.ac.ebi.uniprot.ds.dao.impl.InteractionDAOImpl;
import uk.ac.ebi.uniprot.ds.dao.impl.PathwayDAOImpl;
import uk.ac.ebi.uniprot.ds.dao.impl.ProteinDAOImpl;
import uk.ac.ebi.uniprot.ds.model.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

public class PathwayDAOImplTest extends BaseTest {
    private PathwayDAO pathwayDAO = new PathwayDAOImpl(BaseTest.em);
    private ProteinDAO proteinDAO = new ProteinDAOImpl(BaseTest.em);
    private Pathway pathway;
    private List<Pathway> pathwayList;

    private Protein protein;

    @AfterEach
    void cleanUp(){
        if(this.pathway != null){
            executeInsideTransaction(dao -> dao.delete(this.pathway), this.pathwayDAO);
            this.pathway = null;
        }

        if(this.pathwayList != null && !this.pathwayList.isEmpty()){
            this.pathwayList.forEach(i -> executeInsideTransaction(dao -> dao.delete(i), this.pathwayDAO));
            this.pathwayList = null;
        }


        if(this.protein != null){
            executeInsideTransaction(dao -> dao.delete(this.protein), this.proteinDAO);
            this.protein = null;

        }
    }

    @Test
    void createPathwayWithoutProtein(){
        this.pathway = PathwayTest.createPathwayObject(UUID.randomUUID().toString());
        executeInsideTransaction(dao -> dao.createOrUpdate(this.pathway), this.pathwayDAO);
        assertNotNull(this.pathway.getId(), "unable to create pathway");

        // get and verify the pathway
        Optional<Pathway> optInter = this.pathwayDAO.get(this.pathway.getId());
        assertTrue(optInter.isPresent(), "unable to get the pathway");
        verifyPathway(this.pathway, optInter.get());
    }

    @Test
    void testCreateMultiplePathwaysWithAProtein(){
        // create a protein
        this.protein = ProteinTest.createProteinObject(UUID.randomUUID().toString());
        executeInsideTransaction(dao -> dao.createOrUpdate(this.protein), this.proteinDAO);
        assertNotNull(this.protein.getId(), "unable to create the protein");

        // create 10 Pathways
        this.pathwayList = new ArrayList<>();
        IntStream.range(1, 11).forEach(i -> {
            Pathway storedPath = PathwayTest.createPathwayObject(UUID.randomUUID().toString());
            storedPath.setProtein(this.protein);
            executeInsideTransaction(dao -> dao.createOrUpdate(storedPath), this.pathwayDAO);
            assertNotNull(storedPath.getId(), "unable to create pathway");
            this.pathwayList.add(storedPath);
        });

        // get Pathways by protein
        List<Pathway> storedProteins = this.pathwayDAO.getPathwaysByProtein(this.protein);
        assertFalse(storedProteins.isEmpty(), "unable to get list of pathways");
        assertEquals(10, storedProteins.size());
    }

    private void verifyPathway(Pathway actual, Pathway expected) {
        assertEquals(actual.getId(), expected.getId());
        assertEquals(actual.getPrimaryId(), expected.getPrimaryId());
        assertEquals(actual.getDesc(), expected.getDesc());
        assertEquals(actual.getDbType(), expected.getDbType());
        assertEquals(actual.getIsoformId(), expected.getIsoformId());
        assertEquals(actual.getThird(), expected.getThird());
        assertEquals(actual.getFourth(), expected.getFourth());
        assertEquals(actual.getProtein(), expected.getProtein());
        assertEquals(actual.getCreatedAt(), expected.getCreatedAt());
        assertEquals(actual.getUpdatedAt(), expected.getUpdatedAt());
    }

}
