/*
 * Created by sahmad on 1/16/19 1:50 PM
 * UniProt Consortium.
 * Copyright (c) 2002-2019.
 *
 */

package uk.ac.ebi.diseaseservice.config;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import uk.ac.ebi.diseaseservice.util.Constants;

@SpringBootTest(classes = TestConfiguration.class)
@RunWith(SpringRunner.class)
public class DiseaseServiceImportConfigTests {



    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;

    @Test
    public void testJob() throws Exception {
        JobExecution jobExecution = jobLauncherTestUtils.launchJob();
        Assert.assertEquals(BatchStatus.COMPLETED.toString(), jobExecution.getExitStatus().getExitCode());

        // get the step
        StepExecution stepExecution = jobExecution.getStepExecutions().stream()
                .filter(step -> Constants.DS_HUM_DISEASE_DATA_LOADER_STEP.equals(step.getStepName())).findFirst().get();

        Assert.assertNotNull("Step is null", stepExecution);
        Assert.assertEquals("Status is not as expected", BatchStatus.COMPLETED, stepExecution.getStatus());
        Assert.assertEquals("Read count does not mathc", TestConfiguration.READ_WRITE_COUNT, stepExecution.getReadCount());
        Assert.assertEquals("Write count does not mathc", TestConfiguration.READ_WRITE_COUNT, stepExecution.getWriteCount());
    }

}
