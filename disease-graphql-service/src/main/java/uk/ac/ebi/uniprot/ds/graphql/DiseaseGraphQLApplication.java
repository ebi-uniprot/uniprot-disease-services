package uk.ac.ebi.uniprot.ds.graphql;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import uk.ac.ebi.uniprot.ds.common.dao.DiseaseDAO;
import uk.ac.ebi.uniprot.ds.common.dao.ProteinDAO;
import uk.ac.ebi.uniprot.ds.graphql.resolver.Query;

@SpringBootApplication
@ComponentScan(basePackages = {"uk.ac.ebi.uniprot.ds.common", "uk.ac.ebi.uniprot.ds.graphql"})
public class DiseaseGraphQLApplication {

	public static void main(String[] args) {
		SpringApplication.run(DiseaseGraphQLApplication.class, args);
	}

	@Bean
	public Query query(DiseaseDAO diseaseDAO, ProteinDAO proteinDAO){
		return new Query(diseaseDAO, proteinDAO);
	}
}
