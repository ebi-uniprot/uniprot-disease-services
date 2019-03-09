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
import uk.ac.ebi.uniprot.ds.common.model.Drug;
import uk.ac.ebi.uniprot.ds.common.model.ProteinCrossRef;
import uk.ac.ebi.uniprot.ds.importer.processor.ProteinCrossRefToDrugs;
import uk.ac.ebi.uniprot.ds.importer.reader.ChEMBLDrugReader;
import uk.ac.ebi.uniprot.ds.importer.util.Constants;
import uk.ac.ebi.uniprot.ds.importer.util.JDBCConnectionUtils;
import uk.ac.ebi.uniprot.ds.importer.writer.DrugWriter;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

@Configuration
public class ChEMBLDrugLoadStep {

    @Value(("${ds.import.chunk.size}"))
    private Integer chunkSize;
    @Value("${ds.do.hum.mapping.file.path}")
    private String doHumMappingFile;
    @Value(("${db.oracle.chembl.jdbcUrl}"))
    private String chemblJdbcUrl;
    @Value(("${db.oracle.chembl.user}"))
    private String chemblUser;
    @Value(("${db.oracle.chembl.password}"))
    private String chemblPasswd;

    @Bean(name = "chDrugLoad")
    public Step drugLoadStep(StepBuilderFactory stepBuilderFactory, StepExecutionListener stepListener,
                                        ItemReader<ProteinCrossRef> drugReader,
                                        ItemProcessor<ProteinCrossRef, List<Drug>> drugConverter,
                                        ItemWriter<List<Drug>> drugWriter)  {
        return stepBuilderFactory.get(Constants.DS_DRUG_LOADER_STEP)
                .<ProteinCrossRef, List<Drug>>chunk(chunkSize)
                .reader(drugReader)
                .processor(drugConverter)
                .writer(drugWriter)
                .listener(stepListener)
                .build();
    }

    @Bean
    public ItemReader<ProteinCrossRef> drugReader(){
        ItemReader<ProteinCrossRef>  reader = new ChEMBLDrugReader();
        return reader;
    }

    @Bean
    public ItemProcessor<ProteinCrossRef, List<Drug>> xrefToDrugs() throws SQLException {
        Connection dbConn = JDBCConnectionUtils.getConnection(this.chemblUser, this.chemblPasswd, this.chemblJdbcUrl);
        ItemProcessor<ProteinCrossRef, List<Drug>> processor = new ProteinCrossRefToDrugs(dbConn);
        return processor;
    }

    @Bean
    public ItemWriter<List<Drug>> drugsWriter() {
        return new DrugWriter();
    }

}
