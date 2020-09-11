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

import java.io.IOException;
import java.util.List;

import uk.ac.ebi.uniprot.ds.common.model.Drug;
import uk.ac.ebi.uniprot.ds.importer.model.ChemblOpenTarget;
import uk.ac.ebi.uniprot.ds.importer.processor.ChemblOpenTargetToDrugs;
import uk.ac.ebi.uniprot.ds.importer.reader.ChemblOpenTargetReader;
import uk.ac.ebi.uniprot.ds.importer.util.Constants;
import uk.ac.ebi.uniprot.ds.importer.writer.DrugWriter;

@Configuration
public class ChEMBLDrugLoadStep {

    @Value(("${ds.import.chunk.size}"))
    private Integer chunkSize;
    @Value("${ds.do.chembl.opentarget.file.path}")
    private String chemblOpenTargetFile;
    @Value("${ds.omim.efo.mapping.file}")
    private String omim2EfoFile;

    @Bean(name = "chDrugLoad")
    public Step drugLoadStep(StepBuilderFactory stepBuilderFactory, StepExecutionListener stepListener,
                                        ChunkListener chunkListener,
                                        ItemReader<ChemblOpenTarget> drugReader,
                                        ItemProcessor<ChemblOpenTarget, List<Drug>> drugConverter,
                                        ItemWriter<List<Drug>> drugWriter)  {
        return stepBuilderFactory.get(Constants.DS_DRUG_LOADER_STEP)
                .<ChemblOpenTarget, List<Drug>>chunk(chunkSize)
                .reader(drugReader)
                .processor(drugConverter)
                .writer(drugWriter)
                .listener(stepListener)
                .listener(chunkListener)
                .build();
    }

    @Bean
    public ItemReader<ChemblOpenTarget> drugReader() throws IOException {
        return new ChemblOpenTargetReader(this.chemblOpenTargetFile);
    }

    @Bean
    public ItemProcessor<ChemblOpenTarget, List<Drug>> xrefToDrugs() {
        return new ChemblOpenTargetToDrugs(this.omim2EfoFile);
    }

    @Bean
    public ItemWriter<List<Drug>> drugsWriter() {
        return new DrugWriter();
    }

}
