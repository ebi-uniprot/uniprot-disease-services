/*
 * Created by sahmad on 07/02/19 15:02
 * UniProt Consortium.
 * Copyright (c) 2002-2019.
 *
 */

package uk.ac.ebi.uniprot.ds.rest.service;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit4.SpringRunner;
import uk.ac.ebi.uniprot.ds.common.dao.ProteinDAO;
import uk.ac.ebi.uniprot.ds.common.model.Protein;
import uk.ac.ebi.uniprot.ds.rest.utils.ModelCreationUtils;

import java.util.Optional;
import java.util.Random;
import java.util.UUID;

@RunWith(SpringRunner.class)
@SpringBootTest
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
        this.protein = ModelCreationUtils.createProteinObject(UUID.randomUUID().toString());

        Mockito.doAnswer(invocationOnMock -> {
            Protein passedProtein = invocationOnMock.getArgument(0);
            passedProtein.setId(new Random().nextLong());
            return passedProtein;
        }).when(this.proteinDAO).save(this.protein);

        Mockito.when(this.proteinDAO.findById(Mockito.anyLong())).thenReturn(Optional.of(this.protein));

        Mockito.when(this.proteinDAO.findByProteinId(this.protein.getProteinId())).thenReturn(Optional.ofNullable(this.protein));
    }

    @Test
    public void testCreateProtein(){

        Protein createdProtein = this.proteinService.createProtein(this.protein.getProteinId(), this.protein.getName(),
                this.protein.getAccession(), this.protein.getGene(), this.protein.getDesc());

        Assert.assertNotNull(createdProtein.getId());
    }

    @Test
    public void testGetProteinByProteinId(){
        Optional<Protein> storedProtein = this.proteinService.getProteinByProteinId(this.protein.getProteinId());
        Assert.assertTrue(storedProtein.isPresent());
        Assert.assertEquals(this.protein.getProteinId(), storedProtein.get().getProteinId());
    }
}
