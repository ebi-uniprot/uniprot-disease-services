package uk.ac.ebi.uniprot.disease.service.tsv;

import org.junit.Assert;
import org.junit.Test;
import uk.ac.ebi.uniprot.disease.model.disgenet.VariantDiseaseAssociation;

import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.List;

/**
 * @author sahmad
 */
public class VariantDiseaseParserTest {
    @Test
    public void testParseRecord() {
        // create a row to be parsed
        List<String> record = Arrays.asList("rs1000113", "C0010346", "Crohn Disease", "0.512513295376337", "1", "GWASCAT");
        VariantDiseaseParser parser = new VariantDiseaseParser(null);
        VariantDiseaseAssociation vda = parser.parseRecord(record);
        Assert.assertNotNull("Unable to parse the string array", vda);
        // verify the other fields
        Assert.assertEquals("SNP is not parsed corrected", record.get(0), vda.getSnpId());
        Assert.assertEquals("Disease Id is not parsed corrected", record.get(1), vda.getDiseaseId());
        Assert.assertEquals("Disease Name is not parsed corrected", record.get(2), vda.getDiseaseName());
        Assert.assertEquals("Score is not parsed corrected", Double.valueOf(record.get(3)), vda.getScore());
        Assert.assertEquals("PMIDs count is not parsed corrected", Integer.valueOf(record.get(4)), vda.getPmidCount());
        Assert.assertEquals("Source is not parsed corrected", record.get(5), vda.getSource());
    }

    @Test
    public void testParseRecords() throws FileNotFoundException {
        TSVReader tsvReader = new TSVReader("src/test/resources/sample_variant_disease_association.tsv");
        VariantDiseaseParser parser = new VariantDiseaseParser(tsvReader);
        List<VariantDiseaseAssociation> records = parser.parseRecords();
        Assert.assertTrue("records empty", !records.isEmpty());
        Assert.assertEquals("Records count invalid", 30, records.size());
        records.parallelStream().forEach(VariantDiseaseParserTest::verifyRecord);
    }

    private static void verifyRecord(VariantDiseaseAssociation vda) {
        Assert.assertNotNull("Disease Id is null", vda.getDiseaseId());
        Assert.assertNotNull("Disease name is null", vda.getDiseaseName());
        Assert.assertNotNull("PMID count is null", vda.getPmidCount());
        Assert.assertNotNull("Score is null", vda.getScore());
        Assert.assertNotNull("SNP Id is null", vda.getSnpId());
        Assert.assertNotNull("Source is null", vda.getSource());
    }
}
