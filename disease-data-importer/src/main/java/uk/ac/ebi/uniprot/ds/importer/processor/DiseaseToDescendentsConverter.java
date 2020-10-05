package uk.ac.ebi.uniprot.ds.importer.processor;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;
import java.util.stream.Collectors;

import javax.sql.DataSource;

import uk.ac.ebi.uniprot.ds.importer.model.DiseaseDescendentDTO;

/**
 * @author sahmad
 * @created 02/10/2020
 */
public class DiseaseToDescendentsConverter implements ItemProcessor<Long, List<DiseaseDescendentDTO>> {
    private static final String QUERY_TO_GET_DESCENDENTS = "" +
            "WITH RECURSIVE cd AS (" +
            "   SELECT id from ds_disease where id=? " +
            "   UNION ALL " +
            "   SELECT dr.ds_disease_id " +
            "   FROM ds_disease_relation AS dr " +
            "     JOIN cd ON cd.id = dr.ds_disease_parent_id " +
            ") " +
            "SELECT DISTINCT cd.id FROM cd order by 1";

    private final JdbcTemplate jdbcTemplate;
    public DiseaseToDescendentsConverter(DataSource dataSource){
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public List<DiseaseDescendentDTO> process(Long diseaseId) throws Exception {
        List<Long> descendents = this.jdbcTemplate.query(QUERY_TO_GET_DESCENDENTS,
                (rs, idx) -> rs.getLong("id"), diseaseId);

        return descendents.stream().map(descId -> new DiseaseDescendentDTO(diseaseId, descId))
                .collect(Collectors.toList());
    }
}
