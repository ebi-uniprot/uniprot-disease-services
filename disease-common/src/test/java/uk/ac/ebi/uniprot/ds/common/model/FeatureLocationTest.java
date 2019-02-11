/*
 * Created by sahmad on 07/02/19 11:00
 * UniProt Consortium.
 * Copyright (c) 2002-2019.
 *
 */

package uk.ac.ebi.uniprot.ds.common.model;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityTransaction;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertNotNull;
@ExtendWith(SpringExtension.class)
@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@Transactional
public class FeatureLocationTest extends BaseTest{

    private FeatureLocation fl;

    @AfterEach
    void cleanUp(){
        em.remove(fl);
    }

    @Test
    @DisplayName("Test create a feature_location record")
    void testCreateFeatureLocation() {
        fl = createFeatureLocationObject();
        // save the feature location in db
        em.persist(fl);
        em.flush();
        assertNotNull(fl.getId(), "unable to create the record");
    }

    public static FeatureLocation createFeatureLocationObject() {
       return createFeatureLocationObject(BaseTest.random);
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
