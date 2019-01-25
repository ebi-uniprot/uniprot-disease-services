/*
 * Created by sahmad on 1/25/19 9:18 AM
 * UniProt Consortium.
 * Copyright (c) 2002-2019.
 *
 */

package uk.ac.ebi.uniprot.ds.dao;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import uk.ac.ebi.uniprot.ds.dao.impl.FeatureLocationDAOImpl;
import uk.ac.ebi.uniprot.ds.model.*;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class FeatureLocationDAOImplTest extends BaseTest {
    private FeatureLocationDAOImpl featureLocationDAO = new FeatureLocationDAOImpl(BaseTest.em);
    private FeatureLocation featureLocation;

    @AfterEach
    void cleanUp(){
        if(this.featureLocation != null){
            executeInsideTransaction(dao -> dao.delete(this.featureLocation), this.featureLocationDAO);
            this.featureLocation = null;
        }
    }

    @Test
    void testCreateFeatureLocation(){
        this.featureLocation = FeatureLocationTest.createFeatureLocationObject(UUID.randomUUID().toString());
        executeInsideTransaction(dao -> dao.createOrUpdate(this.featureLocation), this.featureLocationDAO);
        assertNotNull(this.featureLocation.getId(), "unable to create the evidence");

        // get the evidence and verify
        Optional<FeatureLocation> optStoredFL = this.featureLocationDAO.get(this.featureLocation.getId());
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
