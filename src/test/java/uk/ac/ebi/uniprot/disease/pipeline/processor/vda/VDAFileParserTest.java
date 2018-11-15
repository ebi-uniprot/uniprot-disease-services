package uk.ac.ebi.uniprot.disease.pipeline.processor.vda;

import org.junit.Assert;
import org.junit.Test;
import uk.ac.ebi.uniprot.disease.pipeline.processor.gda.GDAFileParser;
import uk.ac.ebi.uniprot.disease.pipeline.request.DiseaseRequest;

import java.io.IOException;

public class VDAFileParserTest {
    private String dataFile = "src/test/resources/sample_variant_disease_association.tsv";

    @Test
    public void testProcessNext() throws IOException {
        DiseaseRequest request = DiseaseRequest.builder().uncompressedFilePath(dataFile).batchSize(200).build();
        VDAFileParser fileParser = new VDAFileParser();
        Assert.assertNull("The parsed records not null", request.getParsedVDARecords());
        fileParser.processRequest(request);
        Assert.assertNotNull("The parsed records null", request.getParsedVDARecords());
        Assert.assertEquals("The processor name is not equal", "VDAFileParser", fileParser.getProcessorName());

    }
}
