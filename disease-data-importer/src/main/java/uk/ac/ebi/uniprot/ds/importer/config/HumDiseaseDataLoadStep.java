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
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import uk.ac.ebi.uniprot.ds.common.model.Disease;
import uk.ac.ebi.uniprot.ds.importer.util.Constants;
import uk.ac.ebi.uniprot.ds.importer.reader.HumDiseaseReader;
import uk.ac.ebi.uniprot.ds.importer.writer.HumDiseaseWriter;

import java.io.FileNotFoundException;

@Configuration
public class HumDiseaseDataLoadStep {

    @Value(("${ds.import.chunk.size}"))
    private Integer chunkSize;

    @Value("${ds.humdisease.data.file.path}")
    private String humDiseaseDataFile;

    @Bean
    public Step humDiseaseStep(StepBuilderFactory stepBuilderFactory, StepExecutionListener stepListener,
                               ChunkListener chunkListener,
                               @Qualifier("humDiseaseReader") ItemReader<Disease> humDiseaseReader,
                               ItemWriter<Disease> humDiseaseWriter) throws FileNotFoundException {
        return stepBuilderFactory.get(Constants.DS_HUM_DISEASE_DATA_LOADER_STEP)
                .<Disease, Disease>chunk(chunkSize)
                .reader(humDiseaseReader)
                .writer(humDiseaseWriter)
                .listener(stepListener)
                .listener(chunkListener)
                .build();
    }

    @Bean(name = "humDiseaseReader")
    ItemReader<Disease> humDiseaseReader() throws FileNotFoundException {
        ItemReader<Disease> reader = new HumDiseaseReader(humDiseaseDataFile);
        return reader;
    }

    @Bean
    public HumDiseaseWriter humDiseaseWriter(){
        return new HumDiseaseWriter();
    }

}
