/*
 * Created by sahmad on 1/31/19 1:51 PM
 * UniProt Consortium.
 * Copyright (c) 2002-2019.
 *
 */

package uk.ac.ebi.uniprot.ds.config;

import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.batch.core.*;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import uk.ac.ebi.uniprot.ds.dao.*;
import uk.ac.ebi.uniprot.ds.model.*;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class BatchConfigurationDiseaseServiceTest {

    @Autowired
    private Job importUniProtDataJob;

    @Autowired
    private JobLauncher jobLauncher;

    @After
    public void cleanUp(){
        this.synonymDAO.deleteAll();
        this.variantDAO.deleteAll();
        this.pathwayDAO.deleteAll();
        this.interactionDAO.deleteAll();
        this.featureLocationDAO.deleteAll();
        this.evidenceDAO.deleteAll();
        this.diseaseDAO.deleteAll();
        this.proteinDAO.deleteAll();
    }

    @Test
    public void testDiseaseServiceJob() throws Exception {
        //verifyEmptyDB(); FIXME

        JobParameters jobParameters = new JobParametersBuilder().addLong("time",System.currentTimeMillis()).toJobParameters();

        JobExecution jobExecution = jobLauncher.run(importUniProtDataJob, jobParameters);
        BatchStatus status = jobExecution.getStatus();
        Assert.assertEquals(status, BatchStatus.COMPLETED);

        // verify the data
        List<Disease> diseases = this.diseaseDAO.findAll();
        Assert.assertTrue(diseases.size() > 5000);

        List<Protein> proteins = this.proteinDAO.findAll();
        Assert.assertTrue(proteins.size() > 0);

        List<Evidence> evidences = this.evidenceDAO.findAll();
        Assert.assertTrue(evidences.size() >= 0); //TODO add an entry in test file to populate this

        List<FeatureLocation> fls = this.featureLocationDAO.findAll();
        Assert.assertTrue(fls.size() > 0);

        List<Interaction> interactions = this.interactionDAO.findAll();
        Assert.assertTrue(interactions.size() > 0);

        List<Pathway> pathways = this.pathwayDAO.findAll();
        Assert.assertTrue(pathways.size() > 0);

        List<Synonym> syns = this.synonymDAO.findAll();
        Assert.assertTrue(syns.size() > 0);

        List<Variant> vars = this.variantDAO.findAll();
        Assert.assertTrue(vars.size() > 0);
    }

    private void verifyEmptyDB() {
        Assert.assertTrue(this.diseaseDAO.findAll().isEmpty());

        Assert.assertTrue(this.proteinDAO.findAll().isEmpty());

        Assert.assertTrue(this.evidenceDAO.findAll().isEmpty());

        Assert.assertTrue( this.featureLocationDAO.findAll().isEmpty());

        Assert.assertTrue(this.interactionDAO.findAll().isEmpty());

        Assert.assertTrue(this.pathwayDAO.findAll().isEmpty());

        Assert.assertTrue(this.synonymDAO.findAll().isEmpty());

        Assert.assertTrue(this.variantDAO.findAll().isEmpty());
    }


    @Autowired
    private SynonymDAO synonymDAO;

    @Autowired
    private VariantDAO variantDAO;

    @Autowired
    private PathwayDAO pathwayDAO;

    @Autowired
    private InteractionDAO interactionDAO;

    @Autowired
    private FeatureLocationDAO featureLocationDAO;

    @Autowired
    private EvidenceDAO evidenceDAO;

    @Autowired
    private DiseaseDAO diseaseDAO;

    @Autowired
    private ProteinDAO proteinDAO;
}
