/*
 * Created by sahmad on 15/01/19 14:22
 * UniProt Consortium.
 * Copyright (c) 2002-2019.
 *
 */

package uk.ac.ebi.diseaseservice.config;

import org.springframework.batch.support.transaction.ResourcelessTransactionManager;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.PlatformTransactionManager;

@Import(DiseaseServiceImportConfig.class)
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class ImportAppMain {
    public static void main(String[] args) {
        SpringApplication.run(ImportAppMain.class, args);
    }

    @Bean
    public PlatformTransactionManager transactionManager() {
        return new ResourcelessTransactionManager();
    }
}
