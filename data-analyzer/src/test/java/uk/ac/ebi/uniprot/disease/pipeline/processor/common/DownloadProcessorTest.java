package uk.ac.ebi.uniprot.disease.pipeline.processor.common;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import uk.ac.ebi.uniprot.disease.pipeline.processor.common.Cleaner;
import uk.ac.ebi.uniprot.disease.pipeline.processor.common.DownloadProcessor;
import uk.ac.ebi.uniprot.disease.pipeline.request.DiseaseRequest;
import uk.ac.ebi.uniprot.disease.pipeline.request.WorkflowMetrics;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

public class DownloadProcessorTest {
    private String url = "http://www.disgenet.org/ds/DisGeNET/results/curated_gene_disease_associations.tsv.gz";
    private String downloadedFile = "/tmp/cgda.tsv.gz";
    private String unzippedFile = "/tmp/cgda.tsv";
    @Ignore
    public void testProcessRequest() throws IOException, SQLException {
        DownloadProcessor processor = new DownloadProcessor();
        WorkflowMetrics metrics = new WorkflowMetrics(System.currentTimeMillis());
        DiseaseRequest.DiseaseRequestBuilder builder = DiseaseRequest.builder();
        builder.url(url);
        builder.downloadedFilePath(downloadedFile);
        builder.uncompressedFilePath(unzippedFile);
        DiseaseRequest request = builder.workflowMetrics(metrics).build();
        long startTime = System.currentTimeMillis();
        processor.processRequest(request);

        // check if the file is downloaded
        File file = new File(downloadedFile);
        Assert.assertTrue("The file is not downloaded", file.exists());
        long fileModifiedTime = file.lastModified();
        long tenMinutesInMillis = 10*60*1000L; // Assumption the file should have downloaded in 10 minutes
        Assert.assertTrue("The file timestamp is older than 10 minutes, was the file downloaded?",
                (fileModifiedTime-startTime) < tenMinutesInMillis);
        // check if the file was uncompressed
        File uFile = new File(unzippedFile);
        Assert.assertTrue("The file was not uncompressed", uFile.exists());
        long lastModTime = uFile.lastModified();
        Assert.assertTrue("The file timestamp is older than 10 minutes, was the file uncompressed?",
                (lastModTime-startTime) < tenMinutesInMillis);
        Assert.assertTrue("Something is wrong with uncompressing. The uncompressed file should be larger than the compressed one",
                uFile.length() > file.length());

        // clean up, delete the compressed and uncompressed file
        Cleaner cleaner = new Cleaner();
        cleaner.processRequest(request);
    }
}
