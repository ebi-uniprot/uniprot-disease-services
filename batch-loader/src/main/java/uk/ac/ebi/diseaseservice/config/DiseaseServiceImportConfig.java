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
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.data.MongoItemWriter;
import org.springframework.batch.item.support.CompositeItemWriter;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.mongodb.core.MongoTemplate;
import uk.ac.ebi.diseaseservice.model.Protein;
import uk.ac.ebi.diseaseservice.processor.UniProtDiseaseConverter;
import uk.ac.ebi.diseaseservice.reader.SwissProtReader;
import uk.ac.ebi.diseaseservice.writer.*;
import uk.ac.ebi.diseaseservice.listener.LogJobListener;
import uk.ac.ebi.diseaseservice.listener.LogStepListener;
import uk.ac.ebi.diseaseservice.reader.HumDiseaseReader;
import uk.ac.ebi.diseaseservice.util.Constants;
import uk.ac.ebi.kraken.interfaces.uniprot.UniProtEntry;
import uk.ac.ebi.uniprot.disease.model.Disease;
import uk.ac.ebi.uniprot.disease.model.sources.uniprot.UniProtDisease;

import javax.sql.DataSource;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

    @Value("${ds.mongodb.reset}")
    private boolean resetDB;

    @Bean
    public Job getDiseaseServiceDataLoadJob() throws FileNotFoundException {
        return this.jobBuilderFactory.get(Constants.DISEASE_SERVICE_DATA_LOADER)
                .flow(cleanDB())
                //.next(importHumDiseaseData())
                .next(importSwissProtData())
                .end()
                .listener(getLogJobListener())
                .build();
    }

    @Bean
    public Step cleanDB() {
        return this.stepBuilderFactory.get(Constants.DB_CLEAN_STEP)
                .tasklet(dbCleanTasklet())
                .build();
    }

    private Tasklet dbCleanTasklet() {
        Tasklet tasklet = (contribution, chunkContext) -> {
            if(resetDB){
                logger.info("Deleting the collection {}", diseaseCollectionName);
                mongoTemplate.dropCollection("protein");
                mongoTemplate.dropCollection("interaction");
                mongoTemplate.dropCollection("pathway");
                mongoTemplate.dropCollection("variant");
                mongoTemplate.dropCollection(diseaseCollectionName);
            }
            return RepeatStatus.FINISHED;
        };

        return tasklet;
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
    public Step importSwissProtData() throws FileNotFoundException {
        return this.stepBuilderFactory.get("SWISS_PROT_DATA_LOAD_STEP")
                .<UniProtEntry, UniProtEntry>chunk(chunkSize)
                .reader(getSwissProtReader())
                .writer(compositeItemWriter())
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
    ItemReader<UniProtEntry> getSwissProtReader() throws FileNotFoundException {
        ItemReader<UniProtEntry> reader = new SwissProtReader("/Users/sahmad/Documents/uniprot_sprot.dat");
        return reader;
    }

    @Bean
    ItemWriter<UniProtDisease> getDummyWriter() {
        return new NoOpItemWriter();
    }


    @Bean
    public MongoItemWriter<Disease> getMongoItemWriter() {
        MongoItemWriter<Disease> mongoItemWriter = new MongoItemWriter<>();
        mongoItemWriter.setCollection(this.diseaseCollectionName);
        mongoItemWriter.setTemplate(mongoTemplate);
        return mongoItemWriter;
    }

    @Bean
    public CompositeItemWriter<UniProtEntry> compositeItemWriter(){
        CompositeItemWriter compositeWriter = new CompositeItemWriter();
        ProteinWriter writer1 = getProteinWriter();
        DiseaseWriter writer2 = getDiseaseWriter();
        InteractionWriter writer3 = getInteractionWriter();
        VariantWriter writer4 = getVariantWriter();
        PathwayWriter writer5 = getPathwayWriter();
        DiseaseVariantWriter writer6 = getDiseaseVariantWriter();
        DiseaseInteractionWriter writer7 = getDiseaseInteractionWriter();
        List<ItemWriter> writers = new ArrayList<>();
        writers.add(writer1);
        writers.add(writer2);
        writers.add(writer3);
        writers.add(writer4);
        writers.add(writer5);
        writers.add(writer6);
        writers.add(writer7);

        compositeWriter.setDelegates(writers);
        return compositeWriter;
    }

    @Bean
    public ProteinWriter getProteinWriter(){
        ProteinWriter proteinWriter = new ProteinWriter("protein");
        proteinWriter.setTemplate(mongoTemplate);
        return proteinWriter;
    }


    @Bean
    public DiseaseWriter getDiseaseWriter(){
        DiseaseWriter diseaseWriter = new DiseaseWriter("disease");
        diseaseWriter.setTemplate(mongoTemplate);
        return diseaseWriter;
    }

    @Bean
    public InteractionWriter getInteractionWriter(){
        InteractionWriter writer = new InteractionWriter();
        writer.setTemplate(mongoTemplate);
        return writer;

    }

    @Bean
    public VariantWriter getVariantWriter(){
        VariantWriter writer = new VariantWriter();
        writer.setTemplate(mongoTemplate);
        return writer;
    }

    @Bean
    public PathwayWriter getPathwayWriter(){
        PathwayWriter writer = new PathwayWriter();
        writer.setTemplate(mongoTemplate);
        return writer;
    }

    @Bean
    public DiseaseInteractionWriter getDiseaseInteractionWriter() {
        DiseaseInteractionWriter writer = new DiseaseInteractionWriter();
        writer.setTemplate(mongoTemplate);
        return writer;
    }

    @Bean
    public DiseaseVariantWriter getDiseaseVariantWriter(){
        DiseaseVariantWriter writer = new DiseaseVariantWriter();
        writer.setTemplate(mongoTemplate);
        return writer;
    }
}
