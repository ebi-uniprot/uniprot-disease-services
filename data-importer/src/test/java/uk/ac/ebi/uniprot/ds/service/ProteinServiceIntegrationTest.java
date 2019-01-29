/*
 * Created by sahmad on 1/28/19 10:17 AM
 * UniProt Consortium.
 * Copyright (c) 2002-2019.
 *
 */

package uk.ac.ebi.uniprot.ds.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.ac.ebi.uniprot.ds.dao.ProteinDAO;
import uk.ac.ebi.uniprot.ds.model.Protein;
import uk.ac.ebi.uniprot.ds.model.ProteinTest;

import java.util.Optional;
import java.util.Random;
import java.util.UUID;

@ExtendWith(SpringExtension.class)
public class ProteinServiceIntegrationTest {

   @TestConfiguration
    static class ProteinServiceTestContextConfiguration{
        @Bean
        public ProteinService proteinService(){
            return new ProteinService();
        }
    }

    @Autowired
    private ProteinService proteinService;

    @MockBean
    private ProteinDAO proteinDAO;

    private Protein protein;
    @BeforeEach
    void setUp(){
        this.protein = ProteinTest.createProteinObject(UUID.randomUUID().toString());

        Mockito.doAnswer(invocationOnMock -> {
            Protein passedProtein = invocationOnMock.getArgument(0);
            passedProtein.setId(new Random().nextLong());
            return passedProtein;
        }).when(this.proteinDAO).save(this.protein);

        Mockito.when(this.proteinDAO.findById(Mockito.anyLong())).thenReturn(Optional.of(this.protein));

        Mockito.when(this.proteinDAO.findByProteinId(this.protein.getProteinId())).thenReturn(this.protein);
    }

    @Test
    void testCreateProtein(){

        Protein createdProtein = this.proteinService.createProtein(this.protein.getProteinId(), this.protein.getName(),
                this.protein.getAccession(), this.protein.getGene(), this.protein.getDesc());

        Assertions.assertNotNull(createdProtein.getId());
    }

    @Test
    void testGetProteinByProteinId(){
        Protein storedProtein = this.proteinService.getProteinByProteinId(this.protein.getProteinId());
        Assertions.assertNotNull(storedProtein);
        Assertions.assertEquals(this.protein.getProteinId(), storedProtein.getProteinId());
    }
}
