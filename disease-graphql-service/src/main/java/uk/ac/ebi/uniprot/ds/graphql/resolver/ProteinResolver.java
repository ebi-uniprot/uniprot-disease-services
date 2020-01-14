package uk.ac.ebi.uniprot.ds.graphql.resolver;

import com.coxautodev.graphql.tools.GraphQLResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;
import org.springframework.web.util.UriBuilder;
import uk.ac.ebi.uniprot.ds.common.model.dataservice.Variation;
import uk.ac.ebi.uniprot.ds.common.model.Protein;

import java.util.List;

@Service
public class ProteinResolver implements GraphQLResolver<Protein> {

    @Autowired
    private RestTemplate restTemplate;

    public List<Variation> variations(Protein protein){
        DefaultUriBuilderFactory handler = (DefaultUriBuilderFactory) this.restTemplate.getUriTemplateHandler();
        UriBuilder uriBuilder = handler.builder().path(protein.getAccession());
        DataServiceProtein response = this.restTemplate.getForObject(uriBuilder.build(), DataServiceProtein.class);
        return response.getFeatures();
    }
}
