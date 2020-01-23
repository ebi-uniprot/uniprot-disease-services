package uk.ac.ebi.uniprot.ds.graphql;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.graphql.spring.boot.test.GraphQLResponse;
import com.graphql.spring.boot.test.GraphQLTest;
import com.graphql.spring.boot.test.GraphQLTestTemplate;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.util.DefaultUriBuilderFactory;
import org.springframework.web.util.UriBuilder;
import uk.ac.ebi.uniprot.ds.common.model.Disease;
import uk.ac.ebi.uniprot.ds.common.model.Publication;
import uk.ac.ebi.uniprot.ds.common.model.Synonym;
import uk.ac.ebi.uniprot.ds.graphql.model.DataServiceProtein;
import uk.ac.ebi.uniprot.ds.graphql.model.Variation;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@GraphQLTest
@Import(TestConfig.class)
public class DiseaseGraphQueryTest extends BaseGraphQueryTest{
	private static final String TEST_DISEASE_ID = "Alzheimer disease";

	@Autowired
	private GraphQLTestTemplate graphQLTestTemplate;

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
}
