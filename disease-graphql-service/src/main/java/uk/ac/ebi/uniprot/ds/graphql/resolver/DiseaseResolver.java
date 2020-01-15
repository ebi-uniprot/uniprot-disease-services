package uk.ac.ebi.uniprot.ds.graphql.resolver;

import com.coxautodev.graphql.tools.GraphQLResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;
import org.springframework.web.util.UriBuilder;
import uk.ac.ebi.uniprot.ds.common.model.Disease;
import uk.ac.ebi.uniprot.ds.common.model.dataservice.Variation;
import uk.ac.ebi.uniprot.ds.graphql.model.DataServiceProtein;

import java.util.ArrayList;
import java.util.List;

@Service
public class DiseaseResolver implements GraphQLResolver<Disease> {

    @Autowired
    private RestTemplate restTemplate;

    public List<Variation> variations(Disease disease){
        // get variations by diseaseId
        DefaultUriBuilderFactory handler = (DefaultUriBuilderFactory) this.restTemplate.getUriTemplateHandler();
        UriBuilder uriBuilder = handler.builder().queryParam("disease", disease.getDiseaseId());
        ResponseEntity<DataServiceProtein[]> response = this.restTemplate.getForEntity(uriBuilder.build(), DataServiceProtein[].class);
        DataServiceProtein[] dataServiceProteins = response.getBody();
        List<Variation> variations = null;

        for(DataServiceProtein dsp : dataServiceProteins){
            if(variations == null){
                variations = new ArrayList<>();
            }

            variations.addAll(dsp.getFeatures());
        }
        return variations;
    }
}
