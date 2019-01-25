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

import java.util.UUID;

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
    void testCreatePathway(){
        pr = createProtein();
        pt = createPathwayObject(UUID.randomUUID().toString());
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

    public  static Pathway createPathwayObject(String uuid) {
        Pathway pathway = new Pathway();
        String pId = "PID-" + uuid;
        String desc = "DESC-" + uuid;
        String type = "TYPE-" + uuid;
        String iid = "IID-" + uuid;
        String t = "T-" + uuid;
        String f = "F-" + uuid;
        pathway.setPrimaryId(pId);
        pathway.setDesc(desc);
        pathway.setDbType(type);
        pathway.setIsoformId(iid);
        pathway.setThird(t);
        pathway.setFourth(f);
        return pathway;
    }
}
