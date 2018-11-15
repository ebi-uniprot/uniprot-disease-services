package uk.ac.ebi.uniprot.disease.pipeline.processor;

import uk.ac.ebi.uniprot.disease.model.DisGeNET.GeneDiseaseAssociation;
import uk.ac.ebi.uniprot.disease.pipeline.request.DiseaseRequest;

import java.io.IOException;
import java.util.List;

public class DataSaver extends BaseProcessor{
    @Override
    public void processRequest(DiseaseRequest request) throws IOException {
        System.out.println("in data saver");
        persistRecords(request.getParsedRecords());
        // enrich data in request
        if(nextProcessor != null){
            nextProcessor.processRequest(request);
        }
    }

    private void persistRecords(List<GeneDiseaseAssociation> parsedRecords) {
        for(GeneDiseaseAssociation gda : parsedRecords){
            persistRecord(gda);
        }
    }

    private void persistRecord(GeneDiseaseAssociation gda) {
        System.out.println(gda);
    }
}
