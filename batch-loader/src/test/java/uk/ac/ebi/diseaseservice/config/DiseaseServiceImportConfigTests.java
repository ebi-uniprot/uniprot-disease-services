/*
 * Created by sahmad on 1/16/19 1:50 PM
 * UniProt Consortium.
 * Copyright (c) 2002-2019.
 *
 */

package uk.ac.ebi.diseaseservice.config;

import com.mongodb.MongoClient;
import cz.jirutka.spring.embedmongo.EmbeddedMongoFactoryBean;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.batch.BatchAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.embedded.EmbeddedMongoAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;


@SpringBootTest(classes = DiseaseServiceImportConfigTests.TestConfiguration.class)
@SpringBatchTest
@RunWith(SpringRunner.class)
public class DiseaseServiceImportConfigTests {

    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;

    @Configuration
    @Import(DiseaseServiceImportConfig.class)
    @ComponentScan(basePackages = {"uk.ac.ebi.diseaseservice"})
    @EnableBatchProcessing
    @EnableAutoConfiguration(exclude = {EmbeddedMongoAutoConfiguration.class, BatchAutoConfiguration.class})
    static class TestConfiguration {

        @Autowired
        private MongoDbFactory mongoDbFactory;

        @Bean
        MongoTemplate mongoTemplate() throws IOException {
            EmbeddedMongoFactoryBean mongo = new EmbeddedMongoFactoryBean();
            mongo.setBindIp("localhost");
            MongoClient mongoClient = mongo.getObject();
            return new MongoTemplate(mongoClient, "disease-service");
        }

    }

    @Test
    public void testJob() throws Exception {
        JobExecution jobExecution = jobLauncherTestUtils.launchJob();
        Assert.assertEquals("COMPLETED", jobExecution.getExitStatus().getExitCode());
    }
}
