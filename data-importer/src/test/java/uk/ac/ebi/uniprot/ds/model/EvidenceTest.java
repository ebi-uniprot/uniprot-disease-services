/*
 * Created by sahmad on 23/01/19 15:51
 * UniProt Consortium.
 * Copyright (c) 2002-2019.
 *
 */

package uk.ac.ebi.uniprot.ds.model;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import javax.persistence.EntityTransaction;

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
        String eid = "EID-" + random;
        String type = "TYPE-" + random;
        String attr = "ATTR-" + random;
        String code = "CODE-" + random;
        Boolean useCode = getBoolean(random);
        String tv = "TV-" + random;
        Boolean hasTV = !getBoolean(random);

        // create evidence
        Evidence ev = new Evidence();
        ev.setEvidenceId(eid);
        ev.setType(type);
        ev.setAttribute(attr);
        ev.setCode(code);
        ev.setUseECOCode(useCode);
        ev.setTypeValue(tv);
        ev.setHasTypeValue(hasTV);

        return ev;
    }
}
