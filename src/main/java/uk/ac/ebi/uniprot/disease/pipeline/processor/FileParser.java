package uk.ac.ebi.uniprot.disease.pipeline.processor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.uniprot.disease.model.disgenet.GeneDiseaseAssociation;
import uk.ac.ebi.uniprot.disease.pipeline.request.DiseaseRequest;
import uk.ac.ebi.uniprot.disease.service.tsv.GeneDiseaseParser;
import uk.ac.ebi.uniprot.disease.service.tsv.TSVReader;

import java.io.IOException;
import java.util.List;

/**
 * Processor to handle file parsing and creating models
 * @author sahmad
 */

public class FileParser extends BaseProcessor {
    private static final Logger LOGGER = LoggerFactory.getLogger(FileParser.class);
    private static final String PROCESSOR_NAME = "FileParser";

    @Override
    public String getProcessorName(){
        return PROCESSOR_NAME;
    }

    @Override
    public void processRequest(DiseaseRequest request)  throws IOException {
        LOGGER.debug("Going to parse the input file {}", request.getUncompressedFilePath());
        // don't move to the next step until
        TSVReader reader = new TSVReader(request.getUncompressedFilePath());
        GeneDiseaseParser parser = new GeneDiseaseParser(reader);
        List<GeneDiseaseAssociation> gdas;
        int count = 0;
        do {
            gdas = parser.parseRecords(request.getBatchSize());
            // enrich the request and pass on
            request.setParsedRecords(gdas);
            count += gdas.size();

            LOGGER.debug("The file is parsed and request is enriched with parsed records {}", request);
            if (nextProcessor != null) {
                LOGGER.debug("Invoking the next processor {}", nextProcessor.getProcessorName());
                nextProcessor.processRequest(request);
            }
        }while(!gdas.isEmpty());

        LOGGER.debug("Total records parsed and saved {}", count);

    }
}
