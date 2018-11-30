package uk.ac.ebi.uniprot.disease.pipeline.processor.vda;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.uniprot.disease.model.disgenet.VariantDiseaseAssociation;
import uk.ac.ebi.uniprot.disease.pipeline.processor.common.BaseDataSaver;
import uk.ac.ebi.uniprot.disease.pipeline.request.DiseaseRequest;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

/**
 * Class responsible for storing the VDA data in DB
 * @author sahmad
 */

public class VDADataSaver extends BaseDataSaver {
    private static final Logger LOGGER = LoggerFactory.getLogger(VDADataSaver.class);
    private static final String PROCESSOR_NAME = "VDADataSaver";
    private static final String INSERT_QUERY = "INSERT INTO disease.dgn_vda " +
            "(snp_id, disease_id, disease_name, score, no_of_pmids, data_source) " +
            "VALUES(?, ?, ?, ?, ?, ?)";

    @Override
    public String getProcessorName(){
        return PROCESSOR_NAME;
    }

    @Override
    public void processRequest(DiseaseRequest request) throws IOException, SQLException {
        LOGGER.debug("Going to persist VDA parsed data");
        if(!request.getParsedVDARecords().isEmpty()) {
            long startTime = System.currentTimeMillis();

            List<VariantDiseaseAssociation> vdas = request.getParsedVDARecords();
            if(request.isStore()) {
                persistRecords(request, vdas);
            }

            long endTime = System.currentTimeMillis();
            updateMetrics(request, vdas.size(), startTime, endTime);
        } else if(nextProcessor != null){ // move to the next processor
            LOGGER.debug("Invoking the next processor {}", nextProcessor.getProcessorName());
            nextProcessor.processRequest(request);
        }
    }

    private void persistRecords(DiseaseRequest request, List<VariantDiseaseAssociation> parsedRecords) throws SQLException {
        Connection conn = getConnection(request);
        PreparedStatement ps = conn.prepareStatement(INSERT_QUERY);
        // (snp_id, disease_id, disease_name, score, no_of_pmids, data_source)
        for(VariantDiseaseAssociation vda : parsedRecords){
            ps.setString(1, vda.getSnpId());
            ps.setString(2, vda.getDiseaseId());
            ps.setString(3, vda.getDiseaseName());
            ps.setDouble(4, vda.getScore());
            ps.setInt(5, vda.getPmidCount());
            ps.setString(6, vda.getSource());
            ps.addBatch();
        }

        int[] updatedCounts = ps.executeBatch();
        ps.close();
        LOGGER.debug("No. of records inserted in this batch {}", updatedCounts.length);
    }
}
