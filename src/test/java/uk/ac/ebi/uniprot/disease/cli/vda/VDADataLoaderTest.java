package uk.ac.ebi.uniprot.disease.cli.vda;

import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

public class VDADataLoaderTest {
    @Test
    public void testVDADataLoader(){
        try {
            VDADataLoader.main(new String[0]);
        } catch (IOException e) {
            Assert.assertTrue("The VDADataloader call has failed. See the stacktrace below", false);
            e.printStackTrace();
        }
    }
}
