package uk.ac.ebi.uniprot.ds.graphql.model.mapper;

import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.modelmapper.spi.MappingContext;
import uk.ac.ebi.uniprot.ds.common.model.Disease;
import uk.ac.ebi.uniprot.ds.common.model.DiseaseProtein;
import uk.ac.ebi.uniprot.ds.graphql.model.DiseaseType;
import uk.ac.ebi.uniprot.ds.graphql.model.ProteinType;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class DiseaseToDiseaseType extends PropertyMap<Disease, DiseaseType> {
    private static final String MONDO_STR = "MONDO";

    @Override
    protected void configure() {
        map().setDescription(source.getDesc());
        map().setDiseaseName(source.getName());
        using(new DiseaseProteinsToProteins()).map(source.getDiseaseProteins()).setProteins(null);
        using(context -> MONDO_STR.equals(context.getSource())).map(source.getSource()).setIsGroup(null);
    }

    private class DiseaseProteinsToProteins implements Converter<Set<DiseaseProtein>, List<ProteinType>> {
        @Override
        public List<ProteinType> convert(MappingContext<Set<DiseaseProtein>, List<ProteinType>> context) {
            Set<DiseaseProtein> diseaseProteins = context.getSource();
            return diseaseProteins != null && !diseaseProteins.isEmpty() ? diseaseProteins
                    .stream()
                    .map(DiseaseProtein::getProtein)
                    .map(p -> new ModelMapper().map(p, ProteinType.class))
                    .collect(Collectors.toList())
                    : null;
        }
    }
}
