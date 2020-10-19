package uk.ac.ebi.uniprot.ds.importer.config;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import uk.ac.ebi.uniprot.ds.common.dao.DiseaseDAO;
import uk.ac.ebi.uniprot.ds.common.dao.DiseaseProteinDAO;
import uk.ac.ebi.uniprot.ds.common.dao.ProteinDAO;
import uk.ac.ebi.uniprot.ds.common.model.Disease;
import uk.ac.ebi.uniprot.ds.common.model.DiseaseProtein;
import uk.ac.ebi.uniprot.ds.common.model.Protein;
import uk.ac.ebi.uniprot.ds.importer.DataImporterSpringBootApplication;
import uk.ac.ebi.uniprot.ds.importer.processor.ChemblOpenTargetToDrugsTest;
import uk.ac.ebi.uniprot.ds.importer.util.Constants;
import uk.ac.ebi.uniprot.ds.importer.writer.DiseaseWriterTest;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {DataImporterSpringBootApplication.class, BatchConfigurationDiseaseService.class, AlzheimerProteinLoadStep.class})
class AlzheimerProteinLoadStepTest{
    private List<String> sampleProteinAccessions = Arrays.asList("P05023", "Q9UQM7", "P37173");
    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;
    @Autowired
    private DiseaseDAO diseaseDAO;
    @Autowired
    private ProteinDAO proteinDAO;
    @Autowired
    private DiseaseProteinDAO diseaseProteinDAO;

    @BeforeEach
    @AfterEach
    void cleanUp() {
        this.diseaseProteinDAO.deleteAll();
        this.diseaseDAO.deleteAll();
        this.proteinDAO.deleteAll();
    }

    @Test
    void testAlzheimerDiseaseProteinsLoadStep() throws Exception {
        Assertions.assertTrue(this.diseaseProteinDAO.findAll().isEmpty());
        // create test data
        Disease alzheimerDisease = this.diseaseDAO.save(DiseaseWriterTest.createDiseaseByDiseaseId("Alzheimer disease"));
        Protein p1 = ChemblOpenTargetToDrugsTest.createProtein();
        p1.setAccession(sampleProteinAccessions.get(0));
        Protein p2 = ChemblOpenTargetToDrugsTest.createProtein();
        p2.setAccession(sampleProteinAccessions.get(1));
        Protein p3 = ChemblOpenTargetToDrugsTest.createProtein();
        p3.setAccession(sampleProteinAccessions.get(2));
        this.proteinDAO.saveAll(Arrays.asList(p1, p2, p3));

        JobParameters jobParameters = new JobParametersBuilder().addLong("time", System.currentTimeMillis()).toJobParameters();
        // when
        JobExecution jobExecution = jobLauncherTestUtils.launchStep(Constants.DS_AD_PROTEIN_LOADER_STEP, jobParameters);
        Collection<StepExecution> actualStepExecutions = jobExecution.getStepExecutions();
        ExitStatus actualJobExitStatus = jobExecution.getExitStatus();
        // then
        // verify job status
        Assertions.assertEquals(1, actualStepExecutions.size());
        Assertions.assertEquals("COMPLETED", actualJobExitStatus.getExitCode());
        StepExecution step = actualStepExecutions.stream().collect(Collectors.toList()).get(0);
        Assertions.assertNotNull(step);
        Assertions.assertEquals(3, step.getReadCount());
        Assertions.assertEquals(3, step.getWriteCount());
        List<DiseaseProtein> dps = this.diseaseProteinDAO.findAll();
        Assertions.assertFalse(dps.isEmpty());
        Assertions.assertEquals(3, dps.size());
        List<String> accessions = dps.stream().map(DiseaseProtein::getProtein).map(Protein::getAccession).collect(Collectors.toList());
        assertThat(accessions, containsInAnyOrder(sampleProteinAccessions.get(0), sampleProteinAccessions.get(1), sampleProteinAccessions.get(2)));
    }
}
