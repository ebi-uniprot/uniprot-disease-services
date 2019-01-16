/*
 * Created by sahmad on 16/01/19 10:14
 * UniProt Consortium.
 * Copyright (c) 2002-2019.
 *
 */

package uk.ac.ebi.diseaseservice.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.StepExecution;
import uk.ac.ebi.diseaseservice.util.Constants;

import java.util.concurrent.TimeUnit;

public class LogJobListener implements JobExecutionListener {
    private static final Logger LOGGER = LoggerFactory.getLogger(LogJobListener.class);

    @Override
    public void beforeJob(JobExecution jobExecution) {
        LOGGER.info("Job {} starting ...", Constants.DISEASE_SERVICE_DATA_LOADER);
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        LOGGER.info("Job {} completed.", Constants.DISEASE_SERVICE_DATA_LOADER);

        long durationMillis = jobExecution.getEndTime().getTime() - jobExecution.getStartTime().getTime();

        String duration = String.format("%d hrs, %d min, %d sec",
                TimeUnit.MILLISECONDS.toHours(durationMillis),
                TimeUnit.MILLISECONDS.toMinutes(durationMillis) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS
                        .toHours(durationMillis)),
                TimeUnit.MILLISECONDS.toSeconds(durationMillis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS
                        .toMinutes(durationMillis))
        );

        LOGGER.info("=====================================================");
        LOGGER.info("              {} Job Statistics                 ", Constants.DISEASE_SERVICE_DATA_LOADER );
        LOGGER.info("Exit status   : {}", jobExecution.getExitStatus().getExitCode());
        LOGGER.info("Start time    : {}", jobExecution.getStartTime());
        LOGGER.info("End time      : {}", jobExecution.getEndTime());
        LOGGER.info("Duration      : {}", duration);

        long skipCount = 0L;
        long readSkips = 0L;
        long writeSkips = 0L;
        long processingSkips = 0L;
        long readCount = 0L;
        long writeCount = 0L;

        for (StepExecution stepExecution : jobExecution.getStepExecutions()) {
            readSkips += stepExecution.getReadSkipCount();
            writeSkips += stepExecution.getWriteSkipCount();
            processingSkips += stepExecution.getProcessSkipCount();
            readCount += stepExecution.getReadCount();
            writeCount += stepExecution.getWriteCount();
            skipCount += stepExecution.getSkipCount();
        }
        LOGGER.info("Read count    : {}", readCount);
        LOGGER.info("Write count   : {}", writeCount);
        LOGGER.info("Skip count    : {} ({} read, {} processing and {} write)", skipCount, readSkips, processingSkips, writeSkips);
    }
}
