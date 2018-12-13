package uk.ac.ebi.uniprot.disease.pipeline.processor.omim;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class MIMTitleSaver extends OMIMDataSaver {
    private static final Logger LOGGER = LoggerFactory.getLogger(MIMTitleSaver.class);
    private static final int TOTAL_FIELDS_COUNT = 5;

    private static final String INSERT_QUERY = "INSERT INTO disease.omim_mim_titles " +
            "(prefix, mim_id, preferred_title, alternative_title, included_title) " +
            "VALUES(?, ?, ?, ?, ?)";

    public MIMTitleSaver(String user, String passwd, String jdbcUrl) throws SQLException {
        super(user, passwd, jdbcUrl);
    }

    @Override
    public void persistRecords(List<List<String>> records) throws SQLException {
        try (PreparedStatement ps = connxn.prepareStatement(INSERT_QUERY)) {
            for (List<String> record : records) {
                if(record.size() < TOTAL_FIELDS_COUNT){
                    record = fillNull(record, TOTAL_FIELDS_COUNT);
                }
                ps.setString(1, record.get(0));
                ps.setInt(2, Integer.parseInt(record.get(1)));
                setNullSafeString(ps,3, record.get(2));
                setNullSafeString(ps, 4, record.get(3));
                setNullSafeString(ps, 5, record.get(4));
                ps.addBatch();
            }
            int[] updatedCounts = ps.executeBatch();
            LOGGER.debug("No. of records inserted in this batch {}", updatedCounts.length);
        }

    }

}
