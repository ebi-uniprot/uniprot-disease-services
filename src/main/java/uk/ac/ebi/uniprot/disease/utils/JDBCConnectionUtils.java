package uk.ac.ebi.uniprot.disease.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class JDBCConnectionUtils {

    public static Connection getConnection(String userName, String password, String jdbcUrl) throws SQLException {
        Connection conn = null;
        Properties connectionProps = new Properties();
        connectionProps.put("user", userName);
        connectionProps.put("password", password);
        conn = DriverManager.getConnection(jdbcUrl, connectionProps);
        System.out.println("Connected to database");
        return conn;
    }

    public static void closeConnection(Connection conn){
        try {
            if(conn != null){
                conn.close();
                conn = null;
            }
        } catch(SQLException sqe){
            System.out.println(sqe.getLocalizedMessage());
        }
    }
}
