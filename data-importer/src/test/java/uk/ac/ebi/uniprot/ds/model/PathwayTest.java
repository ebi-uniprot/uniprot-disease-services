/*
 * Created by sahmad on 23/01/19 16:18
 * UniProt Consortium.
 * Copyright (c) 2002-2019.
 *
 */

package uk.ac.ebi.uniprot.ds.model;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import javax.persistence.EntityTransaction;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class PathwayTest extends BaseTest {

    private Pathway pt;
    private Protein pr;

    @AfterEach
    void cleanUp(){
        EntityTransaction txn = em.getTransaction();
        txn.begin();
        em.remove(pt);
        em.remove(pr);
        txn.commit();
    }

    @Test
    void testCreateInteraction(){
        pr = createProtein();
        pt = new Pathway();
        String pId = "PID-" + random;
        String desc = "DESC-" + random;
        String type = "TYPE-" + random;
        String iid = "IID-" + random;
        String t = "T-" + random;
        String f = "F-" + random;
        pt.setPrimaryId(pId);
        pt.setDesc(desc);
        pt.setDbType(type);
        pt.setIsoformId(iid);
        pt.setThird(t);
        pt.setFourth(f);
        pt.setProtein(pr);

        // persist
        EntityTransaction txn = em.getTransaction();
        txn.begin();
        em.persist(pt);
        txn.commit();

        assertNotNull(pt.getId(), "unable to create the pathway record");
        assertNotNull(pt.getProtein());
        assertNotNull(pt.getProtein().getId());
    }

    private Protein createProtein() {
        // create protein
        Protein protein = new Protein();
        String pId = "PID-" + random;
        String pn = "PN-" + random;
        String acc = "ACC-" + random;
        String gene = "GENE-" + random;
        String pDesc = "PDESC-" + random;

        protein.setProteinId(pId);
        protein.setName(pn);
        protein.setAccession(acc);
        protein.setGene(gene);
        protein.setDesc(pDesc);

        EntityTransaction txn = em.getTransaction();
        txn.begin();
        em.persist(protein);
        txn.commit();

        assertNotNull(protein.getId(), "unable to create the protein record");
        return protein;
    }
}
