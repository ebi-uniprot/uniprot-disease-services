package uk.ac.ebi.uniprot.disease.pipeline.processor.common;

import uk.ac.ebi.uniprot.disease.pipeline.request.DiseaseRequest;

import java.io.IOException;

/**
 * The workflow for GDA is like
 * Start --> Download --> [FileParser --> DataSaver]+ --> WorkflowReporter --> Cleaner --> End
 * @author sahmad
 */

public abstract class BaseProcessor {

    protected BaseProcessor nextProcessor;

    public void setNextProcessor(BaseProcessor nextProcessor) {
        this.nextProcessor = nextProcessor;
    }

    public abstract void processRequest(DiseaseRequest request) throws IOException;
    public abstract String getProcessorName();

}
