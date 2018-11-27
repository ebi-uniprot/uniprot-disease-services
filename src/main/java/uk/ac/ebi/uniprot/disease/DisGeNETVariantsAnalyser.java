package uk.ac.ebi.uniprot.disease;

import uk.ac.ebi.uniprot.disease.service.tsv.TSVReader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.sql.*;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class DisGeNETVariantsAnalyser {
    private static final Integer BATCH_SIZE = 100;

    public static void main(String[] args) throws SQLException, FileNotFoundException {

        String url = "jdbc:postgresql://pgsql-hxvm7-011.ebi.ac.uk:5432/unpvardev";
        String user = "variant";
        String password = "uniDvar";
        Connection con = null;

        PrintWriter pw = new PrintWriter(new File("missingVariants.csv"));
        StringBuilder sb = new StringBuilder();
        sb.append("snpId");
        sb.append(',');
        sb.append("diseaseId");
        sb.append(',');
        sb.append("diseaseName");
        sb.append(',');
        sb.append("score");
        sb.append(',');
        sb.append("NofPmids");
        sb.append(',');
        sb.append("source");
        sb.append('\n');

        int count = 0;
        try {
            con = DriverManager.getConnection(url, user, password);
            TSVReader reader = new TSVReader("/Users/sahmad/Downloads/DisGeNET/all_variant_disease_association.tsv");
            Set<String> rsIds = new HashSet<>();
            while (reader.hasMoreRecord()) {
                count++;
                List<String> record = reader.getRecord();
                String rsId = record.get(0);
                Statement st = con.createStatement();
                ResultSet rs = st.executeQuery("select count(distinct dbsnp_id) from variation_human where dbsnp_id in ('"
                        + rsId + "')");
                if (rs.next()) {
                    int lm = Integer.parseInt(rs.getString(1));
                    if (lm == 0) {
                        sb.append(getCSV(record));
                    }
                }
                System.out.println("Processing record number: " + count);
            }

        } catch (SQLException ex) {
            System.out.println(ex.getLocalizedMessage());
        } finally {
            if (con != null)
                con.close();
        }

        pw.write(sb.toString());
        pw.close();
        System.out.println("Total records processed " + count);
    }

    private static String getCSV(List<String> record) {
        StringBuilder sb = new StringBuilder();
        sb.append(record.get(0));
        sb.append(',');
        sb.append(record.get(1));
        sb.append(',');
        sb.append(record.get(2));
        sb.append(',');
        sb.append(record.get(3));
        sb.append(',');
        sb.append(record.get(4));
        sb.append(',');
        sb.append(record.get(5));
        sb.append('\n');

        return sb.toString();
    }

    public static void main1(String[] args) throws SQLException, FileNotFoundException {

        String url = "jdbc:postgresql://pgsql-hxvm7-011.ebi.ac.uk:5432/unpvardev";
        String user = "variant";
        String password = "uniDvar";
        Connection con = null;
        int matched = 0;
        int unmatched = 0;
        int count = 0;
        try {
            con = DriverManager.getConnection(url, user, password);
            TSVReader reader = new TSVReader("/Users/sahmad/Downloads/DisGeNET/all_variant_disease_association.tsv");
            Set<String> rsIds = new HashSet<>();
            while (reader.hasMoreRecord()) {
                count++;
                rsIds.add(reader.getRecord().get(0));
                if (rsIds.size() == BATCH_SIZE) {
                    Statement st = con.createStatement();
                    ResultSet rs = st.executeQuery("select count(distinct dbsnp_id) from variation_human where dbsnp_id in ("
                            + getCommaSeparatedRSIds(rsIds) + ")");
                    if (rs.next()) {
                        int lm = Integer.parseInt(rs.getString(1));
                        int lum = BATCH_SIZE - lm;
                        matched += lm;
                        unmatched += lum;
                    } else {
                        unmatched += BATCH_SIZE;
                    }

                    rsIds.clear();
                }
            }

        } catch (SQLException ex) {
            System.out.println(ex.getLocalizedMessage());
        } finally {
            if (con != null)
                con.close();
        }

        System.out.println("Variants found in unpvardev " + matched);
        System.out.println("Variants not found in unpvardev " + unmatched);
        System.out.println("Total records processed " + count);
    }

    private static String getCommaSeparatedRSIds(Set<String> rsIds) {
        StringBuilder sb = new StringBuilder();
        for (String rsId : rsIds) {
            sb.append("'");
            sb.append(rsId);
            sb.append("'");
            sb.append(",");
        }
        sb.deleteCharAt(sb.length() - 1);
        return sb.toString();
    }
}
