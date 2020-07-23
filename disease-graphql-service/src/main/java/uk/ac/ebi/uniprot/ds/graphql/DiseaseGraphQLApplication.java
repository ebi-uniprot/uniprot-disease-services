package uk.ac.ebi.uniprot.ds.graphql;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.util.DefaultUriBuilderFactory;
import uk.ac.ebi.uniprot.ds.graphql.model.mapper.DiseaseToDiseaseType;
import uk.ac.ebi.uniprot.ds.graphql.model.mapper.ProteinToProteinType;

@SpringBootApplication
@ComponentScan(basePackages = {"uk.ac.ebi.uniprot.ds.common", "uk.ac.ebi.uniprot.ds.graphql"})
public class DiseaseGraphQLApplication {
	@Value(("${variation.api}"))
	private String variationAPI;

	public static void main(String[] args) {
		SpringApplication.run(DiseaseGraphQLApplication.class, args);
	}


	@Bean
	public RestTemplate restTemplate(){
		RestTemplate restTemplate = new RestTemplate();
		restTemplate.setUriTemplateHandler(new DefaultUriBuilderFactory(variationAPI));
		return restTemplate;
	}

	@Bean
	ModelMapper modelMapper() {
		ModelMapper modelMapper = new ModelMapper();
		modelMapper.addMappings(new DiseaseToDiseaseType());
		modelMapper.addMappings(new ProteinToProteinType());
		return modelMapper;
	}

	static final String ALLOW_ALL_ORIGINS = "*";
	@Bean
	public WebMvcConfigurer corsConfigurer() {
			return new WebMvcConfigurer() {
			@Override
			public void addCorsMappings(CorsRegistry registry) {
				registry.addMapping("/graphql/**").allowedOrigins(ALLOW_ALL_ORIGINS);
				registry.addMapping("/graphql/*").allowedOrigins(ALLOW_ALL_ORIGINS);
			}
		};
	}
}
