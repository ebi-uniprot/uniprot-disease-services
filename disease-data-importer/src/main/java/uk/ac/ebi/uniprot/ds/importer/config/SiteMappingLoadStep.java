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
import uk.ac.ebi.uniprot.ds.common.model.SiteMapping;
import uk.ac.ebi.uniprot.ds.importer.reader.SiteMappingReader;
import uk.ac.ebi.uniprot.ds.importer.util.Constants;
import uk.ac.ebi.uniprot.ds.importer.writer.SiteMappingWriter;

import java.io.FileNotFoundException;

/**
 * @author lgonzales
 * @since 10/09/2020
 */
@Configuration
public class SiteMappingLoadStep {

    @Value(("${ds.import.chunk.size}"))
    private Integer chunkSize;

    @Value("${ds.site.mapping.file.path}")
    private String siteMappingDataFile;

    @Bean
    public Step siteMappingStep(StepBuilderFactory stepBuilderFactory, StepExecutionListener stepListener,
                               ChunkListener chunkListener,
                               @Qualifier("siteMappingReader") ItemReader<SiteMapping> siteMappingReader,
                               ItemWriter<SiteMapping> siteMappingWriter) {
        return stepBuilderFactory.get(Constants.DS_SITE_MAPPING_DATA_LOADER_STEP)
                .<SiteMapping, SiteMapping>chunk(chunkSize)
                .reader(siteMappingReader)
                .writer(siteMappingWriter)
                .listener(stepListener)
                .listener(chunkListener)
                .build();
    }

    @Bean(name = "siteMappingReader")
    ItemReader<SiteMapping> siteMappingReader() throws FileNotFoundException {
        return new SiteMappingReader(siteMappingDataFile);
    }

    @Bean
    public SiteMappingWriter siteMappingWriter(){
        return new SiteMappingWriter();
    }

}
