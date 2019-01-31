/*
 * Created by sahmad on 1/25/19 9:18 AM
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

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class FeatureLocationDAOImplTest {

    @Autowired
    private FeatureLocationDAO featureLocationDAO;
    private FeatureLocation featureLocation;

    @AfterEach
    void cleanUp(){
        if(this.featureLocation != null){
            this.featureLocationDAO.deleteById(this.featureLocation.getId());
            this.featureLocation = null;
        }
    }

    @Test
    void testCreateFeatureLocation(){
        this.featureLocation = FeatureLocationTest.createFeatureLocationObject(UUID.randomUUID().toString());
        this.featureLocationDAO.save(this.featureLocation);
        assertNotNull(this.featureLocation.getId(), "unable to create the evidence");

        // get the evidence and verify
        Optional<FeatureLocation> optStoredFL = this.featureLocationDAO.findById(this.featureLocation.getId());
        assertTrue(optStoredFL.isPresent(), "unable to get the featureLocation");

        verifyFeatureLocation(this.featureLocation, optStoredFL.get());
    }

    private void verifyFeatureLocation(FeatureLocation actual, FeatureLocation expected) {
        assertEquals(actual.getId(), expected.getId());
        assertEquals(actual.getStartModifier(), expected.getStartModifier());
        assertEquals(actual.getEndModifier(), expected.getEndModifier());
        assertEquals(actual.getStartId(), expected.getStartId());
        assertEquals(actual.getEndId(), expected.getEndId());
        assertEquals(actual.getCreatedAt(), expected.getCreatedAt());
        assertEquals(actual.getUpdatedAt(), expected.getUpdatedAt());
    }
}
