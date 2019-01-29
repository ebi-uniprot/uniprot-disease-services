/*
 * Created by sahmad on 23/01/19 12:04
 * UniProt Consortium.
 * Copyright (c) 2002-2019.
 *
 */

package uk.ac.ebi.uniprot.ds.model;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.persistence.EntityTransaction;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

public class FeatureLocationTest extends BaseTest{

    private FeatureLocation fl;

    @AfterEach
    void cleanUp(){
        EntityTransaction txn = em.getTransaction();
        txn.begin();
        em.remove(fl);
        txn.commit();
    }

    @Disabled
    @Test
    @DisplayName("Test create a feature_location record")
    void testCreateFeatureLocation() {
        fl = createFeatureLocationObject();
        // save the feature location in db
        EntityTransaction txn = em.getTransaction();
        txn.begin();
        em.persist(fl);
        txn.commit();
        assertNotNull(fl.getId(), "unable to create the record");

    }

    public static FeatureLocation createFeatureLocationObject() {
       return createFeatureLocationObject(String.valueOf(random));
    }

    public static FeatureLocation createFeatureLocationObject(String uuid) {
        String sm = "SM-" + uuid;
        String em = "EM-" + uuid;
        int si = new Random().nextInt();
        int ei = si + 5;
        FeatureLocation fl = new FeatureLocation();
        fl.setStartModifier(sm);
        fl.setEndModifier(em);
        fl.setStartId(si);
        fl.setEndId(ei);
        return fl;
    }
}
