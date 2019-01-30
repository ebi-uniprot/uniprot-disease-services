/*
 * Created by sahmad on 29/01/19 11:28
 * UniProt Consortium.
 * Copyright (c) 2002-2019.
 *
 */

package uk.ac.ebi.uniprot.ds.config;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.support.CompositeItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import uk.ac.ebi.kraken.interfaces.uniprot.UniProtEntry;
import uk.ac.ebi.uniprot.ds.listener.LogJobListener;
import uk.ac.ebi.uniprot.ds.listener.LogStepListener;
import uk.ac.ebi.uniprot.ds.model.Protein;
import uk.ac.ebi.uniprot.ds.reader.UniProtReader;
import uk.ac.ebi.uniprot.ds.writer.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration
@EnableBatchProcessing
public class BatchConfigurationUniProtData {
    private Map<String, Protein> proteinIdProteinMap = new HashMap<>();

    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    // tag::jobstep[]
    @Bean
    public Job importUniProtDataJob(){
        return jobBuilderFactory.get("importUniProtData")
                .incrementer(new RunIdIncrementer())
                .flow(importUniProtDataStep())
                .end()
                .listener(logJobListener())
                .build();
    }

    @Bean
    public Step importUniProtDataStep() {
        return stepBuilderFactory.get("importUniProtDataStep")
                .<UniProtEntry, UniProtEntry>chunk(50)
                .reader(uniprotReader())
                .writer(compositeItemWriter())
                .listener(logStepListener())
                .build();
    }

    @Bean
    public ProteinWriter proteinWriter() {
        return new ProteinWriter(proteinIdProteinMap);
    }

    @Bean
    public CompositeItemWriter<UniProtEntry> compositeItemWriter(){
        CompositeItemWriter compositeWriter = new CompositeItemWriter();
        ProteinWriter writer1 = proteinWriter();
        InteractionWriter writer2 = interactionWriter();
        PathwayWriter writer3 = pathwayWriter();
        VariantWriter writer4 = variantWriter();
        DiseaseWriter writer5 = diseaseWriter();
        List<ItemWriter> writers = new ArrayList<>();
        writers.add(writer1);
        writers.add(writer2);
        writers.add(writer3);
        writers.add(writer4);
        writers.add(writer5);
        compositeWriter.setDelegates(writers);
        return compositeWriter;
    }

    @Bean
    public VariantWriter variantWriter() {
        return new VariantWriter(proteinIdProteinMap);
    }

    @Bean
    public InteractionWriter interactionWriter() {
        return new InteractionWriter(proteinIdProteinMap);
    }

    @Bean
    public DiseaseWriter diseaseWriter() {
        return new DiseaseWriter(proteinIdProteinMap);
    }

    @Bean
    public PathwayWriter pathwayWriter() {
        return new PathwayWriter(proteinIdProteinMap);
    }

    @Bean
    public ItemReader<? extends UniProtEntry> uniprotReader() {
        UniProtReader reader = new UniProtReader("/Users/sahmad/Documents/uniprot_sprot.dat");
        return reader;
    }

    @Bean
    public JobExecutionListener logJobListener() {
        return new LogJobListener();

    }

    @Bean
    public LogStepListener logStepListener(){
        return new LogStepListener();
    }
}
