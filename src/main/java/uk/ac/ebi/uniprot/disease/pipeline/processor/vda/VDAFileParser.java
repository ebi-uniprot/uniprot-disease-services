package uk.ac.ebi.uniprot.disease.pipeline.processor.vda;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.uniprot.disease.model.disgenet.DataTypes;
import uk.ac.ebi.uniprot.disease.model.disgenet.VariantDiseaseAssociation;
import uk.ac.ebi.uniprot.disease.model.disgenet.VariantDiseasePMIDAssociation;
import uk.ac.ebi.uniprot.disease.pipeline.processor.common.BaseFileParser;
import uk.ac.ebi.uniprot.disease.pipeline.request.DiseaseRequest;
import uk.ac.ebi.uniprot.disease.service.tsv.TSVReader;
import uk.ac.ebi.uniprot.disease.service.tsv.VariantDiseaseParser;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

/**
 * Processor to handle file parsing VDA data and creating models
 * @author sahmad
 */

public class VDAFileParser extends BaseFileParser {
    private static final Logger LOGGER = LoggerFactory.getLogger(VDAFileParser.class);
    private static final String PROCESSOR_NAME = "VDAFileParser";

    @Override
    public String getProcessorName(){
        return PROCESSOR_NAME;
    }

    @Override
    public void processRequest(DiseaseRequest request) throws IOException, SQLException {
        LOGGER.debug("Going to parse the VDA input file {}", request.getUncompressedFilePath());

        TSVReader reader = new TSVReader(request.getUncompressedFilePath());
        VariantDiseaseParser parser = new VariantDiseaseParser(reader);
        long count = 0L;

        if(request.getDataType() == DataTypes.vda) {
            List<VariantDiseaseAssociation> vdas;
            do {
                long startTime = System.currentTimeMillis();
                vdas = parser.parseVDARecords(request.getBatchSize());
                // enrich the request and pass on
                request.setParsedVDARecords(vdas);
                count += vdas.size();

                long endTime = System.currentTimeMillis();

                updateMetrics(request, vdas.size(), startTime, endTime, reader.getRecordCount());

                //LOGGER.debug("The file is parsed and request is enriched with parsed records");
                if (nextProcessor != null) {
                    LOGGER.debug("Invoking the next processor {}", nextProcessor.getProcessorName());
                    nextProcessor.processRequest(request);
                }
            } while (!vdas.isEmpty());
        }else if(request.getDataType() == DataTypes.vdpa){
            List<VariantDiseasePMIDAssociation> vdpas;
            do {
                long startTime = System.currentTimeMillis();
                vdpas = parser.parseVDPARecords(request.getBatchSize());
                // enrich the request and pass on
                request.setParsedVDPARecords(vdpas);
                count += vdpas.size();

                long endTime = System.currentTimeMillis();

                updateMetrics(request, vdpas.size(), startTime, endTime, reader.getRecordCount());

                //LOGGER.debug("The file is parsed and request is enriched with parsed records");
                if (nextProcessor != null) {
                    //LOGGER.debug("Invoking the next processor {}", nextProcessor.getProcessorName());
                    nextProcessor.processRequest(request);
                }
            } while (!vdpas.isEmpty());
        } else {
            throw new IllegalArgumentException("The data file of type " + request.getDataType() + " not supported");
        }

        LOGGER.debug("Total VDA records parsed and saved {}", count);

    }
}
