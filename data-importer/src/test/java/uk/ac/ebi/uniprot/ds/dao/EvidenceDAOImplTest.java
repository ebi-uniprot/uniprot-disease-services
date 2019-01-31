/*
 * Created by sahmad on 1/25/19 8:53 AM
 * UniProt Consortium.
 * Copyright (c) 2002-2019.
 *
 */

package uk.ac.ebi.uniprot.ds.dao;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.ac.ebi.uniprot.ds.model.Evidence;
import uk.ac.ebi.uniprot.ds.model.EvidenceTest;

import java.util.Optional;
import java.util.UUID;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class EvidenceDAOImplTest{

    @Autowired
    private EvidenceDAO evidenceDAO;
    private Evidence evidence;

    @AfterEach
    void cleanUp(){
        if(this.evidence != null){
            this.evidenceDAO.deleteById(this.evidence.getId());
            this.evidence = null;
        }
    }

    @Test
    void testCreateEvidence(){
        this.evidence = EvidenceTest.createEvidenceObject(UUID.randomUUID().toString());
        this.evidenceDAO.save(this.evidence);
        assertNotNull(this.evidence.getId(), "unable to create the evidence");

        // get the evidence and verify
        Optional<Evidence> optStoredEv = this.evidenceDAO.findById(this.evidence.getId());
        assertTrue(optStoredEv.isPresent(), "unable to get the evidence");

        verifyEvidence(this.evidence, optStoredEv.get());
    }

    private void verifyEvidence(Evidence actual, Evidence expected) {
        assertEquals(actual.getId(), expected.getId());
        assertEquals(actual.getEvidenceId(), expected.getEvidenceId());
        assertEquals(actual.getType(), expected.getType());
        assertEquals(actual.getAttribute(), expected.getAttribute());
        assertEquals(actual.getCode(), expected.getCode());
        assertEquals(actual.getUseECOCode(), expected.getUseECOCode());
        assertEquals(actual.getTypeValue(), expected.getTypeValue());
        assertEquals(actual.getHasTypeValue(), expected.getHasTypeValue());
        assertEquals(actual.getCreatedAt(), expected.getCreatedAt());
        assertEquals(actual.getUpdatedAt(), expected.getUpdatedAt());
    }
}
