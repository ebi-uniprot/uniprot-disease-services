package uk.ac.ebi.uniprot.disease.pipeline.processor.mapping;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.uniprot.disease.model.disgenet.DataTypes;
import uk.ac.ebi.uniprot.disease.model.disgenet.DiseaseMapping;
import uk.ac.ebi.uniprot.disease.pipeline.processor.common.BaseFileParser;
import uk.ac.ebi.uniprot.disease.pipeline.request.DiseaseRequest;
import uk.ac.ebi.uniprot.disease.service.tsv.TSVReader;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author sahmad
 * class responsinble for parsing and converting to java pojo of records in disease mapping file
 */
public class DMFileParser extends BaseFileParser {
    private static final Logger LOGGER = LoggerFactory.getLogger(DMFileParser.class);
    private static final String PROCESSOR_NAME = "DMFileParser";

    @Override
    public void processRequest(DiseaseRequest request) throws IOException, SQLException {
        long count = 0L;
        TSVReader reader = new TSVReader(request.getUncompressedFilePath());
        List<DiseaseMapping> dms;
        if(request.getDataType() == DataTypes.dm){
            do {
                long startTime = System.currentTimeMillis();
                dms = getDiseaseMappings(request, reader);
                // enrich the request and pass on
                request.setParsedDiseaseMappings(dms);
                count += dms.size();
                long endTime = System.currentTimeMillis();

                updateMetrics(request, dms.size(), startTime, endTime, reader.getRecordCount());

                LOGGER.debug("The disease mapping file is parsed and request is enriched with parsed records");
                if (nextProcessor != null) {
                    LOGGER.debug("Invoking the next processor {}", nextProcessor.getProcessorName());
                    nextProcessor.processRequest(request);
                }

            } while(!dms.isEmpty());


        } else {
            throw new IllegalArgumentException("The data file of type " + request.getDataType() + " not supported");
        }

    }

    private List<DiseaseMapping> getDiseaseMappings(DiseaseRequest request, TSVReader reader) {
        int batchSize = request.getBatchSize();
        int count = 0;
        List<DiseaseMapping> dms = new ArrayList<>();

        while(reader.hasMoreRecord() && batchSize > count){
            List<String> record = reader.getRecord();
            try {
                DiseaseMapping dm = convertToDiseaseMapping(record);
                dms.add(dm);
                count++;
            } catch (Exception ex){
                LOGGER.error("Error is {}", ex.getLocalizedMessage());
                LOGGER.error("Failed record {}", record);
            }
        }

        return dms;
    }

    private DiseaseMapping convertToDiseaseMapping(List<String> record) {
        DiseaseMapping.DiseaseMappingBuilder builder = DiseaseMapping.builder();
        builder.diseaseId(record.get(0)).name(record.get(1)).vocab(record.get(2));
        builder.code(record.get(3)).vocabName(record.get(4));
        return builder.build();
    }

    @Override
    public String getProcessorName() {
        return PROCESSOR_NAME;
    }
}
