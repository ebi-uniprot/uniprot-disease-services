package uk.ac.ebi.uniprot.disease.pipeline.processor.omim;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class GeneMapSaver extends OMIMDataSaver {
    private static final Logger LOGGER = LoggerFactory.getLogger(GeneMapSaver.class);
    private static final int TOTAL_FIELDS_COUNT = 14;

    private static final String INSERT_QUERY = "INSERT INTO disease.omim_genemap " +
            "(chromosome, genomic_start, genomic_end, cyto_location, " +
            "computed_cyto_location, mim_id, gene_symbols, gene_name, approved_symbol, " +
            "entrez_gene_id, ensembl_gene_id, comments, phenotypes, mouse_gene_id) " +
            "VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

    public GeneMapSaver(String user, String passwd, String jdbcUrl) throws SQLException {
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
                ps.setLong(2, Long.parseLong(record.get(1)));
                ps.setLong(3, Long.parseLong(record.get(2)));
                setNullSafeString(ps, 4, record.get(3));
                setNullSafeString(ps, 5, record.get(4));
                setNullSafeInt(ps, 6, record.get(5));
                setNullSafeString(ps, 7, record.get(6));
                setNullSafeString(ps, 8, record.get(7));
                setNullSafeString(ps, 9, record.get(8));
                setNullSafeInt(ps, 10, record.get(9));
                setNullSafeString(ps, 11, record.get(10));
                setNullSafeString(ps, 12, record.get(11));
                setNullSafeString(ps, 13, record.get(12));
                setNullSafeString(ps, 14, record.get(13));

                ps.addBatch();
            }
            int[] updatedCounts = ps.executeBatch();
            LOGGER.debug("No. of records inserted in this batch {}", updatedCounts.length);
        }

    }

}
