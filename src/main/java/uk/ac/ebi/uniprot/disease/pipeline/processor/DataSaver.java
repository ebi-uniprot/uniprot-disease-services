package uk.ac.ebi.uniprot.disease.pipeline.processor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.uniprot.disease.model.disgenet.GeneDiseaseAssociation;
import uk.ac.ebi.uniprot.disease.pipeline.request.DiseaseRequest;

import java.io.IOException;
import java.util.List;

/**
 * Class responsible for storing the data in DB
 * @author sahmad
 */

public class DataSaver extends BaseProcessor{
    private static final Logger LOGGER = LoggerFactory.getLogger(DataSaver.class);
    private static final String PROCESSOR_NAME = "DataSaver";

    @Override
    public String getProcessorName(){
        return PROCESSOR_NAME;
    }

    @Override
    public void processRequest(DiseaseRequest request) throws IOException {
        LOGGER.debug("Going to persist parsed data");
        persistRecords(request.getParsedRecords());
        // enrich data in request TODO with time and other info
        if(nextProcessor != null){
            LOGGER.debug("Invoking the next processor {}", nextProcessor.getProcessorName());
            nextProcessor.processRequest(request);
        }
    }

    private void persistRecords(List<GeneDiseaseAssociation> parsedRecords) {
        for(GeneDiseaseAssociation gda : parsedRecords){
            persistRecord(gda);
        }
    }

    private void persistRecord(GeneDiseaseAssociation gda) {
        //TODO write actual code to store the data
        //LOGGER.debug("Record persisted {}", gda);
    }
}
