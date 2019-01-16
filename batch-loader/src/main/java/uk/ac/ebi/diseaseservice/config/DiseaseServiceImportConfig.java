/*
 * Created by sahmad on 15/01/19 14:23
 * UniProt Consortium.
 * Copyright (c) 2002-2019.
 *
 */

package uk.ac.ebi.diseaseservice.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.DefaultBatchConfigurer;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.data.MongoItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.mongodb.core.MongoTemplate;
import uk.ac.ebi.diseaseservice.processor.UniProtDiseaseConverter;
import uk.ac.ebi.diseaseservice.writer.NoOpItemWriter;
import uk.ac.ebi.diseaseservice.listener.LogJobListener;
import uk.ac.ebi.diseaseservice.listener.LogStepListener;
import uk.ac.ebi.diseaseservice.reader.HumDiseaseReader;
import uk.ac.ebi.diseaseservice.util.Constants;
import uk.ac.ebi.uniprot.disease.model.Disease;
import uk.ac.ebi.uniprot.disease.model.sources.uniprot.UniProtDisease;

import javax.sql.DataSource;
import java.io.FileNotFoundException;

@Configuration
@EnableBatchProcessing
@PropertySource("classpath:application.properties")
public class DiseaseServiceImportConfig extends DefaultBatchConfigurer {
    @Override
    public void setDataSource(DataSource dataSource) {
    }

    private static final Logger logger = LoggerFactory.getLogger(DiseaseServiceImportConfig.class);

    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Value("${ds.input.humdisease.path}")
    private String humDiseaseFile;

    @Value("${ds.mongodb.collection.disease}")
    private String diseaseCollectionName;

    @Value("${ds.input.humdisease.chunk.size}")
    private Integer chunkSize;

    @Bean
    public Job getDiseaseServiceDataLoadJob() throws FileNotFoundException {
        return this.jobBuilderFactory.get(Constants.DISEASE_SERVICE_DATA_LOADER)
                .start(importHumDiseaseData())
                .listener(getLogJobListener())
                .build();
    }

    @Bean
    public Step importHumDiseaseData() throws FileNotFoundException {
        return this.stepBuilderFactory.get(Constants.DS_HUM_DISEASE_DATA_LOADER_STEP)
                .<UniProtDisease, Disease>chunk(chunkSize)
                .reader(getHumDiseaseReader())
                .processor(getConverter())
                .writer(getMongoItemWriter())
                .listener(getLogStepListener())
                .build();
    }

    @Bean
    public UniProtDiseaseConverter getConverter() {
        return new UniProtDiseaseConverter();
    }

    @Bean
    public JobExecutionListener getLogJobListener() {
        return new LogJobListener();

    }

    @Bean
    public LogStepListener getLogStepListener(){
        return new LogStepListener();
    }

    @Bean
    ItemReader<UniProtDisease> getHumDiseaseReader() throws FileNotFoundException {
        ItemReader<UniProtDisease> reader = new HumDiseaseReader(this.humDiseaseFile);
        return reader;
    }

    @Bean
    ItemWriter<UniProtDisease> getDummyWriter() {
        return new NoOpItemWriter();
    }


    @Bean
    public MongoItemWriter<Disease> getMongoItemWriter() {
        mongoTemplate.dropCollection(this.diseaseCollectionName);// TODO make it another conditional step e.g tasklet
        MongoItemWriter<Disease> mongoItemWriter = new MongoItemWriter<>();
        mongoItemWriter.setCollection(this.diseaseCollectionName);
        mongoItemWriter.setTemplate(mongoTemplate);
        return mongoItemWriter;
    }


}
