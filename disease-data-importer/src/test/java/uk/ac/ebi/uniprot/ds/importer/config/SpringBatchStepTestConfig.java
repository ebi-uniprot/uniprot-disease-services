package uk.ac.ebi.uniprot.ds.importer.config;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author sahmad
 * @created 13/10/2020
 */
@Configuration
@EnableBatchProcessing
public class SpringBatchStepTestConfig {
    @Bean
    JobLauncherTestUtils jobLauncherTestUtils() {
        return new JobLauncherTestUtils();
    }
}
