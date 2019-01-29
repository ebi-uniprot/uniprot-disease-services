/*
 * Created by sahmad on 1/25/19 11:23 AM
 * UniProt Consortium.
 * Copyright (c) 2002-2019.
 *
 */

package uk.ac.ebi.uniprot.ds.dao;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.ac.ebi.uniprot.ds.model.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class PathwayDAOImplTest{
    @Autowired
    private PathwayDAO pathwayDAO;
    @Autowired
    private ProteinDAO proteinDAO;
    private Pathway pathway;
    private List<Pathway> pathwayList;

    private Protein protein;

    @AfterEach
    void cleanUp(){
        if(this.pathway != null){
            this.pathwayDAO.delete(this.pathway);
            this.pathway = null;
        }

        if(this.pathwayList != null && !this.pathwayList.isEmpty()){
            this.pathwayList.forEach(i -> this.pathwayDAO.delete(i));
            this.pathwayList = null;
        }


        if(this.protein != null){
            this.proteinDAO.delete(this.protein);
            this.protein = null;

        }
    }

    @Test
    void createPathwayWithoutProtein(){
        this.pathway = PathwayTest.createPathwayObject(UUID.randomUUID().toString());
        this.pathwayDAO.save(this.pathway);
        assertNotNull(this.pathway.getId(), "unable to create pathway");

        // get and verify the pathway
        Optional<Pathway> optInter = this.pathwayDAO.findById(this.pathway.getId());
        assertTrue(optInter.isPresent(), "unable to get the pathway");
        verifyPathway(this.pathway, optInter.get());
    }

    @Test
    void testCreateMultiplePathwaysWithAProtein(){
        // create a protein
        this.protein = ProteinTest.createProteinObject(UUID.randomUUID().toString());
         this.proteinDAO.save(this.protein);
        assertNotNull(this.protein.getId(), "unable to create the protein");

        // create 10 Pathways
        this.pathwayList = new ArrayList<>();
        IntStream.range(1, 11).forEach(i -> {
            Pathway storedPath = PathwayTest.createPathwayObject(UUID.randomUUID().toString());
            storedPath.setProtein(this.protein);
            this.pathwayDAO.save(storedPath);
            assertNotNull(storedPath.getId(), "unable to create pathway");
            this.pathwayList.add(storedPath);
        });

        // get Pathways by protein
        List<Pathway> storedProteins = this.pathwayDAO.findAllByProtein(this.protein);
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
