package uk.ac.ebi.uniprot.disease.service.tsv;

import org.junit.Assert;
import org.junit.Test;
import uk.ac.ebi.uniprot.disease.model.DisGeNET.GeneDiseaseAssociation;

import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.List;

/**
 * @author sahmad
 */
public class GeneDiseaseParserTest {

    @Test
    public void testParseRecord() {
        // create a row to be parsed
        List<String> record = Arrays.asList("10", "NAT2", "C0005695", "Bladder Neoplasm", "0.245871429880008", "5", "0", "CTD_human");
        GeneDiseaseParser parser = new GeneDiseaseParser(null);
        GeneDiseaseAssociation gdAssociation = parser.parseRecord(record);
        Assert.assertNotNull("Unable to parse the string array", gdAssociation);
        // verify the other fields
        Assert.assertEquals("GeneId is not parsed corrected", record.get(0), gdAssociation.getGeneId());
        Assert.assertEquals("Gene Symbol is not parsed corrected", record.get(1), gdAssociation.getGeneSymbol());
        Assert.assertEquals("Disease Id is not parsed corrected", record.get(2), gdAssociation.getDiseaseId());
        Assert.assertEquals("Disease Name is not parsed corrected", record.get(3), gdAssociation.getDiseaseName());
        Assert.assertEquals("Score is not parsed corrected", Double.valueOf(record.get(4)), gdAssociation.getScore());
        Assert.assertEquals("PMIDs count is not parsed corrected", Integer.valueOf(record.get(5)), gdAssociation.getPmidCount());
        Assert.assertEquals("SNPs count is not parsed corrected", Integer.valueOf(record.get(6)), gdAssociation.getSnpCount());
        Assert.assertEquals("Source is not parsed corrected", record.get(7), gdAssociation.getSource());
    }

    @Test
    public void testParseRecords() throws FileNotFoundException {
        TSVReader tsvReader = new TSVReader("src/test/resources/sample_gene_disease_association.tsv");
        GeneDiseaseParser parser = new GeneDiseaseParser(tsvReader);
        List<GeneDiseaseAssociation> records = parser.parseRecords();
        Assert.assertTrue("records empty", !records.isEmpty());
        Assert.assertEquals("Records count invalid", 20, records.size());
    }
}
