
package uk.ac.ebi.uniprot.ds.importer.util;

import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

@Slf4j
public class JDBCConnectionUtils {

    public static Connection getConnection(String userName, String password, String jdbcUrl) throws SQLException {
        Connection conn;
        Properties connectionProps = new Properties();
        connectionProps.put("user", userName);
        connectionProps.put("password", password);
        conn = DriverManager.getConnection(jdbcUrl, connectionProps);
        return conn;
    }

    public static void closeConnection(Connection conn){
        try {
            if(conn != null){
                conn.close();
            }
        } catch(SQLException sqe){
            log.error(sqe.getLocalizedMessage());
        }
    }

//    public static void main(String[] args) throws SQLException {
//        String url = "jdbc:oracle:thin:@//ora-vm-065.ebi.ac.uk:1531/chempro";
//        String usr = "uniprot";
//        String passwd = "uniprot_ro";
//        Connection connxn = getConnection(usr, passwd, url);
//        ResultSet result = connxn.createStatement().executeQuery("SELECT  drug.name AS name,   mdict.CHEMBL_ID AS source_id,  " +
//                "mdict.MOLECULE_TYPE AS molecule_type  FROM  chembl_24_app.target_dictionary dict, " +
//                "chembl_24_app.record_drug_targets drug, CHEMBL_24_APP.COMPOUND_RECORDS crec, CHEMBL_24_APP.MOLECULE_DICTIONARY " +
//                "mdict WHERE dict.tid = drug.tid AND crec.record_id = drug.record_id and  crec.molregno = mdict.molregno AND  " +
//                "dict.chembl_id='CHEMBL1893'");
//        while(result.next()){
//            System.out.print(result.getString(1) + "\t");
//            System.out.print(result.getString(2) + "\t");
//            System.out.print(result.getString(3));
//            System.out.println();
//        }
//        System.out.println();
//    }
}
