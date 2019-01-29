/*
 * Created by sahmad on 29/01/19 10:46
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
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.ac.ebi.uniprot.ds.dao.DiseaseDAO;
import uk.ac.ebi.uniprot.ds.model.Disease;
import uk.ac.ebi.uniprot.ds.model.DiseaseTest;

import java.util.Optional;
import java.util.Random;
import java.util.UUID;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class DiseaseServiceIntegrationTest {
    @Autowired
    private DiseaseService diseaseService;

    @MockBean
    private DiseaseDAO diseaseDAO;

    private Disease disease;

    @BeforeEach
    void setUp(){
        this.disease = DiseaseTest.createDiseaseObject(UUID.randomUUID().toString());

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
    void testCreateDisease(){

        Disease nDisease = this.diseaseService.createUpdateDisease(
                                                                    this.disease.getDiseaseId(), this.disease.getName(),
                                                                    this.disease.getDesc(), this.disease.getAcronym()
                                                                    );

        Assertions.assertNotNull(nDisease.getId());
    }

    @Test
    void testCreateDisease2(){
        Disease nDisease = this.diseaseService.createUpdateDisease(this.disease);
        Assertions.assertNotNull(nDisease.getId());
    }

    @Test
    void testGetDiseaseByDiseaseId(){
        Optional<Disease> storedDisease = this.diseaseService.findByDiseaseId(this.disease.getDiseaseId());
        Assertions.assertTrue(storedDisease.isPresent());
        Assertions.assertEquals(this.disease.getDiseaseId(), storedDisease.get().getDiseaseId());
    }

    @Test
    void testGetDiseaseById(){
        Disease nDisease = this.diseaseService.createUpdateDisease(
                this.disease.getDiseaseId(), this.disease.getName(),
                this.disease.getDesc(), this.disease.getAcronym()
        );

        Assertions.assertNotNull(nDisease.getId());

        Optional<Disease> storedDisease = this.diseaseService.findById(nDisease.getId());
        Assertions.assertTrue(storedDisease.isPresent());
    }

    @Test
    void testDeleteDiseaseByDiseaseId(){
        this.diseaseService.deleteDiseaseByDiseaseId(this.disease.getDiseaseId());
    }

    @Test
    void testDeleteDiseaseById(){
        this.diseaseService.deleteDiseaseById(new Random().nextLong());
    }
}
