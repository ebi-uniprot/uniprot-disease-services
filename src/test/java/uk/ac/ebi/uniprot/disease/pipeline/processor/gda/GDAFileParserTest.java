package uk.ac.ebi.uniprot.disease.pipeline.processor.gda;

import org.junit.Assert;
import org.junit.Test;
import uk.ac.ebi.uniprot.disease.pipeline.request.DiseaseRequest;

import java.io.IOException;

public class GDAFileParserTest {
    private String dataFile = "src/test/resources/sample_gene_disease_association.tsv";

    @Test
    public void testProcessNext() throws IOException {
        DiseaseRequest request = DiseaseRequest.builder().uncompressedFilePath(dataFile).batchSize(200).build();
        GDAFileParser fileParser = new GDAFileParser();
        Assert.assertNull("The parsed records not null", request.getParsedGDARecords());
        fileParser.processRequest(request);
        Assert.assertNotNull("The parsed records null", request.getParsedGDARecords());
        Assert.assertEquals("The processor name is not equal", "GDAFileParser", fileParser.getProcessorName());

    }
}
