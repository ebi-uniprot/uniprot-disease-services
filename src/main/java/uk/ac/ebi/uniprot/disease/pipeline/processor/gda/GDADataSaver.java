package uk.ac.ebi.uniprot.disease.pipeline.processor.gda;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.uniprot.disease.model.disgenet.GeneDiseaseAssociation;
import uk.ac.ebi.uniprot.disease.pipeline.processor.common.BaseProcessor;
import uk.ac.ebi.uniprot.disease.pipeline.request.DiseaseRequest;

import java.io.IOException;
import java.util.List;

/**
 * Class responsible for storing the data in DB
 * @author sahmad
 */

public class GDADataSaver extends BaseProcessor {
    private static final Logger LOGGER = LoggerFactory.getLogger(GDADataSaver.class);
    private static final String PROCESSOR_NAME = "GDADataSaver";

    @Override
    public String getProcessorName(){
        return PROCESSOR_NAME;
    }

    @Override
    public void processRequest(DiseaseRequest request) throws IOException {
        LOGGER.debug("Going to persist parsed GDA data");
        if(!request.getParsedGDARecords().isEmpty()) {
            persistRecords(request.getParsedGDARecords());
            // enrich data in request TODO with time and other info
        } else if(nextProcessor != null){ // move to the next processor
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
        //LOGGER.debug("GDA Record persisted {}", gda);
    }
}
