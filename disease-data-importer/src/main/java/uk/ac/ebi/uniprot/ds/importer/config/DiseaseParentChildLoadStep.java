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
import org.springframework.batch.item.database.ItemPreparedStatementSetter;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import uk.ac.ebi.uniprot.ds.importer.model.DiseaseRelationDTO;
import uk.ac.ebi.uniprot.ds.importer.processor.MondoTermToDiseaseChildConverter;
import uk.ac.ebi.uniprot.ds.importer.reader.MondoTermReader;
import uk.ac.ebi.uniprot.ds.importer.reader.diseaseontology.OBOTerm;
import uk.ac.ebi.uniprot.ds.importer.util.Constants;
import uk.ac.ebi.uniprot.ds.importer.writer.ListJDBCBatchItemWriter;

import javax.sql.DataSource;
import java.io.FileNotFoundException;
import java.util.List;

@Configuration
public class DiseaseParentChildLoadStep {

    @Value(("${ds.import.chunk.size}"))
    private Integer chunkSize;
    private static final String QUERY_TO_INSERT_DISEASE_RELATION = "INSERT " +
            "INTO ds_disease_relation(ds_disease_parent_id, ds_disease_id) " +
            "VALUES (?, ?)";

    @Bean
    public Step parentChildLoadStep(StepBuilderFactory stepBuilderFactory, StepExecutionListener stepListener,
                                           ChunkListener chunkListener,
                                           @Qualifier("oboTermReader") ItemReader<OBOTerm> oboTermReader,
                                           @Qualifier("oboTermToDiseaseChildConverter") ItemProcessor<OBOTerm, List<DiseaseRelationDTO>> oboTermToDiseaseChildConverter,
                                           @Qualifier("diseaseRelationWriter") ItemWriter<List<DiseaseRelationDTO>> listJDBCItemWriter) throws FileNotFoundException {

        return stepBuilderFactory.get(Constants.DS_DISEASE_PARENT_CHILD_LOADER_STEP)
                .<OBOTerm, List<DiseaseRelationDTO>>chunk(chunkSize)
                .reader(oboTermReader)
                .processor(oboTermToDiseaseChildConverter)
                .writer(listJDBCItemWriter)
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
    public ItemProcessor<OBOTerm, List<DiseaseRelationDTO>> oboTermToDiseaseConverter() {
        ItemProcessor<OBOTerm, List<DiseaseRelationDTO>> processor =
                new MondoTermToDiseaseChildConverter();
        return processor;
    }

    @Bean(name = "diseaseRelationWriter")
    public ItemWriter<List<DiseaseRelationDTO>> listJDBCItemWriter(DataSource dataSource, NamedParameterJdbcTemplate jdbcTemplate) {
        JdbcBatchItemWriter<DiseaseRelationDTO> jdbcBatchItemWriter = new JdbcBatchItemWriter<>();
        jdbcBatchItemWriter.setDataSource(dataSource);
        jdbcBatchItemWriter.setJdbcTemplate(jdbcTemplate);
        jdbcBatchItemWriter.setSql(QUERY_TO_INSERT_DISEASE_RELATION);
        ItemPreparedStatementSetter<DiseaseRelationDTO> itemPreparedStatementSetter=
                (dto, ps) -> {
                    ps.setLong(1, dto.getParentId());
                    ps.setLong(2, dto.getChildId());
                };
        jdbcBatchItemWriter.setItemPreparedStatementSetter(itemPreparedStatementSetter);

        ListJDBCBatchItemWriter<DiseaseRelationDTO> listJDBCBatchItemWriter = new ListJDBCBatchItemWriter<>();
        listJDBCBatchItemWriter.setDelegate(jdbcBatchItemWriter);

        return listJDBCBatchItemWriter;
    }
}
