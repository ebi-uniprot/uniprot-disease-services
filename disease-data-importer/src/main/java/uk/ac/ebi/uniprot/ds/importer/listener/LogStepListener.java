/*
 * Created by sahmad on 29/01/19 19:28
 * UniProt Consortium.
 * Copyright (c) 2002-2019.
 *
 */

package uk.ac.ebi.uniprot.ds.importer.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.beans.factory.annotation.Value;
import uk.ac.ebi.uniprot.ds.importer.util.Constants;

public class LogStepListener implements StepExecutionListener {
    private static final Logger LOGGER = LoggerFactory.getLogger(LogStepListener.class);
    @Value("${ds.uniprot.data.file.path}")
    private String uniProtDataFilePath;
    @Value("${ds.humdisease.data.file.path}")
    private String humDiseaseDataFile;
    @Value("${ds.genecoords.data.file.path}")
    private String geneCoordsDataFile;
    private static final String FILE_LOG_MESSAGE = "Using the input file: {}";

    @Override
    public void beforeStep(StepExecution stepExecution) {
        LOGGER.info("Disease import STEP '{}' starting.", stepExecution.getStepName());
        if(Constants.DS_HUM_DISEASE_DATA_LOADER_STEP.equals(stepExecution.getStepName())){
            LOGGER.info(FILE_LOG_MESSAGE, humDiseaseDataFile);
        }else if(Constants.DS_UNIPROT_DATA_LOADER_STEP.equals(stepExecution.getStepName())){
            LOGGER.info(FILE_LOG_MESSAGE, uniProtDataFilePath);
        }else if(Constants.DS_GENE_COORD_LOADER_STEP.equals(stepExecution.getStepName())){
            LOGGER.info(FILE_LOG_MESSAGE, geneCoordsDataFile);
        }
    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        LOGGER.info("=====================================================");
        LOGGER.info("              Disease import Step Statistics                 ");
        LOGGER.info("Step name     : {}", stepExecution.getStepName());
        LOGGER.info("Exit status   : {}", stepExecution.getExitStatus().getExitCode());
        LOGGER.info("Read count    : {}", stepExecution.getReadCount());
        LOGGER.info("Write count   : {}", stepExecution.getWriteCount());
        LOGGER.info("Skip count    : {} ({} read / {} processing /{} write)", stepExecution.getSkipCount(),
                stepExecution.getReadSkipCount(), stepExecution.getProcessSkipCount(),
                stepExecution.getWriteSkipCount());
        LOGGER.info("=====================================================");
        return stepExecution.getExitStatus();
    }
}
