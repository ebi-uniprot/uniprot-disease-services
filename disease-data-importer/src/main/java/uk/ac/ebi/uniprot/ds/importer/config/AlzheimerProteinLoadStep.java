package uk.ac.ebi.uniprot.ds.importer.config;

import org.springframework.batch.core.ChunkListener;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.ItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import uk.ac.ebi.uniprot.ds.importer.reader.DiseaseProteinReader;
import uk.ac.ebi.uniprot.ds.importer.util.Constants;

import javax.sql.DataSource;

/**
 * @author sahmad
 */
@Configuration
public class AlzheimerProteinLoadStep {

    // Query to get all the proteins which are manually mapped to AD
    private static final String QUERY_TO_GET_AD_PROTEIN = "select  p.id as protein_id, " +
            "(select d.id from ds_disease d where d.disease_id = 'Alzheimer disease') as disease_id, " +
            "true as is_mapped " +
            "from     ds_protein p where     p.accession in ('O75899', " +
            "'P05023',     'Q14934',     'Q562E7',     'Q6UXX9',     'Q14739', " +
            "'Q8N8I0',     'Q8NDV7',     'Q9NUN7',     'Q9Y3C8',     'Q9BXJ3', " +
            "'O15269',     'O15270',     'O95470',     'P05067',     'P40818', " +
            "'P56817',     'Q09327',     'Q13526',     'Q5JVS0',     'Q8IWX5', " +
            "'Q8N3V7',     'Q8N427',     'Q8NFR3',     'Q92599',     'Q969W0', " +
            "'Q99250',     'Q9BX95',     'Q9NQC3',     'Q9NRA0',     'Q9NUV7', " +
            "'Q9NYA1',     'Q9NZ52',     'Q9UJY4',     'Q9UJY5',     'Q99719', " +
            "'O14880',     'O60356',     'P10909',     'P20020',     'P21246', " +
            "'P21741',     'P28799',     'Q8NEU8',     'Q96LT7',     'O14672', " +
            "'P10636',     'P27144',     'P61026',     'Q4J6C6',     'Q5S007', " +
            "'Q6UWF3',     'Q86TB3',     'Q8IV01',     'Q9H2J4',     'Q9NZC2', " +
            "'Q9Y6A9',     'P02649',     'P04062',     'P47870',     'Q13510', " +
            "'Q16739',     'Q5QJU3',     'Q8NBF1',     'Q8TDN7',     'Q9H227', " +
            "'Q9HCG7',     'Q9NR71',     'O00499',     'P49810',     'Q86VP3', " +
            "'Q96FE5',     'Q9C0K0',     'Q9UQM7',     'Q9Y2G1',     'P49768', " +
            "'O43914',     'Q6U841',     'P17302',     'P78536',     'P19021', " +
            "'Q02318',     'Q9Y6A2',     'O15240',     'O60602',     'O75607', " +
            "'P02778',     'P05156',     'P08603',     'P11836',     'P16070', " +
            "'P17927',     'P20138',     'P20336',     'P22301',     'P26038', " +
            "'P30740',     'P35637',     'P37840',     'Q13148',     'Q13492', " +
            "'Q13651',     'Q16623',     'Q8WXD2',     'Q9H063',     'Q9H5N1', " +
            "'Q9UPA5',     'Q9Y2J0',     'Q9Y2K9',     'O43157',     'P14867', " +
            "'P41597',     'Q9NX46',     'P19793',     'P26378',     'Q13127', " +
            "'Q15149',     'Q8IZY2',     'Q96DC8',     'Q9UQB8', " +
            "'Q9Y232',     'O00116',     'O60603',     'O60663',     'O75509', " +
            "'O75920',     'O76096',     'O95153',     'O95206',     'P01024', " +
            "'P01130',     'P01137',     'P01375',     'P01584',     'P02745', " +
            "'P04233',     'P04792',     'P05231',     'P06858',     'P07196', " +
            "'P07333',     'P07339',     'P08575',     'P08651',     'P11215', " +
            "'P12314',     'P12821',     'P14136',     'P14735',     'P14780', " +
            "'P16885',     'P17252',     'P17947',     'P19438',     'P20273', " +
            "'P21453',     'P21709',     'P21730',     'P21860',     'P23470', " +
            "'P24593',     'P30273',     'P30793',     'P34810',     'P34998', " +
            "'P34998',     'P35398',     'P35625',     'P37173',     'P38606', " +
            "'P40925',     'P41146',     'P43034',     'P49238',     'P53804', " +
            "'P55008',     'P55055',     'P55210',     'P61366',     'P61769', " +
            "'P63010',     'P68402',     'P78324',     'P78423',     'Q00535', " +
            "'Q06413',     'Q06520',     'Q07869',     'Q08209',     'Q08722', " +
            "'Q09472',     'Q12879',     'Q12908',     'Q13133',     'Q13153', " +
            "'Q13443',     'Q13546',     'Q13547',     'Q13572',     'Q14289', " +
            "'Q14693',     'Q15077',     'Q15102',     'Q15382',     'Q15650', " +
            "'Q15700',     'Q16082',     'Q16204',     'Q16820',     'Q2M1K9', " +
            "'Q30154',     'Q495T6',     'Q4V9L6',     'Q6UXB4',     'Q7Z7G1', " +
            "'Q86SF2',     'Q8IV08',     'Q8N0W5',     'Q8NEA6',     'Q8NFF2', " +
            "'Q8NHY3',     'Q8TD46',     'Q92835',     'Q92879',     'Q96AC1', " +
            "'Q96D09',     'Q96JQ5',     'Q96PG1',     'Q96PV0',     'Q96RI1', " +
            "'Q99075',     'Q99571',     'Q99712',     'Q99714',     'Q99726', " +
            "'Q99836',     'Q9BRN9',     'Q9BXN2',     'Q9BXS0',     'Q9BZF3', " +
            "'Q9H1B7',     'Q9H2W1',     'Q9H7Z6',     'Q9HB55',     'Q9NPD7', " +
            "'Q9NQ66',     'Q9NQ75',     'Q9NY64',     'Q9NZC7',     'Q9P2A4', " +
            "'Q9P2Q2',     'Q9UBK2',     'Q9UBX0',     'Q9UHI8',     'Q9UIW2', " +
            "'Q9UJT9',     'Q9UKJ1',     'Q9UM73',     'Q9Y287',     'Q9Y5B0', " +
            "'Q9Y5K6',     'Q9Y5Z0',     'Q9Y691')";


    private static final String QUERY_TO_INSERT_AD_PROTEIN = "INSERT " +
            "INTO ds_disease_protein(ds_disease_id, ds_protein_id, is_mapped) " +
            "VALUES (:diseaseId, :proteinId, :isMapped)";


    @Value(("${ds.import.chunk.size}"))
    private Integer chunkSize;

    @Bean(name = "alzheimerProteinLoad")
    public Step diseaseProteinStep(StepBuilderFactory stepBuilders, StepExecutionListener stepListener,
                                  ChunkListener chunkListener,
                                  ItemReader<DiseaseProteinReader.DiseaseProteinDTO> reader,
                                  ItemWriter<DiseaseProteinReader.DiseaseProteinDTO> writer) {
        return stepBuilders.get(Constants.DS_AD_PROTEIN_LOADER_STEP)
                .<DiseaseProteinReader.DiseaseProteinDTO, DiseaseProteinReader.DiseaseProteinDTO>chunk(chunkSize)
                .reader(reader)
                .writer(writer)
                .listener(stepListener)
                .listener(chunkListener)
                .build();
    }

    @Bean
    public ItemReader<DiseaseProteinReader.DiseaseProteinDTO> reader(DataSource readDataSource) {
        JdbcCursorItemReader<DiseaseProteinReader.DiseaseProteinDTO> itemReader = new JdbcCursorItemReader<>();
        itemReader.setDataSource(readDataSource);
        itemReader.setSql(QUERY_TO_GET_AD_PROTEIN);
        itemReader.setRowMapper(new DiseaseProteinReader());

        return itemReader;
    }

    @Bean
    public ItemWriter<DiseaseProteinReader.DiseaseProteinDTO> writer(DataSource dataSource) {

        JdbcBatchItemWriter<DiseaseProteinReader.DiseaseProteinDTO> databaseItemWriter = new JdbcBatchItemWriter<>();
        databaseItemWriter.setDataSource(dataSource);
        databaseItemWriter.setSql(QUERY_TO_INSERT_AD_PROTEIN);
        ItemSqlParameterSourceProvider<DiseaseProteinReader.DiseaseProteinDTO> paramProvider =
                new BeanPropertyItemSqlParameterSourceProvider<>();
        databaseItemWriter.setItemSqlParameterSourceProvider(paramProvider);
        return databaseItemWriter;
    }
}
