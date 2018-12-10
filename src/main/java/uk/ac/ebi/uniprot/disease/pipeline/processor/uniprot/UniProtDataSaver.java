package uk.ac.ebi.uniprot.disease.pipeline.processor.uniprot;

import org.apache.commons.lang3.StringUtils;
import uk.ac.ebi.uniprot.disease.utils.JDBCConnectionUtils;

import java.sql.*;

public class UniProtDataSaver {
    private Connection connxn;

    private static final String INSERT_DISEASE_QUERY = "INSERT INTO disease.up_disease " +
            "(identifier, acronym, definition, accession) " +
            "VALUES(?, ?, ?, ?)";

    private static final String INSERT_CROSS_REF_QUERY = "INSERT INTO disease.up_cross_reference " +
            "(ref_type, ref_id, disease_id, ref_meta) " +
            "VALUES(?, ?, ?, ?)";

    private static final String INSERT_SYNONYM_QUERY = "INSERT INTO disease.up_alternative_name " +
            "(synonym, disease_id) VALUES(?, ?)";

    private static final String INSERT_KEYWORD_QUERY = "INSERT INTO disease.up_keyword " +
            "(key_id, key_value, disease_id) VALUES(?, ?, ?)";


    public UniProtDataSaver(String user, String passwd, String jdbcUrl) throws SQLException {
        connxn = JDBCConnectionUtils.getConnection(user, passwd, jdbcUrl);
    }

    public Integer createDisease(String identifier, String acronym, String accession, String def) throws SQLException {
        try(PreparedStatement ps = connxn.prepareStatement(INSERT_DISEASE_QUERY, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, identifier);

            if (StringUtils.isEmpty(acronym)) {
                ps.setNull(2, Types.VARCHAR);
            } else {
                ps.setString(2, acronym);
            }

            if (StringUtils.isEmpty(def)) {
                ps.setNull(3, Types.VARCHAR);
            } else {
                ps.setString(3, def);
            }
            ps.setString(4, accession);

            int affectedRows = ps.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Could not create disease " + identifier);
            }

            Integer generatedKey = getGeneratedKey(ps, identifier);

            return generatedKey;
        }
    }

    public void createCrossRef(String refType, String refId, String refMeta, Integer diseaseId) throws SQLException {
        try(PreparedStatement ps = connxn.prepareStatement(INSERT_CROSS_REF_QUERY)) {
            ps.setString(1, refType);
            ps.setString(2, refId);
            ps.setInt(3, diseaseId);
            if (StringUtils.isEmpty(refMeta)) {
                ps.setNull(4, Types.VARCHAR);
            } else {
                ps.setString(4, refMeta);
            }


            int affectedRows = ps.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Could not create cross ref for " + refId);
            }
        }
    }

    public void createSynonym(String name, Integer diseaseId) throws SQLException {
        try(PreparedStatement ps = connxn.prepareStatement(INSERT_SYNONYM_QUERY)) {
            ps.setString(1, name);
            ps.setInt(2, diseaseId);

            int affectedRows = ps.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Could not create synonym for " + name);
            }
        }
    }

    public void createKeyword(String keyId, String keyValue, Integer diseaseId) throws SQLException {
        try(PreparedStatement ps = connxn.prepareStatement(INSERT_KEYWORD_QUERY)) {
            ps.setString(1, keyId);
            ps.setString(2, keyValue);
            ps.setInt(3, diseaseId);

            int affectedRows = ps.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Could not create keyword for  " + keyId);
            }
        }
    }

    private Integer getGeneratedKey(PreparedStatement ps, String identifier) throws SQLException {
        try (ResultSet resultSet = ps.getGeneratedKeys()){
            if(resultSet.next()){
                return resultSet.getInt(1);
            } else {
                throw new SQLException("Record creation failed, ID not found for + " + identifier);
            }
        }
    }
}
