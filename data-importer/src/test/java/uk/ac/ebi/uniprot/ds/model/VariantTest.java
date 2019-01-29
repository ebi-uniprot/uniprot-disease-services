/*
 * Created by sahmad on 23/01/19 16:46
 * UniProt Consortium.
 * Copyright (c) 2002-2019.
 *
 */

package uk.ac.ebi.uniprot.ds.model;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import javax.persistence.EntityTransaction;

public class VariantTest extends BaseTest {
    private Variant variant;
    private FeatureLocation fl;
    private Evidence ev;
    private Protein pr;
    private Disease ds;

    @AfterEach
    void cleanUp(){
        EntityTransaction txn = em.getTransaction();
        txn.begin();
        em.remove(variant);
        em.remove(fl);
        em.remove(ev);
        em.remove(pr);
        em.remove(ds);
        txn.commit();
    }

    @Disabled
    @Test
    void testCreateVariant(){
        // create evidence object
        ev = EvidenceTest.createEvidenceObject();
        fl = FeatureLocationTest.createFeatureLocationObject();
        pr = ProteinTest.createProteinObject();
        ds = DiseaseTest.createDiseaseObject();
        variant = createVariantObject();
        variant.setEvidence(ev);
        variant.setFeatureLocation(fl);
        variant.setProtein(pr);
        variant.setDisease(ds);

        // save it
        EntityTransaction txn = em.getTransaction();
        txn.begin();
        em.persist(ev);
        em.persist(fl);
        em.persist(ds);
        em.persist(pr);
        em.persist(variant);
        txn.commit();
        Assertions.assertNotNull(variant.getId());
        Assertions.assertEquals(ev.getId(), variant.getEvidence().getId());
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
