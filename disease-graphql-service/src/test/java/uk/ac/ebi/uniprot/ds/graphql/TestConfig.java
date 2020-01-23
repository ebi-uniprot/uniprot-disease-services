package uk.ac.ebi.uniprot.ds.graphql;

import org.modelmapper.ModelMapper;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import uk.ac.ebi.uniprot.ds.graphql.model.mapper.DiseaseToDiseaseType;
import uk.ac.ebi.uniprot.ds.graphql.model.mapper.ProteinToProteinType;

@TestConfiguration
public class TestConfig {
    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.addMappings(new DiseaseToDiseaseType());
        modelMapper.addMappings(new ProteinToProteinType());
        return modelMapper;
    }
}
