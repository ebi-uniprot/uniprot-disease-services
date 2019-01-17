/*
 * Created by sahmad on 1/17/19 9:00 AM
 * UniProt Consortium.
 * Copyright (c) 2002-2019.
 *
 */

package uk.ac.ebi.diseaseservice.reader;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.batch.item.ItemReader;
import uk.ac.ebi.diseaseservice.config.TestConfiguration;
import uk.ac.ebi.uniprot.disease.model.sources.uniprot.UniProtDisease;

public class HumDiseaseReaderTest {
    private static final String HUM_DISEASE_FILE = "uniprot/humdisease.txt";

    @Test
    public void testReader() throws Exception {
        ItemReader<UniProtDisease> reader = new HumDiseaseReader(HUM_DISEASE_FILE);
        UniProtDisease disease = reader.read();
        verifyDisease(disease);
    }

    @Test
    public void testWholeFileRead() throws Exception {
        ItemReader<UniProtDisease> reader = new HumDiseaseReader(HUM_DISEASE_FILE);
        UniProtDisease disease;
        int count = 0;
        while((disease = reader.read()) != null){
            count++;
            verifyDisease(disease);
        }

        Assert.assertEquals("Processed disease count didn't match", TestConfiguration.READ_WRITE_COUNT, count);

    }

    private void verifyDisease(UniProtDisease disease) {
        Assert.assertNotNull("Unable to read the file", disease);
        Assert.assertNotNull("Identifier is null", disease.getIdentifier());
        Assert.assertNotNull("Accession is null", disease.getAccession());
        Assert.assertNotNull("Acronym is null", disease.getAcronym());
        Assert.assertNotNull("Definition is null", disease.getDefinition());
    }
}
