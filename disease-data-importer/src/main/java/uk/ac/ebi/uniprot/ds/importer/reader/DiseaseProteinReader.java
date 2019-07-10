package uk.ac.ebi.uniprot.ds.importer.reader;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class DiseaseProteinReader implements RowMapper<DiseaseProteinReader.DiseaseProteinDTO> {

    @Override
    public DiseaseProteinDTO mapRow(ResultSet resultSet, int index) throws SQLException {
        long diseaseId = resultSet.getLong("disease_id");
        long proteinId = resultSet.getLong("protein_id");
        boolean isMapped = resultSet.getBoolean("is_mapped");

        return new DiseaseProteinDTO(diseaseId, proteinId, isMapped);
    }

    @Getter
    @Setter
    @AllArgsConstructor
    public static class DiseaseProteinDTO {
        private final long diseaseId;
        private final long proteinId;
        private final boolean isMapped;
        public boolean getIsMapped(){
            return this.isMapped;
        }

    }
}
