package uk.ac.ebi.uniprot.disease.cli.gda;

import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

public class GDADataLoaderTest {

    @Test
    public void testGDADataLoader(){
        // TODO we can improve this test once we have added the DB
        // the test should behave like this -->
        // download a sample file and insert into db, fetch data and verify if the workflow has inserted the data or not
        // then delete the test data from db
        // Do the same for VDADataLoaderTest
        try {
            GDADataLoader.main(new String[0]);
        } catch (IOException e) {
            Assert.assertTrue("The GDADataloader call has failed. See the stacktrace below", false);
            e.printStackTrace();
        }
    }

}
