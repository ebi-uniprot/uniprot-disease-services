package uk.ac.ebi.uniprot.disease.pipeline.processor.vda;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.uniprot.disease.model.disgenet.DataTypes;
import uk.ac.ebi.uniprot.disease.model.disgenet.VariantDiseaseAssociation;
import uk.ac.ebi.uniprot.disease.model.disgenet.VariantDiseasePMIDAssociation;
import uk.ac.ebi.uniprot.disease.pipeline.processor.common.BaseDataSaver;
import uk.ac.ebi.uniprot.disease.pipeline.request.DiseaseRequest;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.List;

/**
 * Class responsible for storing the VDA data in DB
 *
 * @author sahmad
 */

public class VDADataSaver extends BaseDataSaver {
    private static final Logger LOGGER = LoggerFactory.getLogger(VDADataSaver.class);
    private static final String PROCESSOR_NAME = "VDADataSaver";
    private static final String INSERT_QUERY_VDA = "INSERT INTO disease.dgn_vda " +
            "(snp_id, disease_id, disease_name, score, no_of_pmids, data_source) " +
            "VALUES(?, ?, ?, ?, ?, ?)";
    private static final String INSERT_QUERY_VDPA = "INSERT INTO disease.dgn_vdpa " +
            "(snp_id, disease_id, sentence, pmid, score, data_source, disease_name, disease_type, chromosome, chromosome_position) " +
            "VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

    @Override
    public String getProcessorName() {
        return PROCESSOR_NAME;
    }

    @Override
    public void processRequest(DiseaseRequest request) throws IOException, SQLException {
        LOGGER.debug("Going to persist VDA/VDPA parsed data");
        if (hasMoreData(request)) {
            long startTime = System.currentTimeMillis();
            int size = 0;
            if (request.getDataType() == DataTypes.vda) {
                if (request.isStore()) { // store if set to true
                    List<VariantDiseaseAssociation> vdas = request.getParsedVDARecords();
                    persistVDARecords(request, vdas);
                    size = vdas.size();
                }
            } else if (request.getDataType() == DataTypes.vdpa) {
                if (request.isStore()) { // store if set to true
                    List<VariantDiseasePMIDAssociation> vdpas = request.getParsedVDPARecords();
                    persistVDPARecords(request, vdpas);
                    size = vdpas.size();
                }
            } else {
                throw new IllegalArgumentException("The data file of type " + request.getDataType() + " not supported");
            }

            long endTime = System.currentTimeMillis();
            updateMetrics(request, size, startTime, endTime);
        } else if (nextProcessor != null) { // move to the next processor
            LOGGER.debug("Invoking the next processor {}", nextProcessor.getProcessorName());
            nextProcessor.processRequest(request);
        }
    }

    private void persistVDARecords(DiseaseRequest request, List<VariantDiseaseAssociation> parsedRecords) throws SQLException {
        Connection conn = getConnection(request);
        PreparedStatement ps = conn.prepareStatement(INSERT_QUERY_VDA);
        // (snp_id, disease_id, disease_name, score, no_of_pmids, data_source)
        for (VariantDiseaseAssociation vda : parsedRecords) {
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

    private void persistVDPARecords(DiseaseRequest request, List<VariantDiseasePMIDAssociation> parsedRecords) throws SQLException {
        Connection conn = getConnection(request);
        PreparedStatement ps = conn.prepareStatement(INSERT_QUERY_VDPA);
        // snpId	diseaseId	sentence	pmid	score	originalSource	diseaseName	diseaseType	chromosome	position
        for (VariantDiseasePMIDAssociation vdpa : parsedRecords) {
            ps.setString(1, vdpa.getSnpId());
            ps.setString(2, vdpa.getDiseaseId());
            ps.setString(3, vdpa.getSentence());
            if (vdpa.getPmid() == null) {
                ps.setNull(4, Types.BIGINT);
            } else {
                ps.setLong(4, vdpa.getPmid());
            }

            ps.setDouble(5, vdpa.getScore());
            ps.setString(6, vdpa.getOriginalSource());
            ps.setString(7, vdpa.getDiseaseName());
            ps.setString(8, vdpa.getDiseaseType());

            if (vdpa.getChromosome() != null) {
                ps.setInt(9, vdpa.getChromosome());
            } else {
                ps.setNull(9, Types.INTEGER);
            }

            if (vdpa.getChromosomePosition() != null) {
                ps.setLong(10, vdpa.getChromosomePosition());
            } else {
                ps.setNull(10, Types.BIGINT);
            }
            ps.addBatch();
        }

        int[] updatedCounts = ps.executeBatch();
        ps.close();
        LOGGER.debug("No. of records inserted in this batch {}", updatedCounts.length);
    }

    private boolean hasMoreData(DiseaseRequest request) {
        if (request.getDataType() == DataTypes.vda) {
            return request.getParsedVDARecords() != null && !request.getParsedVDARecords().isEmpty();
        } else if (request.getDataType() == DataTypes.vdpa) {
            return request.getParsedVDPARecords() != null && !request.getParsedVDPARecords().isEmpty();
        } else {
            throw new IllegalArgumentException("The data file of type " + request.getDataType() + " not supported");
        }

    }

}
