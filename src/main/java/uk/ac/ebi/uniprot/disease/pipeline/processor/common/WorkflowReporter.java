package uk.ac.ebi.uniprot.disease.pipeline.processor.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.uniprot.disease.pipeline.request.DiseaseRequest;
import uk.ac.ebi.uniprot.disease.pipeline.request.WorkflowMetrics;

import java.io.File;
import java.io.IOException;

/**
 * Class responsible for reporting the metrics related to the workflow
 * @author sahmad
 */

public class WorkflowReporter extends BaseProcessor {
    private static final Logger LOGGER = LoggerFactory.getLogger(WorkflowReporter.class);
    private static final String PROCESSOR_NAME = "WorkflowReporter";

    @Override
    public String getProcessorName(){
        return PROCESSOR_NAME;
    }


    @Override
    public void processRequest(DiseaseRequest request) throws IOException {
        LOGGER.debug("Starting the reporting processor");

        if(nextProcessor != null){
            LOGGER.debug("Invoking the next processor {}", nextProcessor.getProcessorName());
            nextProcessor.processRequest(request);
        }

        printReport(request.getWorkflowMetrics());

        LOGGER.debug("The metrics of workflow {}", request.getWorkflowMetrics());
    }

    private void printReport(WorkflowMetrics wf) {
        LOGGER.debug("-------------------- Begin the summary of the workflow below -----------------------");

        LOGGER.debug("Time taken to download {} ms", wf.getDownloadTime());
        LOGGER.debug("Size of the downloaded file: {} KB", wf.getSizeOfDownloadedFile()/1024);
        LOGGER.debug("Size of the uncompressed file: {} KB", wf.getSizeOfUncompressedFile()/1024);
        LOGGER.debug("Time taken to parse the file: {} ms", wf.getTotalParseTime());
        LOGGER.debug("Time taken to store the records: {} ms", wf.getTotalSaveTime());
        LOGGER.debug("Total number of records parsed: {}", wf.getRecordsParsed());
        LOGGER.debug("Total number of records stored: {}", wf.getRecordsSaved());
        LOGGER.debug("Total time taken to complete the workflow: {} ms", wf.getTotalTimeTaken());

        LOGGER.debug("-------------------- End the summary of the workflow below -----------------------");
    }


}
