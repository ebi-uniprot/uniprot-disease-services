/*
 * Created by sahmad on 1/30/19 10:19 PM
 * UniProt Consortium.
 * Copyright (c) 2002-2019.
 *
 */

package uk.ac.ebi.uniprot.ds.importer.config;

import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import uk.ac.ebi.uniprot.dataservice.domain.coordinate.jaxb.GnEntry;
import uk.ac.ebi.uniprot.ds.common.model.GeneCoordinate;
import uk.ac.ebi.uniprot.ds.importer.processor.GnEntryToGeneCoordinateConverter;
import uk.ac.ebi.uniprot.ds.importer.reader.GeneCoordinateReader;
import uk.ac.ebi.uniprot.ds.importer.util.Constants;
import uk.ac.ebi.uniprot.ds.importer.writer.GeneCoordinateWriter;

import javax.xml.bind.JAXBException;
import java.io.FileNotFoundException;
import java.util.List;

@Configuration
public class GeneCoordinateDataLoadStep {

    @Value(("${ds.import.chunk.size}"))
    private Integer chunkSize;
    @Value("${ds.genecoords.data.file.path}")
    private String geneCoordsDataFile;

    @Bean(name = "geneCoordsLoad")
    public Step geneCoordinatesLoadStep(StepBuilderFactory stepBuilderFactory, StepExecutionListener stepListener,
                                       ItemReader<GnEntry> geneCoordinateReader,
                                       ItemProcessor<GnEntry, List<GeneCoordinate>> gnEntryToGnCoordConverter,
                                       ItemWriter<List<GeneCoordinate>> geneCoordindateWriter) throws FileNotFoundException {
        return stepBuilderFactory.get(Constants.DS_GENE_COORD_LOADER_STEP)
                .<GnEntry, List<GeneCoordinate>>chunk(chunkSize)
                .reader(geneCoordinateReader)
                .processor(gnEntryToGnCoordConverter)
                .writer(geneCoordindateWriter)
                .listener(stepListener)
                .build();
    }

    @Bean
    public ItemReader<GnEntry> geneCoordinateReader() throws FileNotFoundException, JAXBException {
        ItemReader<GnEntry> reader = new GeneCoordinateReader(geneCoordsDataFile);
        return reader;
    }

    @Bean
    public ItemProcessor<GnEntry, List<GeneCoordinate>> gnEntryToGnCoordConverter() {
        ItemProcessor<GnEntry, List<GeneCoordinate>> processor = new GnEntryToGeneCoordinateConverter();
        return processor;
    }

    @Bean
    public GeneCoordinateWriter geneCoordindateWriter() {
        return new GeneCoordinateWriter();
    }

}
