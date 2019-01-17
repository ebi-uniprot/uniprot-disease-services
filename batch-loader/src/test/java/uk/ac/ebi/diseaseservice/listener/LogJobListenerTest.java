/*
 * Created by sahmad on 1/17/19 11:05 AM
 * UniProt Consortium.
 * Copyright (c) 2002-2019.
 *
 */

package uk.ac.ebi.diseaseservice.listener;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.test.MetaDataInstanceFactory;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import uk.ac.ebi.diseaseservice.config.TestConfiguration;

import java.util.Date;

@SpringBootTest(classes = TestConfiguration.class)
@SpringBatchTest
@RunWith(SpringRunner.class)
@ContextConfiguration
public class LogJobListenerTest {

    @Autowired
    JobExecutionListener jobExecutionListener;

    @Test
    public void testBeforeJob(){
       JobExecution jobExecution = MetaDataInstanceFactory.createJobExecution();
       jobExecutionListener.beforeJob(jobExecution);
   }

    @Test
    public void testAfterJob(){
        JobExecution jobExecution = MetaDataInstanceFactory.createJobExecution();
        jobExecution.setStartTime(new Date());
        jobExecution.setEndTime(new Date());
        jobExecutionListener.afterJob(jobExecution);
    }

}
