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
import uk.ac.ebi.uniprot.ds.common.model.ProteinCrossRef;
import uk.ac.ebi.uniprot.ds.common.model.ProteinCrossRefTest;
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
public class ProteinCrossRefDAOTest {
    @Autowired
    private ProteinCrossRefDAO proteinCrossRefDAO;
    @Autowired
    private ProteinDAO proteinDAO;
    private ProteinCrossRef proteinCrossRef;
    private List<ProteinCrossRef> proteinCrossRefs;

    private Protein protein;

    @AfterEach
    void cleanUp(){
        if(this.proteinCrossRef != null){
            this.proteinCrossRefDAO.deleteById(this.proteinCrossRef.getId());
            this.proteinCrossRef = null;
        }

        if(this.proteinCrossRefs != null && !this.proteinCrossRefs.isEmpty()){
            this.proteinCrossRefs.forEach(i -> this.proteinCrossRefDAO.deleteById(i.getId()));
            this.proteinCrossRefs = null;
        }


        if(this.protein != null){
            this.proteinDAO.delete(this.protein);
            this.protein = null;

        }
    }

    @Test
    void createCrossRefWithoutProtein(){
        this.proteinCrossRef = ProteinCrossRefTest.createProteinCrossRefObject(UUID.randomUUID().toString());
        this.proteinCrossRefDAO.save(this.proteinCrossRef);
        assertNotNull(this.proteinCrossRef.getId(), "unable to create protein cross ref");

        // get and verify the protein cross ref
        Optional<ProteinCrossRef> optInter = this.proteinCrossRefDAO.findById(this.proteinCrossRef.getId());
        assertTrue(optInter.isPresent(), "unable to get the protein cross ref");
        verifyCrossRef(this.proteinCrossRef, optInter.get());
    }

    @Test
    void testCreateMultipleCrossRefsWithAProtein(){
        // create a protein
        this.protein = ProteinTest.createProteinObject(UUID.randomUUID().toString());
         this.proteinDAO.save(this.protein);
        assertNotNull(this.protein.getId(), "unable to create the protein");

        // create 10 protein cross ref
        this.proteinCrossRefs = new ArrayList<>();
        IntStream.range(1, 11).forEach(i -> {
            ProteinCrossRef storedPath = ProteinCrossRefTest.createProteinCrossRefObject(UUID.randomUUID().toString());
            storedPath.setProtein(this.protein);
            this.proteinCrossRefDAO.save(storedPath);
            assertNotNull(storedPath.getId(), "unable to create protein cross ref");
            this.proteinCrossRefs.add(storedPath);
        });

        // get protein cross refs by protein
        List<ProteinCrossRef> storedCrossRefs = this.proteinCrossRefDAO.findAllByProtein(this.protein);
        assertFalse(storedCrossRefs.isEmpty(), "unable to get list of protein cross refs");
        assertEquals(10, storedCrossRefs.size());
    }

    @Test
    void shouldGetMultipleCrossRefsByPrimaryId(){
        // create 3 proteins
        String uuid = UUID.randomUUID().toString();
        Protein p1 = ProteinTest.createProteinObject(uuid + 1);
        this.proteinDAO.save(p1);
        assertNotNull(p1.getId(), "unable to create the protein");

        Protein p2 = ProteinTest.createProteinObject(uuid + 2);
        this.proteinDAO.save(p2);
        assertNotNull(p2.getId(), "unable to create the protein");

        Protein p3 = ProteinTest.createProteinObject(uuid + 3);
        this.proteinDAO.save(p3);
        assertNotNull(p3.getId(), "unable to create the protein");

        // add xref in each protein with same primary id

        ProteinCrossRef xref1 = ProteinCrossRefTest.createProteinCrossRefObject(uuid);
        xref1.setProtein(p1);
        this.proteinCrossRefDAO.save(xref1);
        assertNotNull(xref1.getId(), "unable to create protein cross ref");

        ProteinCrossRef xref2 = ProteinCrossRefTest.createProteinCrossRefObject(uuid);
        xref2.setProtein(p2);
        this.proteinCrossRefDAO.save(xref2);
        assertNotNull(xref2.getId(), "unable to create protein cross ref");

        ProteinCrossRef xref3 = ProteinCrossRefTest.createProteinCrossRefObject(uuid);
        xref3.setProtein(p3);
        this.proteinCrossRefDAO.save(xref3);
        assertNotNull(xref3.getId(), "unable to create protein cross ref");

        // get protein cross refs by protein
        String pId = "PID-" + uuid;
        List<ProteinCrossRef> storedCrossRefs = this.proteinCrossRefDAO.findAllByPrimaryId(pId);
        assertFalse(storedCrossRefs.isEmpty(), "unable to get list of protein cross refs");
        assertEquals(3, storedCrossRefs.size());

        // clean up
        this.proteinCrossRefDAO.deleteById(xref1.getId());
        this.proteinCrossRefDAO.deleteById(xref2.getId());
        this.proteinCrossRefDAO.deleteById(xref3.getId());
        this.proteinDAO.deleteById(p1.getId());
        this.proteinDAO.deleteById(p2.getId());
        this.proteinDAO.deleteById(p3.getId());
    }

    private void verifyCrossRef(ProteinCrossRef actual, ProteinCrossRef expected) {
        assertEquals(actual.getId(), expected.getId());
        assertEquals(actual.getPrimaryId(), expected.getPrimaryId());
        assertEquals(actual.getDescription(), expected.getDescription());
        assertEquals(actual.getDbType(), expected.getDbType());
        assertEquals(actual.getIsoformId(), expected.getIsoformId());
        assertEquals(actual.getThird(), expected.getThird());
        assertEquals(actual.getFourth(), expected.getFourth());
        assertEquals(actual.getProtein(), expected.getProtein());
        assertEquals(actual.getCreatedAt(), expected.getCreatedAt());
        assertEquals(actual.getUpdatedAt(), expected.getUpdatedAt());
    }

}
