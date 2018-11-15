package uk.ac.ebi.uniprot.disease.pipeline.processor;

import uk.ac.ebi.uniprot.disease.pipeline.request.DiseaseRequest;

import java.io.IOException;

public class WorkflowReporter extends BaseProcessor {
    @Override
    public void processRequest(DiseaseRequest request) throws IOException {
        System.out.println("workflow reporter");
        // TODO report the stats
        if(nextProcessor != null){
            nextProcessor.processRequest(request);
        }
    }
}
