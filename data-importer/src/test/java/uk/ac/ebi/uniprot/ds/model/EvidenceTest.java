/*
 * Created by sahmad on 23/01/19 15:51
 * UniProt Consortium.
 * Copyright (c) 2002-2019.
 *
 */

package uk.ac.ebi.uniprot.ds.model;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import javax.persistence.EntityTransaction;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;


public class EvidenceTest extends BaseTest {
    private Evidence ev;

    @AfterEach
    void cleanUp(){
        EntityTransaction txn = em.getTransaction();
        txn.begin();
        em.remove(ev);
        txn.commit();
    }

    @Disabled
    @Test
    void testCreateAndGetEvidence(){
        ev = createEvidenceObject();
        EntityTransaction txn = em.getTransaction();
        txn.begin();
        em.persist(ev);
        txn.commit();
        assertNotNull(ev.getId(), "unable to create the evidence");

        // get and verify the evidence
        Evidence dbEvidence = em.find(Evidence.class, ev.getId());
        assertNotNull(dbEvidence, "unable to get the evidence from db by id");
        assertEquals(ev.getEvidenceId(), dbEvidence.getEvidenceId());
        assertEquals(ev.getType(), dbEvidence.getType());
        assertEquals(ev.getAttribute(), dbEvidence.getAttribute());
        assertEquals(ev.getCode(), dbEvidence.getCode());
        assertEquals(ev.getUseECOCode(), dbEvidence.getUseECOCode());
        assertEquals(ev.getTypeValue(), dbEvidence.getTypeValue());
        assertEquals(ev.getHasTypeValue(), dbEvidence.getHasTypeValue());
    }

    private static Boolean getBoolean(int i) {
        return i % 2 == 0 ? true : false;
    }

    public static Evidence createEvidenceObject() {
        return createEvidenceObject(String.valueOf(random));
    }

    public static Evidence createEvidenceObject(String uuid){
        String eId = "EID-" + uuid;
        String eType = "TYPE-" + uuid;
        String eCode = "ECODE-" + uuid;
        Boolean useCode = new Random().nextInt() % 2 == 0 ? true : false;
        String tVal = "TVAL-" + uuid;
        Boolean hasTVal = new Random().nextInt() % 2 == 0 ? true : false;
        String attrib = "ATTRIB-" + uuid;

        Evidence eObj = new Evidence();
        eObj.setEvidenceId(eId);
        eObj.setType(eType);
        eObj.setCode(eCode);
        eObj.setUseECOCode(useCode);
        eObj.setTypeValue(tVal);
        eObj.setHasTypeValue(hasTVal);
        eObj.setAttribute(attrib);

        return eObj;
    }
}
