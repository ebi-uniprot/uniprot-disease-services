/*
 * Created by sahmad on 1/30/19 10:19 PM
 * UniProt Consortium.
 * Copyright (c) 2002-2019.
 *
 */

package uk.ac.ebi.uniprot.ds.importer.config;

import org.apache.commons.lang3.tuple.Pair;
import org.springframework.batch.core.ChunkListener;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import uk.ac.ebi.uniprot.ds.common.model.Disease;
import uk.ac.ebi.uniprot.ds.common.model.Synonym;
import uk.ac.ebi.uniprot.ds.importer.processor.DOHumToSynonymConverter;
import uk.ac.ebi.uniprot.ds.importer.reader.DOToHumMappingReader;
import uk.ac.ebi.uniprot.ds.importer.util.Constants;
import uk.ac.ebi.uniprot.ds.importer.writer.DOSynonymWriter;
import uk.ac.ebi.uniprot.ds.importer.writer.DiseaseOntologyWriter;

import java.io.FileNotFoundException;
import java.util.List;

@Configuration
public class DOSynonymLoadStep {

    @Value(("${ds.import.chunk.size}"))
    private Integer chunkSize;
    @Value("${ds.do.hum.mapping.file.path}")
    private String doHumMappingFile;

    @Bean(name = "doSynLoad")
    public Step doSynLoadStep(StepBuilderFactory stepBuilderFactory, StepExecutionListener stepListener,
                                        ChunkListener chunkListener,
                                        ItemReader<Pair<String, String>> humToDOReader,
                                        ItemProcessor<Pair<String, String>, Synonym> pairToSynonym,
                                        ItemWriter<Synonym> synonymWriter) throws FileNotFoundException {
        return stepBuilderFactory.get(Constants.DS_DISEASE_ONTOLOGY_SYNONYM_LOADER_STEP)
                .<Pair<String, String>, Synonym>chunk(chunkSize)
                .reader(humToDOReader)
                .processor(pairToSynonym)
                .writer(synonymWriter)
                .listener(stepListener)
                .listener(chunkListener)
                .build();
    }

    @Bean
    public ItemReader<Pair<String, String>> doHumMappingReader() throws FileNotFoundException {
        ItemReader<Pair<String, String>>  reader = new DOToHumMappingReader();
        return reader;
    }

    @Bean
    public ItemProcessor<Pair<String, String>, Synonym> pairToSynonymConverter() {
        ItemProcessor<Pair<String, String>, Synonym> processor = new DOHumToSynonymConverter();
        return processor;
    }

    @Bean
    public ItemWriter<Synonym> doSynonymWriter() {
        return new DOSynonymWriter();
    }

}
