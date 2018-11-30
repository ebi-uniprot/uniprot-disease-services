package uk.ac.ebi.uniprot.disease.cli.vda;

import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.sql.SQLException;

public class VDADataLoaderTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(VDADataLoaderTest.class);

    @Test
    public void testVDADataLoader() throws SQLException {
        try {
            String[] args = {"--store", "false", "--type", "vda"};
            VDADataLoader.main(args);
        } catch (IOException e) {
            Assert.assertTrue("The VDADataloader call has failed. See the stacktrace below", false);
            LOGGER.debug("Error while calling the GDADataLoader workflow", e);
        }
    }

    @Test
    public void testVDADataLoaderHelp() throws SQLException {
        String[] args = {"-h", "true"};
        try {
            VDADataLoader.main(args);
        } catch (IOException ie){
            Assert.assertTrue("The VDADataloader call has failed. See the stacktrace below", false);
            LOGGER.debug("Error while calling the VDADataLoader help", ie);
        }
    }
}
