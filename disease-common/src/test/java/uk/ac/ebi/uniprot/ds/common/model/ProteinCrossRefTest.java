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

import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@Transactional
public class ProteinCrossRefTest extends BaseTest {

    private ProteinCrossRef pcr;
    private Protein pr;

    @AfterEach
    void cleanUp() {
        em.remove(pcr);
        em.remove(pr);
    }

    @Test
    void testCreateProteinCrossRef() {
        pr = createProtein();
        pcr = createProteinCrossRefObject(BaseTest.random);
        pcr.setProtein(pr);

        // persist
        em.persist(pcr);
        em.flush();

        assertNotNull(pcr.getId(), "unable to create the protein cross ref record");
        assertNotNull(pcr.getProtein());
        assertNotNull(pcr.getProtein().getId());
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

        em.persist(protein);
        em.flush();

        assertNotNull(protein.getId(), "unable to create the protein record");
        return protein;
    }

    public static ProteinCrossRef createProteinCrossRefObject(String uuid) {
        ProteinCrossRef proteinCrossRef = new ProteinCrossRef();
        String pId = "PID-" + uuid;
        String desc = "DESC-" + uuid;
        String type = "TYPE-" + uuid;
        String iid = "IID-" + uuid;
        String t = "T-" + uuid;
        String f = "F-" + uuid;
        proteinCrossRef.setPrimaryId(pId);
        proteinCrossRef.setDescription(desc);
        proteinCrossRef.setDbType(type);
        proteinCrossRef.setIsoformId(iid);
        proteinCrossRef.setThird(t);
        proteinCrossRef.setFourth(f);
        return proteinCrossRef;
    }

    public static ProteinCrossRef createProteinCrossRefObject() {
        return createProteinCrossRefObject(String.valueOf(BaseTest.random));
    }
}
