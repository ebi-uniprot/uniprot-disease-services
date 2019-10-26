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
import uk.ac.ebi.uniprot.ds.importer.processor.MondoTermToDiseaseConverter;
import uk.ac.ebi.uniprot.ds.importer.reader.HumDiseaseReader;
import uk.ac.ebi.uniprot.ds.importer.reader.MondoDiseaseReader;
import uk.ac.ebi.uniprot.ds.importer.reader.diseaseontology.OBOTerm;
import uk.ac.ebi.uniprot.ds.importer.util.Constants;
import uk.ac.ebi.uniprot.ds.importer.writer.HumDiseaseWriter;

import java.io.FileNotFoundException;

import static uk.ac.ebi.uniprot.ds.importer.util.Constants.*;

/**
 * Loads the Mondo Diseases from mondo.obo file. We load only the missing term(it can be disease or disease group name)
 * We are doing this just to create the Parent-Child relationship among diseases.
 */
@Configuration
public class MondoDiseaseLoadStep {

    @Value(("${ds.import.chunk.size}"))
    private Integer chunkSize;

    @Value("${ds.mondo.file.path}")
    private String mondoDiseaseDataFile;

    @Bean
    public Step mondoDiseaseStep(StepBuilderFactory stepBuilderFactory, StepExecutionListener stepListener,
                               ChunkListener chunkListener,
                                 @Qualifier("mondoOBOReader") ItemReader<OBOTerm> mondoDiseaseReader,
                               ItemProcessor<OBOTerm, Disease> oboToDiseaseConverter,
                               ItemWriter<Disease> diseaseWriter) throws FileNotFoundException {
        return stepBuilderFactory.get(Constants.DS_MONDO_DISEASE_DATA_LOADER_STEP)
                .<OBOTerm, Disease>chunk(chunkSize)
                .reader(mondoDiseaseReader) // load terms from mondo.obo
                .processor(oboToDiseaseConverter)// convert obo term to disease.
                .writer(diseaseWriter) // See HumDiseaseWriter. Write the mondo obo term as synonym or a disease through create/update disease.
                .listener(promotionListener()) // to promote step context as job context to pass data around
                .listener(stepListener)
                .listener(chunkListener)
                .build();
    }

    @Bean
    public ExecutionContextPromotionListener promotionListener() {
        ExecutionContextPromotionListener listener = new ExecutionContextPromotionListener();
        listener.setKeys(new String[] {DISEASE_NAME_OR_OMIM_DISEASE_MAP, MONDO_OBO_TERMS_LIST});
        return listener;
    }

    @Bean(name = "mondoOBOReader")
    ItemReader<OBOTerm> mondoOBOFileReader() throws FileNotFoundException {
        ItemReader<OBOTerm> reader = new MondoDiseaseReader(this.mondoDiseaseDataFile);
        return reader;
    }

    @Bean
    public ItemProcessor<OBOTerm, Disease> oboToDiseaseConverter() {
        ItemProcessor<OBOTerm, Disease> converter = new MondoTermToDiseaseConverter();
        return converter;
    }

}
