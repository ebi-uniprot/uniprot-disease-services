package uk.ac.ebi.uniprot.disease.pipeline.processor.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.uniprot.disease.pipeline.request.DiseaseRequest;

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
        // TODO report the stats
        if(nextProcessor != null){
            LOGGER.debug("Invoking the next processor {}", nextProcessor.getProcessorName());
            nextProcessor.processRequest(request);
        }
    }
}
