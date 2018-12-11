package uk.ac.ebi.uniprot.disease.pipeline.processor.omim;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.List;

public class MIM2GeneSaver extends OMIMDataSaver {
    private static final Logger LOGGER = LoggerFactory.getLogger(MIM2GeneSaver.class);

    private static final String INSERT_QUERY = "INSERT INTO disease.omim_mim_gene " +
            "(mim_id, mim_type, entrez_gene_id, gene_symbol, ensembl_gene_id) " +
            "VALUES(?, ?, ?, ?, ?)";

    public MIM2GeneSaver(String user, String passwd, String jdbcUrl) throws SQLException {
        super(user, passwd, jdbcUrl);
    }
    @Override
    public void persistRecords(List<List<String>> records) throws SQLException {
        try(PreparedStatement ps = connxn.prepareStatement(INSERT_QUERY)){
            for(List<String> record : records){
                int size = record.size();
                switch (size){
                    case 2 :
                        ps.setInt(1, Integer.parseInt(record.get(0)));
                        ps.setString(2, record.get(1));
                        ps.setNull(3, Types.INTEGER);
                        ps.setNull(4, Types.VARCHAR);
                        ps.setNull(5, Types.VARCHAR);
                        break;
                    case 3:
                        ps.setInt(1, Integer.parseInt(record.get(0)));
                        ps.setString(2, record.get(1));
                        ps.setInt(3, Integer.parseInt(record.get(2)));
                        ps.setNull(4, Types.VARCHAR);
                        ps.setNull(5, Types.VARCHAR);
                        break;
                    case 4:
                        ps.setInt(1, Integer.parseInt(record.get(0)));
                        ps.setString(2, record.get(1));
                        ps.setInt(3, Integer.parseInt(record.get(2)));
                        ps.setString(4, record.get(3));
                        ps.setNull(5, Types.VARCHAR);
                        break;
                    default:
                        ps.setInt(1, Integer.parseInt(record.get(0)));
                        ps.setString(2, record.get(1));
                        ps.setInt(3, Integer.parseInt(record.get(2)));
                        ps.setString(4, record.get(3));
                        ps.setString(5, record.get(4));
                }
                ps.addBatch();
            }
            int[] updatedCounts = ps.executeBatch();
            LOGGER.debug("No. of records inserted in this batch {}", updatedCounts.length);
        }

    }
}
