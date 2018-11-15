package uk.ac.ebi.uniprot.disease.pipeline.processor;

import uk.ac.ebi.uniprot.disease.pipeline.request.DiseaseRequest;

import java.io.IOException;

public abstract class BaseProcessor {

    protected BaseProcessor nextProcessor;

    public void setNextProcessor(BaseProcessor nextProcessor) {
        this.nextProcessor = nextProcessor;
    }

    public abstract void processRequest(DiseaseRequest request) throws IOException;
    public abstract String getProcessorName();

}
