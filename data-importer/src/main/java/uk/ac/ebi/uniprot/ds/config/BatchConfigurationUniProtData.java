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
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.support.CompositeItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import uk.ac.ebi.kraken.interfaces.uniprot.UniProtEntry;
import uk.ac.ebi.uniprot.disease.model.sources.uniprot.UniProtDisease;
import uk.ac.ebi.uniprot.ds.listener.LogJobListener;
import uk.ac.ebi.uniprot.ds.listener.LogStepListener;
import uk.ac.ebi.uniprot.ds.model.Disease;
import uk.ac.ebi.uniprot.ds.model.Protein;
import uk.ac.ebi.uniprot.ds.processor.UniProtDiseaseToDiseaseConverter;
import uk.ac.ebi.uniprot.ds.reader.HumDiseaseReader;
import uk.ac.ebi.uniprot.ds.reader.UniProtReader;
import uk.ac.ebi.uniprot.ds.util.Constants;
import uk.ac.ebi.uniprot.ds.writer.*;

import java.io.FileNotFoundException;
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

    @Value("${ds.humdisease.data.file.path}")
    private String humDiseaseDataFile;

    @Value("${ds.uniprot.data.file.absolute.path}")
    private String uniProtDataAbsFilePath;

    @Value(("${ds.import.chunk.size}"))
    private Integer chunkSize;

    @Bean
    public Job importUniProtDataJob(JobExecutionListener jobExecutionListener, Step humDiseaseStep,
                                    Step uniProtStep) throws FileNotFoundException {

        return jobBuilderFactory.get(Constants.DISEASE_SERVICE_DATA_LOADER)
                .incrementer(new RunIdIncrementer())
                .flow(humDiseaseStep)
                .next(uniProtStep)
                .end()
                .listener(jobExecutionListener)
                .build();
    }


    @Bean
    public Step humDiseaseStep(StepExecutionListener stepListener,
                               ItemReader<UniProtDisease> humDiseaseReader,
                               ItemProcessor<UniProtDisease, Disease> humDiseaseConverter,
                               ItemWriter<Disease> humDiseaseWriter) throws FileNotFoundException {
        return this.stepBuilderFactory.get(Constants.DS_HUM_DISEASE_DATA_LOADER_STEP)
                .<UniProtDisease, Disease>chunk(chunkSize)
                .reader(humDiseaseReader)
                .processor(humDiseaseConverter)
                .writer(humDiseaseWriter)
                .listener(stepListener)
                .build();
    }

    @Bean
    public Step uniProtStep(StepExecutionListener stepListener,
                            ItemReader<UniProtEntry> uniProtReader, CompositeItemWriter<UniProtEntry> uniProtCompositeWriter) {
        return stepBuilderFactory.get(Constants.DS_UNIPROT_DATA_LOADER_STEP)
                .<UniProtEntry, UniProtEntry>chunk(chunkSize)
                .reader(uniProtReader)
                .writer(uniProtCompositeWriter)
                .listener(stepListener)
                .build();
    }

    @Bean
    public UniProtDiseaseToDiseaseConverter humDiseaseConverter() {
        return new UniProtDiseaseToDiseaseConverter();
    }

    @Bean
    public HumDiseaseWriter humDiseaseWriter(){
        return new HumDiseaseWriter();
    }

    @Bean
    ItemReader<UniProtDisease> humDiseaseReader() throws FileNotFoundException {
        ItemReader<UniProtDisease> reader = new HumDiseaseReader(humDiseaseDataFile);
        return reader;
    }

    @Bean
    public ProteinWriter proteinWriter() {
        return new ProteinWriter(proteinIdProteinMap);
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
    public ItemReader<UniProtEntry> uniProtReader() {
        UniProtReader reader = new UniProtReader(uniProtDataAbsFilePath);
        return reader;
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
