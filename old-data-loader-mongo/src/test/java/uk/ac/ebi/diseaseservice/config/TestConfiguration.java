/*
 * Created by sahmad on 1/17/19 9:04 AM
 * UniProt Consortium.
 * Copyright (c) 2002-2019.
 *
 */

package uk.ac.ebi.diseaseservice.config;

import com.mongodb.MongoClient;
import cz.jirutka.spring.embedmongo.EmbeddedMongoFactoryBean;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.batch.BatchAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.embedded.EmbeddedMongoAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.TestPropertySource;
import java.io.IOException;

@Configuration
@Import(DiseaseServiceImportConfig.class)
@EnableBatchProcessing
@TestPropertySource(locations = "classpath:application.properties")
@EnableAutoConfiguration(exclude = {EmbeddedMongoAutoConfiguration.class, BatchAutoConfiguration.class})
public class TestConfiguration {
    public static final int READ_WRITE_COUNT = 5184;
    @Autowired
    private MongoDbFactory mongoDbFactory;

    @Bean
    public JobLauncherTestUtils utils() throws Exception {
        return new JobLauncherTestUtils();
    }

    @Bean
    MongoTemplate mongoTemplate() throws IOException {
        EmbeddedMongoFactoryBean mongo = new EmbeddedMongoFactoryBean();
        mongo.setBindIp("localhost");
        MongoClient mongoClient = mongo.getObject();
        return new MongoTemplate(mongoClient, "disease-service");
    }
}
