/*
 * Created by sahmad on 29/01/19 11:28
 * UniProt Consortium.
 * Copyright (c) 2002-2019.
 *
 */

package uk.ac.ebi.uniprot.ds.importer.config;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import uk.ac.ebi.uniprot.ds.importer.listener.LogJobListener;
import uk.ac.ebi.uniprot.ds.importer.listener.LogStepListener;
import uk.ac.ebi.uniprot.ds.importer.util.Constants;

@Configuration
@EnableBatchProcessing
public class BatchConfigurationDiseaseService {
    @Bean
    public Job importUniProtDataJob(JobBuilderFactory jobBuilderFactory, JobExecutionListener jobExecutionListener,
                                    Step humDiseaseStep, Step uniProtStep) {//, Step enrichHumDiseaseStep

        return jobBuilderFactory.get(Constants.DISEASE_SERVICE_DATA_LOADER)
                .incrementer(new RunIdIncrementer())
                .start(humDiseaseStep)
                .next(uniProtStep)
                //.next(enrichHumDiseaseStep)// enrich the hum disease by DisGeNET data based on OMIM and MeSH mapping
                .listener(jobExecutionListener)
                .build();

    }


    @Bean
    public JobExecutionListener jobExecutionListener() {
        return new LogJobListener();

    }

    @Bean
    public StepExecutionListener stepListener(){
        return new LogStepListener();
    }
}
