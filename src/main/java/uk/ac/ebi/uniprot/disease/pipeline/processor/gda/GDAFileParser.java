package uk.ac.ebi.uniprot.disease.pipeline.processor.gda;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.uniprot.disease.model.disgenet.DataTypes;
import uk.ac.ebi.uniprot.disease.model.disgenet.GeneDiseaseAssociation;
import uk.ac.ebi.uniprot.disease.model.disgenet.GeneDiseasePMIDAssociation;
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
        LOGGER.debug("Going to parse the {} input file {}", request.getDataType(), request.getUncompressedFilePath());
        long count = 0L;
        TSVReader reader = new TSVReader(request.getUncompressedFilePath());
        GeneDiseaseParser parser = new GeneDiseaseParser(reader);

        if(request.getDataType() == DataTypes.gda) {
            List<GeneDiseaseAssociation> gdas;
            do {
                long startTime = System.currentTimeMillis();
                gdas = parser.parseGDARecords(request.getBatchSize());
                // enrich the request and pass on
                request.setParsedGDARecords(gdas);
                count += gdas.size();
                long endTime = System.currentTimeMillis();

                updateMetrics(request, gdas.size(), startTime, endTime, reader.getRecordCount());

                LOGGER.debug("The GDA file is parsed and request is enriched with parsed records {}", request);
                if (nextProcessor != null) {
                    LOGGER.debug("Invoking the next processor {}", nextProcessor.getProcessorName());
                    nextProcessor.processRequest(request);
                }
            } while (!gdas.isEmpty());
        } else if(request.getDataType() == DataTypes.gdpa){
            List<GeneDiseasePMIDAssociation> gdpas;
            do {
                long startTime = System.currentTimeMillis();
                gdpas = parser.parseGDPARecords(request.getBatchSize());
                // enrich the request and pass on
                request.setParsedGDPARecords(gdpas);
                count += gdpas.size();
                long endTime = System.currentTimeMillis();

                updateMetrics(request, gdpas.size(), startTime, endTime, reader.getRecordCount());

                LOGGER.debug("The GDPA file is parsed and request is enriched with parsed records {}", request);
                if (nextProcessor != null) {
                    LOGGER.debug("Invoking the next processor {}", nextProcessor.getProcessorName());
                    nextProcessor.processRequest(request);
                }
            } while (!gdpas.isEmpty());
        } else {
            throw new IllegalArgumentException("The data file of type " + request.getDataType() + " not supported");
        }


        LOGGER.debug("Total {} records parsed and saved {}", request.getDataType(), count);

    }


}
