package uk.ac.ebi.uniprot.disease.pipeline.processor.gda;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.uniprot.disease.model.disgenet.DataTypes;
import uk.ac.ebi.uniprot.disease.model.disgenet.GeneDiseaseAssociation;
import uk.ac.ebi.uniprot.disease.model.disgenet.GeneDiseasePMIDAssociation;
import uk.ac.ebi.uniprot.disease.pipeline.processor.common.BaseDataSaver;
import uk.ac.ebi.uniprot.disease.pipeline.request.DiseaseRequest;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.List;

/**
 * Class responsible for storing the data in DB
 * @author sahmad
 */

public class GDADataSaver extends BaseDataSaver {
    private static final Logger LOGGER = LoggerFactory.getLogger(GDADataSaver.class);
    private static final String PROCESSOR_NAME = "GDADataSaver";
    private static final String INSERT_QUERY_GDA = "INSERT INTO disease.dgn_gda " +
            "(gene_id, gene_symbol, disease_id, disease_name, score, no_of_pmids, no_of_snps, data_source) " +
            "VALUES(?, ?, ?, ?, ?, ?, ?, ?)";

    private static final String INSERT_QUERY_GDPA = "INSERT INTO disease.dgn_gdpa " +
            "(gene_id, disease_id, pmid, gene_symbol, disease_name, disease_type, association_type, sentence, score, data_source) " +
            "VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

    @Override
    public String getProcessorName(){
        return PROCESSOR_NAME;
    }

    @Override
    public void processRequest(DiseaseRequest request) throws IOException, SQLException {
        LOGGER.debug("Going to persist parsed GDA/GDPA data");
        if(hasMoreData(request)) {
            long startTime = System.currentTimeMillis();
            int size = 0;

            if(request.getDataType() == DataTypes.gda) {
                if (request.isStore()) { // store if set to true
                    List<GeneDiseaseAssociation> gdas = request.getParsedGDARecords();
                    persistGDARecords(request, gdas); // save the data to db
                    size = gdas.size();
                }
            } else if(request.getDataType() == DataTypes.gdpa){
                if (request.isStore()) { // store if set to true
                    List<GeneDiseasePMIDAssociation> gdpas = request.getParsedGDPARecords();
                    persistGDPARecords(request, gdpas); // save the data to db
                    size = gdpas.size();
                }
            } else {
                throw new IllegalArgumentException("The data file of type " + request.getDataType() + " not supported");
            }

            long endTime = System.currentTimeMillis();
            updateMetrics(request, size, startTime, endTime);
        } else if(nextProcessor != null){ // move to the next processor
            LOGGER.debug("Invoking the next processor {}", nextProcessor.getProcessorName());
            nextProcessor.processRequest(request);
        }
    }

    private boolean hasMoreData(DiseaseRequest request) {
        if(request.getDataType() == DataTypes.gda){
            return request.getParsedGDARecords() != null && !request.getParsedGDARecords().isEmpty();
        } else if (request.getDataType() == DataTypes.gdpa){
            return request.getParsedGDPARecords() != null && !request.getParsedGDPARecords().isEmpty();
        } else {
            throw new IllegalArgumentException("The data file of type " + request.getDataType() + " not supported");
        }

    }

    private void persistGDARecords(DiseaseRequest request, List<GeneDiseaseAssociation> parsedRecords) throws SQLException {
        Connection conn = getConnection(request);
        PreparedStatement ps = conn.prepareStatement(INSERT_QUERY_GDA);
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

    private void persistGDPARecords(DiseaseRequest request, List<GeneDiseasePMIDAssociation> parsedRecords) throws SQLException {
        Connection conn = getConnection(request);
        PreparedStatement ps = conn.prepareStatement(INSERT_QUERY_GDPA);
        //(gene_id, disease_id, pmid, gene_symbol, disease_name, disease_type, association_type,
        // sentence, score, data_source
        for(GeneDiseasePMIDAssociation gdpa : parsedRecords){
            ps.setInt(1, gdpa.getGeneId());
            ps.setString(2, gdpa.getDiseaseId());
            if(gdpa.getPmid() != null) {
                ps.setLong(3, gdpa.getPmid());
            } else {
                ps.setNull(3, Types.BIGINT);
            }
            ps.setString(4, gdpa.getGeneSymbol());
            ps.setString(5, gdpa.getDiseaseName());
            ps.setString(6, gdpa.getDiseaseType());
            ps.setString(7, gdpa.getAssociationType());
            ps.setString(8, gdpa.getSentence());
            ps.setDouble(9, gdpa.getScore());
            ps.setString(10, gdpa.getSource());
            ps.addBatch();
        }

        int[] updatedCounts = ps.executeBatch();
        ps.close();
        LOGGER.debug("No. of records inserted in this batch {}", updatedCounts.length);
    }
}
