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
import uk.ac.ebi.uniprot.ds.dao.impl.EvidenceDAOImpl;
import uk.ac.ebi.uniprot.ds.model.BaseTest;
import uk.ac.ebi.uniprot.ds.model.Evidence;
import uk.ac.ebi.uniprot.ds.model.EvidenceTest;

import java.util.Optional;
import java.util.UUID;

public class EvidenceDAOImplTest extends BaseTest {
    private EvidenceDAOImpl evidenceDAO = new EvidenceDAOImpl(BaseTest.em);
    private Evidence evidence;

    @AfterEach
    void cleanUp(){
        if(this.evidence != null){
            executeInsideTransaction(dao -> dao.delete(this.evidence), this.evidenceDAO);
            this.evidence = null;
        }
    }

    @Test
    void testCreateEvidence(){
        this.evidence = EvidenceTest.createEvidenceObject(UUID.randomUUID().toString());
        executeInsideTransaction(dao -> dao.createOrUpdate(this.evidence), this.evidenceDAO);
        assertNotNull(this.evidence.getId(), "unable to create the evidence");

        // get the evidence and verify
        Optional<Evidence> optStoredEv = this.evidenceDAO.get(this.evidence.getId());
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
