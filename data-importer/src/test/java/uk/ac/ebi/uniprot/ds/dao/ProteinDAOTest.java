/*
 * Created by sahmad on 28/01/19 18:55
 * UniProt Consortium.
 * Copyright (c) 2002-2019.
 *
 */

package uk.ac.ebi.uniprot.ds.dao;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.ac.ebi.uniprot.ds.model.Protein;
import uk.ac.ebi.uniprot.ds.model.ProteinTest;

import java.util.List;
import java.util.UUID;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class ProteinDAOTest {
    @Autowired
    private ProteinDAO proteinDAO;

    @Test
    void testCreateProtein(){
        proteinDAO.save(ProteinTest.createProteinObject(UUID.randomUUID().toString()));
        List<Protein> pr = proteinDAO.findAll();
        Assertions.assertNotNull(pr);
    }
}
