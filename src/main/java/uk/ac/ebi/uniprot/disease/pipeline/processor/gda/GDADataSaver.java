package uk.ac.ebi.uniprot.disease.pipeline.processor.gda;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.uniprot.disease.model.disgenet.GeneDiseaseAssociation;
import uk.ac.ebi.uniprot.disease.pipeline.processor.common.BaseDataSaver;
import uk.ac.ebi.uniprot.disease.pipeline.request.DiseaseRequest;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

/**
 * Class responsible for storing the data in DB
 * @author sahmad
 */

public class GDADataSaver extends BaseDataSaver {
    private static final Logger LOGGER = LoggerFactory.getLogger(GDADataSaver.class);
    private static final String PROCESSOR_NAME = "GDADataSaver";
    private static final String INSERT_QUERY = "INSERT INTO disease.dgn_gda " +
            "(gene_id, gene_symbol, disease_id, disease_name, score, no_of_pmids, no_of_snps, data_source) " +
            "VALUES(?, ?, ?, ?, ?, ?, ?, ?)";

    @Override
    public String getProcessorName(){
        return PROCESSOR_NAME;
    }

    @Override
    public void processRequest(DiseaseRequest request) throws IOException, SQLException {
        LOGGER.debug("Going to persist parsed GDA data");
        if(!request.getParsedGDARecords().isEmpty()) {
            long startTime = System.currentTimeMillis();
            List<GeneDiseaseAssociation> gdas = request.getParsedGDARecords();

            if(request.isStore()) { // store if set to true
                persistRecords(request, gdas); // save the data to db
            }

            long endTime = System.currentTimeMillis();
            updateMetrics(request, gdas.size(), startTime, endTime);
        } else if(nextProcessor != null){ // move to the next processor
            LOGGER.debug("Invoking the next processor {}", nextProcessor.getProcessorName());
            nextProcessor.processRequest(request);
        }
    }

    private void persistRecords(DiseaseRequest request, List<GeneDiseaseAssociation> parsedRecords) throws SQLException {
        Connection conn = getConnection(request);
        PreparedStatement ps = conn.prepareStatement(INSERT_QUERY);
        //(gene_id, gene_symbol, disease_id, disease_name, score, no_of_pmids, no_of_snps, data_source)
        for(GeneDiseaseAssociation gda : parsedRecords){
            ps.setInt(1, gda.getGeneId());
            ps.setString(2, gda.getGeneSymbol());
            ps.setString(3, gda.getDiseaseId());
            ps.setString(4, gda.getDiseaseName());
            ps.setDouble(5, gda.getScore());
            ps.setInt(6, gda.getPmidCount());
            ps.setInt(7, gda.getSnpCount());
            ps.setString(8, gda.getSource());
            ps.addBatch();
        }

        int[] updatedCounts = ps.executeBatch();
        ps.close();
        LOGGER.debug("No. of records inserted in this batch {}", updatedCounts.length);
    }
}
