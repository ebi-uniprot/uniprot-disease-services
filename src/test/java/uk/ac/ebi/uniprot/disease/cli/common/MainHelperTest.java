package uk.ac.ebi.uniprot.disease.cli.common;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import uk.ac.ebi.uniprot.disease.cli.gda.GDADataLoader;
import uk.ac.ebi.uniprot.disease.pipeline.request.DiseaseRequest;

import java.io.IOException;

public class MainHelperTest {
    @Test
    public void testFillDefaultParams() {
        DiseaseDataLoaderArgs args = new DiseaseDataLoaderArgs();
        try {
            // the value of params before calling fillDefaultParams
            Assert.assertNull("The url is not null", args.getUrl());
            Assert.assertNull("The downloaded file path is not null", args.getDownloadedFilePath());
            Assert.assertNull("The uncompressed file path is not null", args.getUncompressedFilePath());

            MainHelper.fillDefaultParams(args, GDADataLoader.DEFAULT_GDA_CONFIG_LOCATION);

            // the value of params after calling fillDefaultParams
            Assert.assertNotNull("The url is null", args.getUrl());
            Assert.assertNotNull("The downloaded file path is null", args.getDownloadedFilePath());
            Assert.assertNotNull("The uncompressed file path is null", args.getUncompressedFilePath());

        } catch (IOException e) {
            Assert.assertTrue("Unable to fill the default params from properties file", false);
        }
    }

    @Test
    public void testGetDiseaseRequest(){
        DiseaseDataLoaderArgs args = new DiseaseDataLoaderArgs();
        DiseaseRequest request = MainHelper.getDiseaseRequest(args, System.currentTimeMillis());
        Assert.assertNotNull("The request object is null", request);
        Assert.assertNotNull("isDownload is null", request.isDownload());
        Assert.assertTrue("isDownload is not true", request.isDownload());
        Assert.assertNotNull("batchsize is null", request.getBatchSize());
        Assert.assertEquals("batchsize is not as expected", 200, request.getBatchSize());
        Assert.assertNotNull("workflow metrics is null", request.getWorkflowMetrics());
    }

    @Test
    public void testParseCommandLineArgs(){
        String[] args = {"--url", "http://www.google.com", "-h", "true", "-b", "300"};
        DiseaseDataLoaderArgs options = new DiseaseDataLoaderArgs();
        JCommander commander = MainHelper.parseCommandLineArgs(options, args);
        Assert.assertNotNull("Unable to parse the args", commander);
        Assert.assertNotNull("url is null", options.getUrl());
        Assert.assertEquals("url is not equal", "http://www.google.com", options.getUrl());
        Assert.assertTrue("Help is not set", options.isHelp());
        Assert.assertEquals("Batch size is not updated", Integer.valueOf(300), options.getBatchSize());
    }

    @Test(expected = ParameterException.class)
    public void testParseCommandLineArgsWithWrongParam() {
        String[] args = {"--urlrandom", "http://www.google.com", "-h", "true", "-b", "300"};
        DiseaseDataLoaderArgs options = new DiseaseDataLoaderArgs();
        JCommander commander = MainHelper.parseCommandLineArgs(options, args);
        Assert.assertTrue("This should not execute", false);
    }
}
