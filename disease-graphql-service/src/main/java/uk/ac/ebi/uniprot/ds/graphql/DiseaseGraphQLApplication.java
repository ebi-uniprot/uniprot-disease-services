package uk.ac.ebi.uniprot.ds.graphql;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"uk.ac.ebi.uniprot.ds.common", "uk.ac.ebi.uniprot.ds.graphql"})
public class DiseaseGraphQLApplication {

	public static void main(String[] args) {
		SpringApplication.run(DiseaseGraphQLApplication.class, args);
	}
}
