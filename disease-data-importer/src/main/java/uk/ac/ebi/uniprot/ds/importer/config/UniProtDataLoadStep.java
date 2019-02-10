/*
 * Created by sahmad on 1/30/19 10:26 PM
 * UniProt Consortium.
 * Copyright (c) 2002-2019.
 *
 */

package uk.ac.ebi.uniprot.ds.importer.config;

import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.support.CompositeItemWriter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import uk.ac.ebi.kraken.interfaces.uniprot.UniProtEntry;
import uk.ac.ebi.uniprot.ds.common.model.Protein;
import uk.ac.ebi.uniprot.ds.importer.util.Constants;
import uk.ac.ebi.uniprot.ds.importer.writer.*;
import uk.ac.ebi.uniprot.ds.importer.reader.UniProtReader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration
public class UniProtDataLoadStep {
    private Map<String, Protein> proteinIdProteinMap = new HashMap<>();

    @Value(("${ds.import.chunk.size}"))
    private Integer chunkSize;

    @Value("${ds.uniprot.data.file.path}")
    private String uniProtDataFilePath;


    @Bean
    public Step uniProtStep(StepBuilderFactory stepBuilderFactory, StepExecutionListener stepListener,
                            ItemReader<UniProtEntry> uniProtReader, CompositeItemWriter<UniProtEntry> uniProtCompositeWriter) {
        return stepBuilderFactory.get(Constants.DS_UNIPROT_DATA_LOADER_STEP)
                .<UniProtEntry, UniProtEntry>chunk(chunkSize)
                .reader(uniProtReader)
                .writer(uniProtCompositeWriter)
                .listener(stepListener)
                .build();
    }

    @Bean
    public ItemReader<UniProtEntry> uniProtReader() {
        UniProtReader reader = new UniProtReader(uniProtDataFilePath);
        return reader;
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
