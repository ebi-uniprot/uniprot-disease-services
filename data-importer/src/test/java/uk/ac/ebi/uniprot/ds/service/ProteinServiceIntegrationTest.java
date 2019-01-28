/*
 * Created by sahmad on 1/28/19 10:17 AM
 * UniProt Consortium.
 * Copyright (c) 2002-2019.
 *
 */

package uk.ac.ebi.uniprot.ds.service;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit4.SpringRunner;
import uk.ac.ebi.uniprot.ds.dao.ProteinDAO;
import uk.ac.ebi.uniprot.ds.model.Protein;
import uk.ac.ebi.uniprot.ds.model.ProteinTest;

import java.util.Optional;
import java.util.Random;
import java.util.UUID;

@RunWith(SpringRunner.class)
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
    @Before
    public void setUp(){
        this.protein = ProteinTest.createProteinObject(UUID.randomUUID().toString());

        Mockito.doAnswer(invocationOnMock -> {
            Protein passedProtein = invocationOnMock.getArgument(0);
            passedProtein.setId(new Random().nextLong());
            return passedProtein;
        }).when(this.proteinDAO).createOrUpdate(this.protein);

        Mockito.when(this.proteinDAO.get(Mockito.anyLong())).thenReturn(Optional.of(this.protein));

        Mockito.when(this.proteinDAO.getProteinById(this.protein.getProteinId())).thenReturn(Optional.of(this.protein));
    }

    @Test
    public void testCreateProtein(){

        Protein createdProtein = this.proteinService.createProtein(this.protein.getProteinId(), this.protein.getName(),
                this.protein.getAccession(), this.protein.getGene(), this.protein.getDesc());

        Assertions.assertNotNull(createdProtein.getId());
    }

    @Test
    public void testGetProteinByProteinId(){
        Optional<Protein> optPr = this.proteinService.getProteinByProteinId(this.protein.getProteinId());
        Assert.assertTrue(optPr.isPresent());
        Assert.assertEquals(this.protein.getProteinId(), optPr.get().getProteinId());
    }
}
