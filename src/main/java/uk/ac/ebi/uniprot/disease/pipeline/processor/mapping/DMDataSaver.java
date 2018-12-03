package uk.ac.ebi.uniprot.disease.pipeline.processor.mapping;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.uniprot.disease.model.disgenet.DataTypes;
import uk.ac.ebi.uniprot.disease.model.disgenet.DiseaseMapping;
import uk.ac.ebi.uniprot.disease.model.disgenet.UniProtGene;
import uk.ac.ebi.uniprot.disease.pipeline.processor.common.BaseDataSaver;
import uk.ac.ebi.uniprot.disease.pipeline.request.DiseaseRequest;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class DMDataSaver extends BaseDataSaver {
    private static final Logger LOGGER = LoggerFactory.getLogger(DMDataSaver.class);
    private static final String PROCESSOR_NAME = "DMDataSaver";
    private static final String INSERT_QUERY_DM = "INSERT INTO disease.dgn_disease_mapping " +
            "(disease_id, disease_name, vocabulary, code, vocabulary_name) " +
            "VALUES(?, ?, ?, ?, ?)";
    private static final String INSERT_QUERY_UG = "INSERT INTO disease.dgn_uniprot_gene " +
            "(uniprot_id, gene_id) VALUES(?, ?)";

    @Override
    public void processRequest(DiseaseRequest request) throws IOException, SQLException {
        if (hasMoreData(request)) {
            long startTime = System.currentTimeMillis();
            int size = 0;
            if (request.getDataType() == DataTypes.dm) {
                if(request.isStore()){
                    List<DiseaseMapping> dms = request.getParsedDiseaseMappings();
                    persistRecords(request, dms);
                    size = dms.size();
                }
            } else if (request.getDataType() == DataTypes.ug) {
                if(request.isStore()){
                    List<UniProtGene> upgs = request.getParsedUniProtGeneMappings();
                    persistUniProtGeneRecords(request, upgs);
                    size = upgs.size();
                }
            }else {
                throw new IllegalArgumentException("The data file of type " + request.getDataType() + " not supported");
            }

            long endTime = System.currentTimeMillis();
            updateMetrics(request, size, startTime, endTime);
        } else if (nextProcessor != null) { // move to the next processor
            LOGGER.debug("Invoking the next processor {}", nextProcessor.getProcessorName());
            nextProcessor.processRequest(request);
        }
    }

    private void persistRecords(DiseaseRequest request, List<DiseaseMapping> dms) throws SQLException {
        Connection conn = getConnection(request);
        PreparedStatement ps = conn.prepareStatement(INSERT_QUERY_DM);
        for (DiseaseMapping dm : dms) {
            ps.setString(1, dm.getDiseaseId());
            ps.setString(2, dm.getName());
            ps.setString(3, dm.getVocab());
            ps.setString(4, dm.getCode());
            ps.setString(5, dm.getVocabName());
            ps.addBatch();
        }

        int[] updatedCounts = ps.executeBatch();
        ps.close();
        LOGGER.debug("No. of records inserted in this batch {}", updatedCounts.length);
    }

    private void persistUniProtGeneRecords(DiseaseRequest request, List<UniProtGene> upgs) throws SQLException {
        Connection conn = getConnection(request);
        PreparedStatement ps = conn.prepareStatement(INSERT_QUERY_UG);
        for (UniProtGene upg : upgs) {
            ps.setString(1, upg.getUniProtId());
            ps.setLong(2, upg.getGeneId());
            ps.addBatch();
        }

        int[] updatedCounts = ps.executeBatch();
        ps.close();
        LOGGER.debug("No. of records inserted in this batch {}", updatedCounts.length);
    }


    private boolean hasMoreData(DiseaseRequest request) {
        if(request.getDataType() == DataTypes.dm){
            return request.getParsedDiseaseMappings() != null && !request.getParsedDiseaseMappings().isEmpty();
        } else if(request.getDataType() == DataTypes.ug){
            return request.getParsedUniProtGeneMappings() != null && !request.getParsedUniProtGeneMappings().isEmpty();
        }else {
            throw new IllegalArgumentException("The data file of type " + request.getDataType() + " not supported");
        }
    }

    @Override
    public String getProcessorName() {
        return PROCESSOR_NAME;
    }
}
