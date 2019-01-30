/*
 * Created by sahmad on 1/30/19 10:19 PM
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import uk.ac.ebi.uniprot.disease.model.sources.uniprot.UniProtDisease;
import uk.ac.ebi.uniprot.ds.model.Disease;
import uk.ac.ebi.uniprot.ds.processor.UniProtDiseaseToDiseaseConverter;
import uk.ac.ebi.uniprot.ds.reader.HumDiseaseReader;
import uk.ac.ebi.uniprot.ds.util.Constants;
import uk.ac.ebi.uniprot.ds.writer.*;

import java.io.FileNotFoundException;

@Configuration
public class HumDiseaseDataLoadStep {
    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Value(("${ds.import.chunk.size}"))
    private Integer chunkSize;

    @Value("${ds.humdisease.data.file.path}")
    private String humDiseaseDataFile;

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
    ItemReader<UniProtDisease> humDiseaseReader() throws FileNotFoundException {
        ItemReader<UniProtDisease> reader = new HumDiseaseReader(humDiseaseDataFile);
        return reader;
    }

    @Bean
    public UniProtDiseaseToDiseaseConverter humDiseaseConverter() {
        return new UniProtDiseaseToDiseaseConverter();
    }

    @Bean
    public HumDiseaseWriter humDiseaseWriter(){
        return new HumDiseaseWriter();
    }

}
