package uk.ac.ebi.uniprot.disease.pipeline.processor.gda;

import org.junit.Assert;
import org.junit.Test;
import uk.ac.ebi.uniprot.disease.model.sources.disgenet.DataTypes;
import uk.ac.ebi.uniprot.disease.pipeline.processor.gda.GDAFileParser;
import uk.ac.ebi.uniprot.disease.pipeline.request.DiseaseRequest;
import uk.ac.ebi.uniprot.disease.pipeline.request.WorkflowMetrics;

import java.io.IOException;
import java.sql.SQLException;

public class GDAFileParserTest {
    private String dataFile = "src/test/resources/sample_gene_disease_association.tsv";

    @Test
    public void testProcessNext() throws IOException, SQLException {
        WorkflowMetrics metrics = new WorkflowMetrics(System.currentTimeMillis());
        DiseaseRequest request = DiseaseRequest.builder().uncompressedFilePath(dataFile)
                .batchSize(200).workflowMetrics(metrics).dataType(DataTypes.gda).build();
        GDAFileParser fileParser = new GDAFileParser();
        Assert.assertNull("The parsed records not null", request.getParsedGDARecords());
        fileParser.processRequest(request);
        Assert.assertNotNull("The parsed records null", request.getParsedGDARecords());
        Assert.assertEquals("The processor name is not equal", "GDAFileParser", fileParser.getProcessorName());

    }
}
