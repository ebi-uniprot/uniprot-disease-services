package uk.ac.ebi.uniprot.disease.cli.gda;

import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.sql.SQLException;

public class GDADataLoaderTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(GDADataLoaderTest.class);

    @Test
    public void testGDADataLoader() throws SQLException {
        try {
            String[] args = {"--store", "false", "-t", "gda"};
            GDADataLoader.main(args);
        } catch (IOException e) {
            Assert.assertTrue("The GDADataloader call has failed. See the stacktrace below", false);
            LOGGER.debug("Error while calling the GDADataLoader workflow", e);
        }
    }

    @Test
    public void testGDADataLoaderHelp() throws SQLException {
        String[] args = {"-h", "true"};
        try {
            GDADataLoader.main(args);
        } catch (IOException ie){
            Assert.assertTrue("The GDADataloader call has failed. See the stacktrace below", false);
            LOGGER.debug("Error while calling the GDADataLoader help", ie);
        }
    }

}
