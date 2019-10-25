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
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import uk.ac.ebi.uniprot.ds.common.model.Disease;
import uk.ac.ebi.uniprot.ds.importer.processor.DiseaseOntologyProcessor;
import uk.ac.ebi.uniprot.ds.importer.reader.MondoReader;
import uk.ac.ebi.uniprot.ds.importer.reader.diseaseontology.DiseaseOntologyReader;
import uk.ac.ebi.uniprot.ds.importer.reader.diseaseontology.OBOTerm;
import uk.ac.ebi.uniprot.ds.importer.util.Constants;
import uk.ac.ebi.uniprot.ds.importer.writer.DiseaseOntologyWriter;

import javax.xml.bind.JAXBException;
import java.io.FileNotFoundException;
import java.util.List;

@Configuration
public class DiseaseOntologyDataLoadStep {

    @Value(("${ds.import.chunk.size}"))
    private Integer chunkSize;
    @Value("${ds.do.data.file.path}")
    private String doDataFile;

    @Bean(name = "doLoad")
    public Step diseaseOntologyLoadStep(StepBuilderFactory stepBuilderFactory, StepExecutionListener stepListener,
                                        ChunkListener chunkListener,
                                        ItemReader<List<OBOTerm>> doReader,
                                        ItemProcessor<List<OBOTerm>, List<Disease>> doProcessor,
                                        ItemWriter<List<Disease>> doWriter) throws FileNotFoundException {
        return stepBuilderFactory.get(Constants.DS_DISEASE_PARENT_CHILD_LOADER_STEP)
                .<List<OBOTerm>, List<Disease>>chunk(chunkSize)
                .reader(doReader)
                .processor(doProcessor)
                .writer(doWriter)
                .listener(stepListener)
                .listener(chunkListener)
                .build();
    }

    @Bean
    public ItemReader<List<OBOTerm>> doReader() throws FileNotFoundException, JAXBException {
        ItemReader<List<OBOTerm>> reader = new MondoReader();
        return reader;
    }

    @Bean
    public ItemProcessor<List<OBOTerm>, List<Disease>> doProcessor() {
        ItemProcessor<List<OBOTerm>, List<Disease>> processor = new DiseaseOntologyProcessor();
        return processor;
    }

    @Bean
    public ItemWriter<List<Disease>> doWriter() {
        return new DiseaseOntologyWriter();
    }

}
