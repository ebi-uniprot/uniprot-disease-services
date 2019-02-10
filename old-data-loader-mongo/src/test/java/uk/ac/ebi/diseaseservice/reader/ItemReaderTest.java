/*
 * Created by sahmad on 1/17/19 11:01 AM
 * UniProt Consortium.
 * Copyright (c) 2002-2019.
 *
 */

package uk.ac.ebi.diseaseservice.reader;

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
import uk.ac.ebi.uniprot.disease.model.sources.uniprot.UniProtDisease;

@SpringBootTest(classes = TestConfiguration.class)
@SpringBatchTest
@RunWith(SpringRunner.class)
@ContextConfiguration
public class ItemReaderTest extends StepScopeTestConfig {
    @Autowired
    private ItemReader<UniProtDisease> reader;

    @Test
    public void testReader() throws Exception {
        // The reader is initialized and bound to the input data
        Assert.assertNotNull(reader.read());
    }


}