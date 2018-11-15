package uk.ac.ebi.uniprot.disease.pipeline.processor;

import org.junit.Assert;
import org.junit.Test;
import uk.ac.ebi.uniprot.disease.pipeline.request.DiseaseRequest;

import java.io.IOException;

public class FileParserTest {
    private String dataFile = "src/test/resources/sample_gene_disease_association.tsv";

    @Test
    public void testProcessNext() throws IOException {
        DiseaseRequest request = DiseaseRequest.builder().uncompressedFilePath(dataFile).batchSize(200).build();
        FileParser fileParser = new FileParser();
        Assert.assertNull("The parsed records not null", request.getParsedRecords());
        fileParser.processRequest(request);
        Assert.assertNotNull("The parsed records null", request.getParsedRecords());
        Assert.assertEquals("The processor name is not equal", "FileParser", fileParser.getProcessorName());

    }
}
