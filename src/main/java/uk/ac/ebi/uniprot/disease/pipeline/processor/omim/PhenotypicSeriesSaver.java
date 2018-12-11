package uk.ac.ebi.uniprot.disease.pipeline.processor.omim;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.List;

public class PhenotypicSeriesSaver extends OMIMDataSaver {
    private static final Logger LOGGER = LoggerFactory.getLogger(PhenotypicSeriesSaver.class);
    private static final String INSERT_QUERY = "INSERT INTO disease.omim_phenotypic_series "+
            "(phenotypic_series_number, mim_number, phenotype) VALUES(?, ?, ?)";

    public PhenotypicSeriesSaver(String user, String passwd, String jdbcUrl) throws SQLException {
        super(user, passwd, jdbcUrl);
    }

    public void persistRecords(List<List<String>> records) throws SQLException {
        try(PreparedStatement ps = connxn.prepareStatement(INSERT_QUERY)){
            for(List<String> record : records){
                if(record.size() == 3) {
                    if (record.get(0) == null) {
                        ps.setNull(1, Types.VARCHAR);
                    } else {
                        ps.setString(1, record.get(0));
                    }
                    if (record.get(1) == null) {
                        ps.setNull(2, Types.INTEGER);
                    } else {
                        ps.setInt(2, Integer.parseInt(record.get(1)));
                    }

                    if (record.get(2) == null) {
                        ps.setNull(3, Types.VARCHAR);
                    } else {
                        ps.setString(3, record.get(2));
                    }
                    ps.addBatch();
                }
            }
            int[] updatedCounts = ps.executeBatch();
            LOGGER.debug("No. of records inserted in this batch {}", updatedCounts.length);

        }

    }


}
