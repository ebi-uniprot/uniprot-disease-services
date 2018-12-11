package uk.ac.ebi.uniprot.disease.cli.omim;

import uk.ac.ebi.uniprot.disease.cli.common.DiseaseDataLoaderArgs;
import uk.ac.ebi.uniprot.disease.cli.common.MainHelper;
import uk.ac.ebi.uniprot.disease.pipeline.processor.omim.PhenotypicSeriesSaver;
import uk.ac.ebi.uniprot.disease.service.tsv.TSVReader;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class PhenotypicSeriesLoader {
    private static final Integer BATCH_SIZE = 200;

    public static void main(String[] args) throws IOException, SQLException {
        // load the db connection info
        DiseaseDataLoaderArgs options = new DiseaseDataLoaderArgs();
        MainHelper.fillDBParams(options, MainHelper.DEFAULT_DB_CONNECTION_PROP);

        // parse the file
        TSVReader reader = new TSVReader("src/main/data.omim/phenotypicSeries.txt");

        // save the data
        storeData(reader, options.getDbUser(), options.getDbPassword(), options.getJdbcUrl());

    }

    private static void storeData(TSVReader reader, String dbUser, String dbPass, String jdbcUrl) throws SQLException {
        PhenotypicSeriesSaver saver = new PhenotypicSeriesSaver(dbUser, dbPass, jdbcUrl);

        for(List<List<String>> records = reader.getRecords(BATCH_SIZE);records.size() > 0; records = reader.getRecords(BATCH_SIZE)) {
            saver.persistRecords(records);
        }
    }

}
