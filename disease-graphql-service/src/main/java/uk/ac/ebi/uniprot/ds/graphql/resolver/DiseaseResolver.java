package uk.ac.ebi.uniprot.ds.graphql.resolver;

import com.coxautodev.graphql.tools.GraphQLResolver;
import uk.ac.ebi.uniprot.ds.common.model.Disease;
import uk.ac.ebi.uniprot.ds.common.model.Protein;

import java.util.List;
import java.util.stream.Collectors;

public class DiseaseResolver implements GraphQLResolver<Disease> {

    public List<Protein> getProteins(Disease disease){
        return disease.getDiseaseProteins()
                .stream()
                .map(dp -> dp.getProtein())
                .collect(Collectors.toList());
    }
}
