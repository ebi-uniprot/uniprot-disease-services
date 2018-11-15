package uk.ac.ebi.uniprot.disease.pipeline.processor;

import uk.ac.ebi.uniprot.disease.model.DisGeNET.GeneDiseaseAssociation;
import uk.ac.ebi.uniprot.disease.pipeline.request.DiseaseRequest;
import uk.ac.ebi.uniprot.disease.service.tsv.GeneDiseaseParser;
import uk.ac.ebi.uniprot.disease.service.tsv.TSVReader;

import java.io.IOException;
import java.util.List;

public class FileParser extends BaseProcessor {

    @Override
    public void processRequest(DiseaseRequest request)  throws IOException {
        System.out.println("FileParser");
        TSVReader reader = new TSVReader(request.getUncompressedFilePath());
        GeneDiseaseParser parser = new GeneDiseaseParser(reader);
        List<GeneDiseaseAssociation> gdas = parser.parseRecords();
        request.setParsedRecords(gdas);
        // enrich the request and pass on
        if(nextProcessor != null){
            nextProcessor.processRequest(request);
        }

    }
}
