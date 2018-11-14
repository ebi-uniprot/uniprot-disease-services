package uk.ac.ebi.uniprot.disease.service.tsv;

import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import uk.ac.ebi.uniprot.disease.service.FileDownloader;

import java.io.File;
import java.io.IOException;

public class FileDownloaderTest {
    private String fileUrl = "https://dlptest.com/sample-data.csv";
    private String fileName = "sample-data.csv";


    @Test
    public void testDownload() throws IOException {
        FileDownloader.download(fileUrl, fileName);
        // verify now..
        File downloadedFile = new File(fileName);
        Assert.assertEquals("File is not downloaded", true, downloadedFile.exists());
        // delete the file
        Assert.assertEquals("Unable to delete the downloaded file", true, downloadedFile.delete());
    }
}
