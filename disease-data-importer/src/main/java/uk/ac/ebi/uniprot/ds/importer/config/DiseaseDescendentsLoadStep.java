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
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.util.List;

import javax.sql.DataSource;

import uk.ac.ebi.uniprot.ds.importer.model.DiseaseDescendentDTO;
import uk.ac.ebi.uniprot.ds.importer.processor.DiseaseToDescendentsConverter;
import uk.ac.ebi.uniprot.ds.importer.util.Constants;
import uk.ac.ebi.uniprot.ds.importer.writer.ListJDBCBatchItemWriter;

/**
 * @author sahmad
 * @created 02/10/2020
 */
@Configuration
public class DiseaseDescendentsLoadStep {

    @Value(("${ds.import.chunk.size}"))
    private Integer chunkSize;
    private static final String QUERY_TO_GET_DISEASE = "select id from ds_disease";
    private static final String QUERY_TO_INSERT_DISEASE_DESCENDENT = "INSERT " +
            "INTO ds_disease_descendent(ds_disease_id, ds_descendent_id) " +
            "VALUES (?, ?)";

    @Bean
    public Step descendentsLoadStep(StepBuilderFactory stepBuilderFactory, StepExecutionListener stepListener,
                                    ChunkListener chunkListener,
                                    @Qualifier("diseaseIdReader") ItemReader<Long> diseaseIdReader,
                                    @Qualifier("diseaseIdDescendentConverter") ItemProcessor<Long, List<DiseaseDescendentDTO>>
                                                       diseaseIdDescendentConverter,
                                    @Qualifier("diseaseDescendentsWriter") ItemWriter<List<DiseaseDescendentDTO>> listJDBCItemWriter) {

        return stepBuilderFactory.get(Constants.DS_DISEASE_DESCENDENT_LOADER_STEP)
                .<Long, List<DiseaseDescendentDTO>>chunk(chunkSize)
                .reader(diseaseIdReader)
                .processor(diseaseIdDescendentConverter)
                .writer(listJDBCItemWriter)
                .listener(stepListener)
                .listener(chunkListener)
                .build();
    }

    @Bean(name = "diseaseIdReader")
    public JdbcCursorItemReader<Long> reader(DataSource dataSource){
        JdbcCursorItemReader<Long> reader = new JdbcCursorItemReader<>();
        reader.setDataSource(dataSource);
        reader.setSql(QUERY_TO_GET_DISEASE);
        reader.setRowMapper((rs, idx) -> rs.getLong("id"));
        return reader;
    }

    @Bean(name="diseaseIdDescendentConverter")
    public ItemProcessor<Long, List<DiseaseDescendentDTO>> diseaseIdDescendentConverter(DataSource dataSource) {
        return new DiseaseToDescendentsConverter(dataSource);
    }

    @Bean(name = "diseaseDescendentsWriter")
    public ItemWriter<List<DiseaseDescendentDTO>> listJDBCItemWriter(DataSource dataSource, NamedParameterJdbcTemplate jdbcTemplate) {
        JdbcBatchItemWriter<DiseaseDescendentDTO> jdbcBatchItemWriter = new JdbcBatchItemWriter<>();
        jdbcBatchItemWriter.setDataSource(dataSource);
        jdbcBatchItemWriter.setJdbcTemplate(jdbcTemplate);
        jdbcBatchItemWriter.setSql(QUERY_TO_INSERT_DISEASE_DESCENDENT);
        ItemPreparedStatementSetter<DiseaseDescendentDTO> itemPreparedStatementSetter =
                (dto, ps) -> {
                    ps.setLong(1, dto.getDiseaseId());
                    ps.setLong(2, dto.getDescendentId());
                };
        jdbcBatchItemWriter.setItemPreparedStatementSetter(itemPreparedStatementSetter);

        ListJDBCBatchItemWriter<DiseaseDescendentDTO> listJDBCBatchItemWriter = new ListJDBCBatchItemWriter<>();
        listJDBCBatchItemWriter.setDelegate(jdbcBatchItemWriter);

        return listJDBCBatchItemWriter;
    }
}
