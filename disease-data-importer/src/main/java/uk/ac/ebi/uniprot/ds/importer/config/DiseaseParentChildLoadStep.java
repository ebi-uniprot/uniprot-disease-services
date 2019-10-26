/*
 * Created by sahmad on 1/30/19 10:19 PM
 * UniProt Consortium.
 * Copyright (c) 2002-2019.
 *
 */

package uk.ac.ebi.uniprot.ds.importer.config;

import org.springframework.batch.core.ChunkListener;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.listener.ExecutionContextPromotionListener;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import uk.ac.ebi.uniprot.ds.common.model.Disease;
import uk.ac.ebi.uniprot.ds.importer.processor.MondoTermToDiseaseChildConverter;
import uk.ac.ebi.uniprot.ds.importer.reader.MondoTermReader;
import uk.ac.ebi.uniprot.ds.importer.reader.diseaseontology.OBOTerm;
import uk.ac.ebi.uniprot.ds.importer.util.Constants;

import java.io.FileNotFoundException;
import java.util.List;

import static uk.ac.ebi.uniprot.ds.importer.util.Constants.*;

@Configuration
public class DiseaseParentChildLoadStep {

    @Value(("${ds.import.chunk.size}"))
    private Integer chunkSize;

    @Bean
    public Step parentChildLoadStep(StepBuilderFactory stepBuilderFactory, StepExecutionListener stepListener,
                                           ChunkListener chunkListener,
                                           @Qualifier("oboTermReader") ItemReader<OBOTerm> oboTermReader,
                                           @Qualifier("oboTermToDiseaseChildConverter") ItemProcessor<OBOTerm, Disease> oboTermToDiseaseChildConverter,
                                           ItemWriter<Disease> diseaseWriter) throws FileNotFoundException {
        return stepBuilderFactory.get(Constants.DS_DISEASE_PARENT_CHILD_LOADER_STEP)
                .<OBOTerm, Disease>chunk(chunkSize)
                .reader(oboTermReader)
                .processor(oboTermToDiseaseChildConverter)
                .writer(diseaseWriter)
                .listener(stepListener)
                .listener(chunkListener)
                .build();
    }

    @Bean(name = "oboTermReader")
    public ItemReader<OBOTerm> oboTermReader() {
        ItemReader<OBOTerm> reader = new MondoTermReader();
        return reader;
    }

    @Bean(name = "oboTermToDiseaseChildConverter")
    public ItemProcessor<OBOTerm, Disease> oboTermToDiseaseConverter() {
        ItemProcessor<OBOTerm, Disease> processor = new MondoTermToDiseaseChildConverter();
        return processor;
    }

}
