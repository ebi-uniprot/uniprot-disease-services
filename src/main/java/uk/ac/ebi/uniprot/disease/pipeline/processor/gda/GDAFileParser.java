package uk.ac.ebi.uniprot.disease.pipeline.processor.gda;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.uniprot.disease.model.disgenet.GeneDiseaseAssociation;
import uk.ac.ebi.uniprot.disease.pipeline.processor.common.BaseFileParser;
import uk.ac.ebi.uniprot.disease.pipeline.request.DiseaseRequest;
import uk.ac.ebi.uniprot.disease.service.tsv.GeneDiseaseParser;
import uk.ac.ebi.uniprot.disease.service.tsv.TSVReader;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

/**
 * Processor to handle GDA file parsing and creating models
 * @author sahmad
 */

public class GDAFileParser extends BaseFileParser {
    private static final Logger LOGGER = LoggerFactory.getLogger(GDAFileParser.class);
    private static final String PROCESSOR_NAME = "GDAFileParser";

    @Override
    public String getProcessorName(){
        return PROCESSOR_NAME;
    }

    @Override
    public void processRequest(DiseaseRequest request) throws IOException, SQLException {
        LOGGER.debug("Going to parse the GDA input file {}", request.getUncompressedFilePath());
        // don't move to the next step until
        TSVReader reader = new TSVReader(request.getUncompressedFilePath());
        GeneDiseaseParser parser = new GeneDiseaseParser(reader);
        List<GeneDiseaseAssociation> gdas;
        long count = 0L;
        do {
            long startTime = System.currentTimeMillis();
            gdas = parser.parseRecords(request.getBatchSize());
            // enrich the request and pass on
            request.setParsedGDARecords(gdas);
            count += gdas.size();
            long endTime = System.currentTimeMillis();

            updateMetrics(request, gdas.size(), startTime, endTime);

            LOGGER.debug("The GDA file is parsed and request is enriched with parsed records {}", request);
            if (nextProcessor != null) {
                LOGGER.debug("Invoking the next processor {}", nextProcessor.getProcessorName());
                nextProcessor.processRequest(request);
            }
        }while(!gdas.isEmpty());


        LOGGER.debug("Total GDA records parsed and saved {}", count);

    }


}
