/*
 * Created by sahmad on 1/17/19 11:32 AM
 * UniProt Consortium.
 * Copyright (c) 2002-2019.
 *
 */

package uk.ac.ebi.diseaseservice.processor;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import uk.ac.ebi.diseaseservice.common.StepScopeTestConfig;
import uk.ac.ebi.diseaseservice.config.TestConfiguration;
import uk.ac.ebi.uniprot.disease.model.Disease;
import uk.ac.ebi.uniprot.disease.model.sources.uniprot.UniProtDisease;

@SpringBootTest(classes = TestConfiguration.class)
@SpringBatchTest
@RunWith(SpringRunner.class)
@ContextConfiguration
public class UniProtDiseaseConverterTest extends StepScopeTestConfig {
    @Autowired
    UniProtDiseaseConverter uniProtDiseaseConverter;
    @Autowired
    private ItemReader<UniProtDisease> reader;

    @Test
    public void testProcess() throws Exception {
        UniProtDisease upDisease = reader.read();
        Assert.assertNotNull("UniProtDisease is null", upDisease);
        Disease disease = uniProtDiseaseConverter.process(upDisease);
        Assert.assertNotNull("Disease is null", disease);
        Assert.assertEquals("Name not equal", upDisease.getIdentifier(), disease.getName());
        Assert.assertEquals("Acronym not equal", upDisease.getAcronym(), disease.getAcronym());
        Assert.assertEquals("Description not equal", upDisease.getDefinition(), disease.getDescription());
    }
}
