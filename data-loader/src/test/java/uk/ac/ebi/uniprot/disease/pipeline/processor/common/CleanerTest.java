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

public class CleanerTest {
    private String url = "http://www.disgenet.org/ds/DisGeNET/results/curated_gene_disease_associations.tsv.gz";
    private String downloadedFile = "/tmp/cgda.tsv.gz";
    private String unzippedFile = "/tmp/cgda.tsv";

    @Ignore
    public void testProcessNext() throws IOException, SQLException {
        WorkflowMetrics metrics = new WorkflowMetrics(System.currentTimeMillis());
        DiseaseRequest.DiseaseRequestBuilder builder = DiseaseRequest.builder();
        builder.url(url);
        builder.downloadedFilePath(downloadedFile);
        builder.uncompressedFilePath(unzippedFile);
        DiseaseRequest request = builder.workflowMetrics(metrics).build();

        // create data for cleaner
        setUpData(request);

        Cleaner cleaner = new Cleaner();
        cleaner.processRequest(request);
        // check if the files are actually deleted
        File file = new File(downloadedFile);
        Assert.assertFalse("The downloaded file is not deleted", file.exists());
        File file1 = new File(unzippedFile);
        Assert.assertFalse("The unzipped file is not deleted", file1.exists());
    }

    private void setUpData(DiseaseRequest request) throws IOException, SQLException {
        DownloadProcessor processor = new DownloadProcessor();
        processor.processRequest(request);
    }
}
