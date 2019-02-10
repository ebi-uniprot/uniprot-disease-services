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
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import uk.ac.ebi.uniprot.ds.common.dao.DiseaseDAO;
import uk.ac.ebi.uniprot.ds.common.model.Disease;
import uk.ac.ebi.uniprot.ds.rest.utils.ModelCreationUtils;

import java.util.Optional;
import java.util.Random;
import java.util.UUID;

@RunWith(SpringRunner.class)
@SpringBootTest
public class DiseaseServiceIntegrationTest {
    @Autowired
    private DiseaseService diseaseService;

    @MockBean
    private DiseaseDAO diseaseDAO;

    private Disease disease;

    @Before
    public void setUp(){
        this.disease = ModelCreationUtils.createDiseaseObject(UUID.randomUUID().toString());

        Mockito.doAnswer(invocationOnMock -> {
            Disease passedDisease = invocationOnMock.getArgument(0);
            passedDisease.setId(new Random().nextLong());
            return passedDisease;
        }).when(this.diseaseDAO).save(this.disease);

        Mockito.when(this.diseaseDAO.findById(Mockito.anyLong())).thenReturn(Optional.of(this.disease));

        Mockito.when(this.diseaseDAO.findByDiseaseId(this.disease.getDiseaseId())).thenReturn(Optional.ofNullable(this.disease));

        Mockito.doNothing().when(this.diseaseDAO).deleteByDiseaseId(Mockito.anyString());
        Mockito.doNothing().when(this.diseaseDAO).deleteById(Mockito.anyLong());
    }

    @Test
    public void testCreateDisease(){

        Disease nDisease = this.diseaseService.createUpdateDisease(
                                                                    this.disease.getDiseaseId(), this.disease.getName(),
                                                                    this.disease.getDesc(), this.disease.getAcronym()
                                                                    );

        Assert.assertNotNull(nDisease.getId());
    }

    @Test
    public  void testCreateDisease2(){
        Disease nDisease = this.diseaseService.createUpdateDisease(this.disease);
        Assert.assertNotNull(nDisease.getId());
    }

    @Test
    public void testGetDiseaseByDiseaseId(){
        Optional<Disease> storedDisease = this.diseaseService.findByDiseaseId(this.disease.getDiseaseId());
        Assert.assertTrue(storedDisease.isPresent());
        Assert.assertEquals(this.disease.getDiseaseId(), storedDisease.get().getDiseaseId());
    }

    @Test
    public void testGetDiseaseById(){
        Disease nDisease = this.diseaseService.createUpdateDisease(
                this.disease.getDiseaseId(), this.disease.getName(),
                this.disease.getDesc(), this.disease.getAcronym()
        );

        Assert.assertNotNull(nDisease.getId());

        Optional<Disease> storedDisease = this.diseaseService.findById(nDisease.getId());
        Assert.assertTrue(storedDisease.isPresent());
    }

    @Test
    public void testDeleteDiseaseByDiseaseId(){
        this.diseaseService.deleteDiseaseByDiseaseId(this.disease.getDiseaseId());
    }

    @Test
    public void testDeleteDiseaseById(){
        this.diseaseService.deleteDiseaseById(new Random().nextLong());
    }
}
