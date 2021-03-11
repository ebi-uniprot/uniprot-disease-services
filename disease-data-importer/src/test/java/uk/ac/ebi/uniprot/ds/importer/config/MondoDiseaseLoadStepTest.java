package uk.ac.ebi.uniprot.ds.importer.config;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
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

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import uk.ac.ebi.uniprot.ds.common.common.SourceType;
import uk.ac.ebi.uniprot.ds.common.dao.DiseaseDAO;
import uk.ac.ebi.uniprot.ds.common.model.Disease;
import uk.ac.ebi.uniprot.ds.importer.DataImporterSpringBootApplication;
import uk.ac.ebi.uniprot.ds.importer.util.Constants;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {DataImporterSpringBootApplication.class, BatchConfigurationDiseaseService.class, MondoDiseaseLoadStep.class})
class MondoDiseaseLoadStepTest extends AbstractBaseStepTest {

    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;
    @Autowired
    private DiseaseDAO diseaseDAO;

    @AfterEach
    void cleanUp() {
        this.diseaseDAO.deleteAll();
    }

    @Test
    void testMondoDiseaseLoadStep() throws Exception {
        // create proteins with diseases and other dependent objects by running the Step and then verify them
        JobParameters jobParameters = new JobParametersBuilder().addLong("time", System.currentTimeMillis()).toJobParameters();
        // when
        JobExecution jobExecution = jobLauncherTestUtils.launchStep(Constants.DS_MONDO_DISEASE_DATA_LOADER_STEP, jobParameters);
        Collection<StepExecution> actualStepExecutions = jobExecution.getStepExecutions();
        ExitStatus actualJobExitStatus = jobExecution.getExitStatus();
        // then
        // verify job status
        Assertions.assertEquals(1, actualStepExecutions.size());
        Assertions.assertEquals("COMPLETED", actualJobExitStatus.getExitCode());
        StepExecution step = actualStepExecutions.stream().collect(Collectors.toList()).get(0);
        Assertions.assertNotNull(step);
        Assertions.assertEquals(10, step.getReadCount());
        Assertions.assertEquals(10, step.getWriteCount());
        // and verify data
        verifyDiseases();
    }

    private void verifyDiseases(){
        List<Disease> diseases = this.diseaseDAO.findAll();
        Assertions.assertEquals(10, diseases.size());
        diseases.stream().map(Disease::getSource).forEach(src -> assertEquals(SourceType.MONDO.name(), src));
        diseases.stream().map(Disease::getDiseaseId).forEach(diseaseId -> assertTrue(diseaseId.startsWith("DI-M")));
        verifyCommonFields(diseases);
    }
}
