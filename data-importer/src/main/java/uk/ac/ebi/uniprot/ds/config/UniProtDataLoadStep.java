/*
 * Created by sahmad on 1/30/19 10:26 PM
 * UniProt Consortium.
 * Copyright (c) 2002-2019.
 *
 */

package uk.ac.ebi.uniprot.ds.config;

import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.support.CompositeItemProcessor;
import org.springframework.batch.item.support.CompositeItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import uk.ac.ebi.kraken.interfaces.uniprot.UniProtEntry;
import uk.ac.ebi.uniprot.ds.model.Protein;
import uk.ac.ebi.uniprot.ds.processor.*;
import uk.ac.ebi.uniprot.ds.reader.UniProtReader;
import uk.ac.ebi.uniprot.ds.util.Constants;
import uk.ac.ebi.uniprot.ds.writer.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration
public class UniProtDataLoadStep {
    Map<String, Protein> proteinIdProteinMap = new HashMap<>();
    private List<Protein> proteins = new ArrayList<>();

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Value(("${ds.import.chunk.size}"))
    private Integer chunkSize;

    @Value("${ds.uniprot.data.file.absolute.path}")
    private String uniProtDataAbsFilePath;


    @Bean
    public Step uniProtStep(StepExecutionListener stepListener,
                            ItemReader<UniProtEntry> uniProtReader, CompositeItemWriter<UniProtEntry> uniProtCompositeWriter) {
        return stepBuilderFactory.get(Constants.DS_UNIPROT_DATA_LOADER_STEP)
                .<UniProtEntry, UniProtEntry>chunk(chunkSize)
                .reader(uniProtReader)
                .processor(compositeItemProcessor())
                .writer(proteinWriter1())
                .listener(stepListener)
                .build();
    }

    @Bean
    public ItemReader<UniProtEntry> uniProtReader() {
        UniProtReader reader = new UniProtReader(uniProtDataAbsFilePath);
        return reader;
    }
    @Bean
    public CompositeItemProcessor<UniProtEntry, UniProtEntry> compositeItemProcessor(){
        CompositeItemProcessor<UniProtEntry, UniProtEntry> compositeProcessor = new CompositeItemProcessor<>();

        List itemProcessors = new ArrayList<>();
        itemProcessors.add(new ProteinProcessor(this.proteins));
        itemProcessors.add(new PathwayProcessor(this.proteins));
        itemProcessors.add(new InteractionProcessor(this.proteins));
        itemProcessors.add(diseaseProcessor());
        itemProcessors.add(new VariantProcessor(this.proteins));
        compositeProcessor.setDelegates(itemProcessors);
        return compositeProcessor;
    }

    @Bean
    DiseaseProcessor diseaseProcessor(){
        return new DiseaseProcessor(this.proteins);
    }

    @Bean
    public CompositeItemWriter<UniProtEntry> uniProtCompositeWriter(){
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
    public ProteinWriter1 proteinWriter1() {

        return new ProteinWriter1(this.proteins);
    }

    @Bean
    public ProteinWriter proteinWriter() {

        return new ProteinWriter(proteinIdProteinMap);
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

}
