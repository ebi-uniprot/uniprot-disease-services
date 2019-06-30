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
import uk.ac.ebi.uniprot.ds.common.model.Drug;
import uk.ac.ebi.uniprot.ds.common.model.Protein;
import uk.ac.ebi.uniprot.ds.common.model.ProteinCrossRef;
import uk.ac.ebi.uniprot.ds.rest.utils.ModelCreationUtils;

import java.util.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class DiseaseServiceIntegrationTest {
    @Autowired
    private DiseaseService diseaseService;
    @Autowired
    private DrugService drugService;

    @MockBean
    private DiseaseDAO diseaseDAO;

    private Disease disease;
    private Disease diseaseWithDrug;

    @Before
    public void setUp(){
        String random = UUID.randomUUID().toString();
        this.disease = ModelCreationUtils.createDiseaseObject(random);
        this.diseaseWithDrug = ModelCreationUtils.createDiseaseObject(random + "WithDrug");

        Mockito.doAnswer(invocationOnMock -> {
            Disease passedDisease = invocationOnMock.getArgument(0);
            passedDisease.setId(new Random().nextLong());
            return passedDisease;
        }).when(this.diseaseDAO).save(this.disease);

        Mockito.when(this.diseaseDAO.findById(Mockito.anyLong())).thenReturn(Optional.of(this.disease));

        Mockito.when(this.diseaseDAO.findByDiseaseId(this.disease.getDiseaseId())).thenReturn(Optional.ofNullable(this.disease));

        Mockito.doNothing().when(this.diseaseDAO).deleteByDiseaseId(Mockito.anyString());
        Mockito.doNothing().when(this.diseaseDAO).deleteById(Mockito.anyLong());

        // set up data related to drugs related to a disease
        // create two proteins
        Protein protein1 = ModelCreationUtils.createProteinObject(random + 1);
        Protein protein2 = ModelCreationUtils.createProteinObject(random + 2);
        // create 3 cross ref
        ProteinCrossRef pcr1 = ModelCreationUtils.createProteinXRefObject(random + 1);
        ProteinCrossRef pcr2 = ModelCreationUtils.createProteinXRefObject(random + 2);
        ProteinCrossRef pcr3 = ModelCreationUtils.createProteinXRefObject(random + 3);

        // add protein cross ref
        protein1.setProteinCrossRefs(Arrays.asList(pcr1, pcr2));
        protein2.setProteinCrossRefs(Arrays.asList(pcr2, pcr3));

        // create few drugs
        Drug d1 = ModelCreationUtils.createDrugObject(random + 1);
        Drug d2 = ModelCreationUtils.createDrugObject(random + 2);
        Drug d3 = ModelCreationUtils.createDrugObject(random + 3);
        Drug d4 = ModelCreationUtils.createDrugObject(random + 4);

        // assign drugs to cross ref
        pcr1.setDrugs(Arrays.asList(d1, d2));
        pcr2.setDrugs(Arrays.asList(d2, d3));
        pcr3.setDrugs(Arrays.asList(d3, d4));

        // add proteins to the disease
        this.diseaseWithDrug.setProteins(Arrays.asList(protein1, protein2));
        Mockito.when(this.diseaseDAO.findByDiseaseId(this.diseaseWithDrug.getDiseaseId())).thenReturn(Optional.ofNullable(this.diseaseWithDrug));
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

    @Test
    public void testGetDrugsByDiseaseId(){
        List<Drug> drugs = this.drugService.getDrugsByDiseaseId(this.diseaseWithDrug.getDiseaseId());
        Assert.assertFalse(drugs.isEmpty());
        Assert.assertEquals(4, drugs.size());
    }
}
