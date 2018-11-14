package uk.ac.ebi.uniprot.disease.service.tsv;

import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

/**
 * @author sahmad
 */

public class TSVReaderTest {

    @Test
    public void testGetRecords() throws IOException {
        try(TSVReader tsvReader = new TSVReader("src/test/resources/sample_gene_disease_association.tsv")) {
            int recordCount = 0;
            while (tsvReader.hasMoreRecord()) {
                List<String> record = tsvReader.getRecord();
                verifyRecord(record);
                recordCount++;
            }
            Assert.assertEquals("Record count is not equal", 20, recordCount);
        }
    }

    private void verifyRecord(List<String> record){
        Assert.assertNotNull("Record is null", record);
        Assert.assertEquals("Length of record is not correct", 8, record.size());
    }
}
