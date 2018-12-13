package uk.ac.ebi.uniprot.disease.pipeline.processor.omim;

import org.apache.commons.lang3.StringUtils;
import uk.ac.ebi.uniprot.disease.utils.JDBCConnectionUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

public abstract class OMIMDataSaver {
    protected Connection connxn;

    public OMIMDataSaver(String user, String passwd, String jdbcUrl) throws SQLException {
        this.connxn = JDBCConnectionUtils.getConnection(user, passwd, jdbcUrl);;
    }
    public abstract void persistRecords(List<List<String>> records) throws SQLException;

    protected void setNullSafeString(PreparedStatement ps, int index, String value) throws SQLException {
        if(StringUtils.isEmpty(value)){
            ps.setNull(index, Types.VARCHAR);
        } else {
            ps.setString(index, value);
        }

    }

    protected void setNullSafeInt(PreparedStatement ps, int index, String value) throws SQLException {
        if(StringUtils.isEmpty(value)){
            ps.setNull(index, Types.INTEGER);
        } else {
            ps.setInt(index, Integer.parseInt(value));
        }

    }

    protected List<String> fillNull(List<String> record, int size) {
        List<String> newRecord = new ArrayList<>(record);
        for(int i = record.size(); i < size; i++){
            newRecord.add(null);
        }
        return newRecord;
    }
}
