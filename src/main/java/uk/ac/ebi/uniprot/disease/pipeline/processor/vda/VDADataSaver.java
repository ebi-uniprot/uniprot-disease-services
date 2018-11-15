package uk.ac.ebi.uniprot.disease.pipeline.processor.vda;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.uniprot.disease.model.disgenet.VariantDiseaseAssociation;
import uk.ac.ebi.uniprot.disease.pipeline.processor.common.BaseProcessor;
import uk.ac.ebi.uniprot.disease.pipeline.request.DiseaseRequest;

import java.io.IOException;
import java.util.List;

/**
 * Class responsible for storing the VDA data in DB
 * @author sahmad
 */

public class VDADataSaver extends BaseProcessor {
    private static final Logger LOGGER = LoggerFactory.getLogger(VDADataSaver.class);
    private static final String PROCESSOR_NAME = "VDADataSaver";

    @Override
    public String getProcessorName(){
        return PROCESSOR_NAME;
    }

    @Override
    public void processRequest(DiseaseRequest request) throws IOException {
        LOGGER.debug("Going to persist VDA parsed data");
        if(!request.getParsedGDARecords().isEmpty()) {
            persistRecords(request.getParsedVDARecords());
            // enrich data in request TODO with time and other info
        } else if(nextProcessor != null){ // move to the next processor
            LOGGER.debug("Invoking the next processor {}", nextProcessor.getProcessorName());
            nextProcessor.processRequest(request);
        }
    }

    private void persistRecords(List<VariantDiseaseAssociation> parsedRecords) {
        for(VariantDiseaseAssociation vda : parsedRecords){
            persistRecord(vda);
        }
    }

    private void persistRecord(VariantDiseaseAssociation vda) {
        //TODO write actual code to store the data
        //LOGGER.debug("Record persisted {}", gda);
    }
}
