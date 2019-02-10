/*
 * Created by sahmad on 07/02/19 11:00
 * UniProt Consortium.
 * Copyright (c) 2002-2019.
 *
 */

package uk.ac.ebi.uniprot.ds.common.model;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@Transactional
public class EvidenceTest extends BaseTest {
    private Evidence ev;

    @AfterEach
    void cleanUp(){
        em.remove(ev);
    }

    @Test
    void testCreateAndGetEvidence(){
        ev = createEvidenceObject();
        em.persist(ev);
        em.flush();
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
