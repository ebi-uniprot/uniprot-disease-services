package uk.ac.ebi.uniprot.disease.cli.vda;

import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class VDADataLoaderTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(VDADataLoaderTest.class);

    @Test
    public void testVDADataLoader(){
        try {
            VDADataLoader.main(new String[0]);
        } catch (IOException e) {
            Assert.assertTrue("The VDADataloader call has failed. See the stacktrace below", false);
            LOGGER.debug("Error while calling the GDADataLoader workflow", e);
        }
    }
}
