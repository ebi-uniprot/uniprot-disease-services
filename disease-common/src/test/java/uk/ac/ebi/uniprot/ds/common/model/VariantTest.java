/*
 * Created by sahmad on 07/02/19 11:00
 * UniProt Consortium.
 * Copyright (c) 2002-2019.
 *
 */

package uk.ac.ebi.uniprot.ds.common.model;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityTransaction;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@Transactional
public class VariantTest extends BaseTest {
    private Variant variant;
    private FeatureLocation fl;
    private Evidence ev;
    private Protein pr;
    private Disease ds;

    @AfterEach
    void cleanUp(){
        em.remove(variant);
        em.remove(fl);
        em.remove(ev);
        em.remove(pr);
        em.remove(ds);
    }

    @Test
    void testCreateVariant(){
        // create evidence object
        ev = EvidenceTest.createEvidenceObject();
        fl = FeatureLocationTest.createFeatureLocationObject();
        pr = ProteinTest.createProteinObject();
        ds = DiseaseTest.createDiseaseObject();
        variant = createVariantObject();
        variant.addEvidence(ev);
        variant.setFeatureLocation(fl);
        variant.setProtein(pr);
        variant.setDisease(ds);

        // save it
        em.persist(ev);
        em.persist(fl);
        em.persist(ds);
        em.persist(pr);
        em.persist(variant);
        em.flush();
        Assertions.assertNotNull(variant.getId());
        //Assertions.assertEquals(ev.getId(), variant.getEvidence().getId());
        Assertions.assertEquals(fl.getId(), variant.getFeatureLocation().getId());
        Assertions.assertEquals(pr.getId(), variant.getProtein().getId());
        Assertions.assertEquals(ds.getId(), variant.getDisease().getId());
    }

    public static Variant createVariantObject(String uuid){
        String os = "OS-" + uuid;
        String as = "AS-" + uuid;
        String fid = "FID-" + uuid;
        String fs = "FS-" + uuid;
        String vr = "VR-" + uuid;

        Variant variant = new Variant();
        variant.setOrigSeq(os);
        variant.setAltSeq(as);
        variant.setFeatureId(fid);
        variant.setFeatureStatus(fs);
        variant.setReport(vr);
        return variant;
    }
    public static Variant createVariantObject(){
        return createVariantObject(String.valueOf(random));
    }
}
