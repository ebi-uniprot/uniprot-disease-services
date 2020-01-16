package uk.ac.ebi.uniprot.ds.graphql.model.mapper;

import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.modelmapper.spi.MappingContext;
import uk.ac.ebi.uniprot.ds.common.model.DiseaseProtein;
import uk.ac.ebi.uniprot.ds.common.model.Protein;
import uk.ac.ebi.uniprot.ds.graphql.model.DiseaseType;
import uk.ac.ebi.uniprot.ds.graphql.model.ProteinType;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ProteinToProteinType extends PropertyMap<Protein, ProteinType> {
    @Override
    protected void configure() {
        using(new DiseaseProteinsToDiseases()).map(source.getDiseaseProteins()).setDiseases(null);
    }

    private static class DiseaseProteinsToDiseases implements Converter<Set<DiseaseProtein>, List<DiseaseType>> {
        @Override
        public List<DiseaseType> convert(MappingContext<Set<DiseaseProtein>, List<DiseaseType>> context) {
            Set<DiseaseProtein> diseaseProteins = context.getSource();
            return diseaseProteins != null && !diseaseProteins.isEmpty() ? diseaseProteins
                    .stream()
                    .map(dp -> dp.getDisease())
                    .map(d -> new ModelMapper().map(d, DiseaseType.class))
                    .collect(Collectors.toList())
                    : null;
        }
    }
}
