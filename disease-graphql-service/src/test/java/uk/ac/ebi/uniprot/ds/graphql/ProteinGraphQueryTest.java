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
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.util.DefaultUriBuilderFactory;
import org.springframework.web.util.UriBuilder;
import uk.ac.ebi.uniprot.ds.common.model.*;
import uk.ac.ebi.uniprot.ds.graphql.model.*;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@GraphQLTest
@Import(TestConfig.class)
public class ProteinGraphQueryTest extends BaseGraphQueryTest {
    private static final String TEST_PROTEIN_ID = "P12345";

    @Autowired
    private GraphQLTestTemplate graphQLTestTemplate;

    @Test
    void testGetProteinByAccession() throws IOException {
        Protein protein = createProtein(TEST_PROTEIN_ID);
        // create disease protein
        Set<DiseaseProtein> dps = IntStream.range(1, 5)
                .mapToObj(i -> createDiseaseObject("diseaseId" + i))
                .map(disease -> new DiseaseProtein(disease, protein, false))
                .collect(Collectors.toSet());
        protein.setDiseaseProteins(dps);

        // create gene coordinates
        GeneCoordinate gc1 = createGeneCoordinate("1");
        GeneCoordinate gc2 = createGeneCoordinate("2");
        GeneCoordinate gc3 = createGeneCoordinate("3");
        protein.setGeneCoordinates(Arrays.asList(gc1, gc2, gc3));

        // create publications
        Publication pub1 = createPublication("1");
        Publication pub2 = createPublication("2");
        protein.setPublications(Arrays.asList(pub1, pub2));

        // create pathways
        ProteinCrossRef pt1 = createProteinPathways("1");
        Drug d1 = createDrugObject("1");
        Drug d2 = createDrugObject("2");
        Drug d3 = createDrugObject("3");
        pt1.setDrugs(Arrays.asList(d1, d2));
        ProteinCrossRef pt2 = createProteinPathways("2");
        pt2.setDrugs(Arrays.asList(d3, d2));
        ProteinCrossRef pt3 = createProteinPathways("3");
        pt3.setDrugs(Arrays.asList(d1, d3));
        protein.setProteinCrossRefs(Arrays.asList(pt1, pt2, pt3));

        // create interaction
        Interaction int1 = createInteraction("1");
        Interaction int2 = createInteraction("2");
        protein.setInteractions(Arrays.asList(int1, int2));

        // mock disease api call
        Mockito.when(this.proteinDAO.findProteinByAccession(TEST_PROTEIN_ID)).thenReturn(Optional.of(protein));

        DataServiceProtein dataServiceProtein = createDataServiceProtein();
        // mock variation api call.
        DefaultUriBuilderFactory uriBuilderFactory = new DefaultUriBuilderFactory("");
        Mockito.when(this.restTemplate.getUriTemplateHandler()).thenReturn(uriBuilderFactory);
        UriBuilder uriBuilder = uriBuilderFactory.builder().path(protein.getAccession());
        Mockito.when(this.restTemplate.getForObject(uriBuilder.build(), DataServiceProtein.class)).thenReturn(dataServiceProtein);

        ObjectNode variables = new ObjectMapper().createObjectNode();
        variables.put("accession", TEST_PROTEIN_ID);
        GraphQLResponse response = this.graphQLTestTemplate.perform("graphql/get-protein.graphql", variables);
        assertNotNull(response);
        assertTrue(response.isOk());
        assertEquals(TEST_PROTEIN_ID, response.get("$.data.protein.accession"));
        assertNotNull(response.get("$.data.protein.proteinId"));
        assertNotNull(response.get("$.data.protein.proteinName"));
        assertNotNull(response.get("$.data.protein.gene"));
        assertNotNull(response.get("$.data.protein.description"));
        assertFalse(response.get("$.data.protein.isExternallyMapped", Boolean.class));
        List<GeneCoordinateType> gcs = response.get("$.data.protein.geneCoordinates", List.class);
        verifyGeneCoord(protein, gcs);
        List<PublicationType> pubs = response.get("$.data.protein.publications", List.class);
        verifyPub(protein, pubs);
        List<DiseaseType> diseases = response.get("$.data.protein.diseases", List.class);
        verifyDiseases(protein, diseases);

        List<Variation> variants = response.get("$.data.protein.variants", List.class);
        verifyVariations(dataServiceProtein, variants);

        List<ProteinCrossRefType> pathways = response.get("$.data.protein.pathways", List.class);
        verifyPathways(protein, pathways);

        List<InteractionType> interactions = response.get("$.data.protein.interactions", List.class);
        verifyInteractions(protein, interactions);
    }

    private void verifyPathways(Protein protein, List<ProteinCrossRefType> pathways) {
        assertNotNull(pathways);
        assertFalse(pathways.isEmpty());
        assertEquals(protein.getProteinCrossRefs().size(), pathways.size());
    }

    private void verifyVariations(DataServiceProtein dataServiceProtein, List<Variation> variants) {
        assertNotNull(variants);
        assertFalse(variants.isEmpty());
        assertEquals(dataServiceProtein.getFeatures().size(), variants.size());
    }

    private void verifyInteractions(Protein protein, List<InteractionType> interactions) {
        assertNotNull(interactions);
        assertFalse(interactions.isEmpty());
        assertEquals(protein.getInteractions().size(), interactions.size());
    }

    private void verifyDiseases(Protein protein, List<DiseaseType> diseases) {
        assertNotNull(diseases);
        assertFalse(diseases.isEmpty());
        assertEquals(protein.getDiseaseProteins().size(), diseases.size());
    }

    private void verifyPub(Protein protein, List<PublicationType> pubs) {
        assertNotNull(pubs);
        assertFalse(pubs.isEmpty());
        assertEquals(protein.getPublications().size(), pubs.size());
    }

    private void verifyGeneCoord(Protein protein, List<GeneCoordinateType> gcs) {
        assertNotNull(gcs);
        assertFalse(gcs.isEmpty());
        assertEquals(protein.getGeneCoordinates().size(), gcs.size());
    }
}
