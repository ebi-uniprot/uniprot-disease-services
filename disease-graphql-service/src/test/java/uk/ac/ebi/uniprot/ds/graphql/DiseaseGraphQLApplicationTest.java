package uk.ac.ebi.uniprot.ds.graphql;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.graphql.spring.boot.test.GraphQLResponse;
import com.graphql.spring.boot.test.GraphQLTest;
import com.graphql.spring.boot.test.GraphQLTestTemplate;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;
import org.springframework.web.util.UriBuilder;
import uk.ac.ebi.uniprot.ds.common.dao.DiseaseDAO;
import uk.ac.ebi.uniprot.ds.common.dao.ProteinDAO;
import uk.ac.ebi.uniprot.ds.common.model.Disease;
import uk.ac.ebi.uniprot.ds.common.model.Publication;
import uk.ac.ebi.uniprot.ds.common.model.Synonym;
import uk.ac.ebi.uniprot.ds.graphql.model.DataServiceProtein;
import uk.ac.ebi.uniprot.ds.graphql.model.VariantSourceTypeEnum;
import uk.ac.ebi.uniprot.ds.graphql.model.Variation;
import uk.ac.ebi.uniprot.ds.graphql.model.mapper.DiseaseToDiseaseType;
import uk.ac.ebi.uniprot.ds.graphql.model.mapper.ProteinToProteinType;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@GraphQLTest
public class DiseaseGraphQLApplicationTest {
	private static final String TEST_DISEASE_ID = "Alzheimer disease";

	@Autowired
	private GraphQLTestTemplate graphQLTestTemplate;
	@MockBean
	private RestTemplate restTemplate;
	@MockBean
	private DiseaseDAO diseaseDAO;
	@MockBean
	private ProteinDAO proteinDAO;
	@Autowired
	private ModelMapper modelMapper;

	@Test
	void testGetDiseaseByDiseaseId() throws IOException {
		Disease disease = createDiseaseObject(TEST_DISEASE_ID);
		disease.setDiseaseId(TEST_DISEASE_ID);
		// mock disease api call
		Mockito.when(this.diseaseDAO.findByDiseaseId(TEST_DISEASE_ID)).thenReturn(Optional.of(disease));

		DataServiceProtein[] dataServiceProteins = new DataServiceProtein[1];
		dataServiceProteins[0] = createDataServiceProtein();
		ResponseEntity<DataServiceProtein[]> varResp = new ResponseEntity<>(dataServiceProteins, HttpStatus.OK);
		// mock variation api call.
		DefaultUriBuilderFactory uriBuilderFactory = new DefaultUriBuilderFactory("");
		Mockito.when(this.restTemplate.getUriTemplateHandler()).thenReturn(uriBuilderFactory);
		UriBuilder uriBuilder = uriBuilderFactory.builder().queryParam("disease", disease.getDiseaseId());
		Mockito.when(this.restTemplate.getForEntity(uriBuilder.build(), DataServiceProtein[].class)).thenReturn(varResp);

		ObjectNode variables = new ObjectMapper().createObjectNode();
		variables.put("diseaseId", TEST_DISEASE_ID);
		GraphQLResponse response = this.graphQLTestTemplate.perform("graphql/get-disease.graphql", variables);
		assertNotNull(response);
		assertTrue(response.isOk());
		assertEquals(TEST_DISEASE_ID, response.get("$.data.disease.diseaseId"));
		assertNotNull(response.get("$.data.disease.diseaseName"));
		assertNotNull(response.get("$.data.disease.description"));
		assertNotNull(response.get("$.data.disease.acronym"));
		assertNotNull(response.get("$.data.disease.note"));
		assertFalse(response.get("$.data.disease.isGroup", Boolean.class));
		List<Synonym> syns = response.get("$.data.disease.synonyms", List.class);
		assertNotNull(syns);
		assertFalse(syns.isEmpty());
		assertEquals(disease.getSynonyms().size(), syns.size());
		// publication
		List<Publication> pubs = response.get("$.data.disease.publications", List.class);
		assertNotNull(pubs);
		assertFalse(pubs.isEmpty());
		assertEquals(disease.getPublications().size(), pubs.size());

		// variants
		List<Variation> variants = response.get("$.data.disease.variants", List.class);
		assertNotNull(variants);
		assertFalse(variants.isEmpty());
		assertEquals(dataServiceProteins[0].getFeatures().size(), variants.size());
	}

	@TestConfiguration
	static class TestConfig{
		@Bean
		ModelMapper modelMapper() {
			ModelMapper modelMapper = new ModelMapper();
			modelMapper.addMappings(new DiseaseToDiseaseType());
			modelMapper.addMappings(new ProteinToProteinType());
			return modelMapper;
		}

	}

	private Disease createDiseaseObject(String diseaseId) {
		Disease disease = new Disease();
		String dId = diseaseId;
		String dn = "Disease Name" + diseaseId;
		String desc = "Description" + diseaseId;
		String acr = "ACRONYM-" + diseaseId;
		disease.setDiseaseId(dId);
		disease.setName(dn);
		disease.setDesc(desc);
		disease.setAcronym(acr);
		disease.setNote("Note" + diseaseId);
		// synonym
		disease.addSynonym(createSynonym(diseaseId, "1"));
		disease.addSynonym(createSynonym(diseaseId, "2"));
		disease.addSynonym(createSynonym(diseaseId, "3"));
		// publications
		disease.setPublications(Arrays.asList(createPublication("1"), createPublication("2")));
		// variant
		return disease;
	}

	DataServiceProtein createDataServiceProtein(){
		DataServiceProtein.DataServiceProteinBuilder builder = DataServiceProtein.builder();
		builder.accession("P1234").proteinName("randomProt");
		Variation var1 = createVariation("1");
		Variation var2 = createVariation("2");
		Variation var3 = createVariation("3");
		Variation var4 = createVariation("4");
		Variation var5 = createVariation("5");
		builder.features(Arrays.asList(var1, var2, var3, var4, var5));
		return builder.build();
	}

	Variation createVariation(String suffix){
		Variation.VariationBuilder builder = Variation.builder();
		builder.type("VARIANT").cvId("cvId" + suffix).ftId("VAR_1234" + suffix).description("sample description" + suffix);
		builder.alternativeSequence("M" + suffix).sourceType(VariantSourceTypeEnum.large_scale_study);
		return builder.build();
	}

	Synonym createSynonym(String diseaseId, String suffix){
		Synonym synonym = new Synonym();
		synonym.setName(diseaseId + suffix);
		synonym.setSource("source" + suffix);
		return synonym;
	}

	Publication createPublication(String suffix){
		Publication pub = new Publication();
		pub.setPubId("pubId" + suffix);
		pub.setPubType("pubType" + suffix);
		return pub;
	}

}
