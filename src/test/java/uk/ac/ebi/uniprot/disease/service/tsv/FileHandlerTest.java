package uk.ac.ebi.uniprot.disease.service.tsv;

import org.junit.Assert;
import org.junit.Test;
import uk.ac.ebi.uniprot.disease.service.FileHandler;

import java.io.File;
import java.io.IOException;

public class FileHandlerTest {

    private String inputFile = "src/test/resources/sample_gene_disease_association.tsv";

    @Test
    public void testCompressFile() throws IOException {
        String outputFile = "src/test/resources/sample_gene_disease_association.tsv.gz";
        FileHandler.compressToGZFile(this.inputFile, outputFile);
        // check if the file is created
        File compressedFile = new File(outputFile);
        Assert.assertTrue("Compressed file is not created", compressedFile.exists());
        // check if the size of the compressed file is less than the uncompressed file
        File uncompressedFile = new File(this.inputFile);
        Assert.assertTrue("The input file does not exist", uncompressedFile.exists());
        Assert.assertTrue("The size of the compressed file is not less than uncompressed file", compressedFile.length() < uncompressedFile.length());
        // delete the compressed file
        Assert.assertTrue("Unable to delete the compressed file", compressedFile.delete());
    }

    @Test
    public void testUncompressFile() throws IOException {
        // create a compressed file
        String interFilePath = "src/test/resources/sample_gene_disease_association.tsv.gz";
        FileHandler.compressToGZFile(this.inputFile, interFilePath);
        File interFile = new File(interFilePath);
        Assert.assertTrue("Unable to compress the file", interFile.exists());

        // uncompress the interFile and verify
        String uncompressedFilePath = "src/test/resources/uncompressed_sample_gene_disease_association.tsv";
        FileHandler.uncompressGZFile(interFilePath, uncompressedFilePath);

        File uncompressedFile = new File(uncompressedFilePath);
        Assert.assertTrue("unable to uncompress the file", uncompressedFile.exists());
        Assert.assertTrue("The size of the uncompressed file is not greater than uncompressed file", interFile.length() < uncompressedFile.length());
        // delete the temp(intermediary) compressed file
        Assert.assertTrue("Unable to delete the compressed file", interFile.delete());
        // delete the uncompressed file
        Assert.assertTrue("Unable to delete the uncompressed file", uncompressedFile.delete());
    }

}
