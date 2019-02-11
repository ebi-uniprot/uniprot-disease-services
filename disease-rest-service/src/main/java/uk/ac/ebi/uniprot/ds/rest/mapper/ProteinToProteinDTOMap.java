package uk.ac.ebi.uniprot.ds.rest.mapper;

import org.modelmapper.Converter;
import org.modelmapper.PropertyMap;
import org.modelmapper.spi.MappingContext;
import uk.ac.ebi.uniprot.ds.common.model.Disease;
import uk.ac.ebi.uniprot.ds.common.model.Interaction;
import uk.ac.ebi.uniprot.ds.common.model.Pathway;
import uk.ac.ebi.uniprot.ds.common.model.Protein;
import uk.ac.ebi.uniprot.ds.rest.dto.ProteinDTO;

import java.util.List;
import java.util.stream.Collectors;

public class ProteinToProteinDTOMap extends PropertyMap<Protein, ProteinDTO> {
    @Override
    protected void configure() {
        map().setDescription(source.getDesc());
        using(new VariantsToFeatureIdsConverter()).map(source.getVariants()).setVariants(null);
        using(new InteractionsToAccessionsConverter()).map(source.getInteractions()).setInteractions(null);
        using(new DiseaseToDiseaseIdConverter()).map(source.getDiseases()).setDiseases(null);
        using(new PathwaysToPrimaryIds()).map(source.getPathways()).setPathways(null);
    }

    private class InteractionsToAccessionsConverter implements Converter<List<Interaction>, List<String>> {
        @Override
        public List<String> convert(MappingContext<List<Interaction>, List<String>> context) {
            List<Interaction> ints = context.getSource();
            List<String> intsStr = null;
            if (ints != null) {
                intsStr = ints.stream().map(in -> in.getAccession()).collect(Collectors.toList());
            }
            return intsStr;
        }
    }

    private class DiseaseToDiseaseIdConverter implements Converter<List<Disease>, List<String>> {
        @Override
        public List<String> convert(MappingContext<List<Disease>, List<String>> context) {
            List<Disease> diseases = context.getSource();

            List<String> diseaseNames = null;
            if (diseases != null) {
                diseaseNames = diseases.stream().map(dis -> dis.getDiseaseId()).collect(Collectors.toList());
            }

            return diseaseNames;
        }
    }

    private class PathwaysToPrimaryIds implements Converter<List<Pathway>, List<String>> {
        @Override
        public List<String> convert(MappingContext<List<Pathway>, List<String>> context) {
            List<Pathway> ints = context.getSource();
            List<String> intsStr = null;
            if (ints != null) {
                intsStr = ints.stream().map(in -> in.getPrimaryId()).collect(Collectors.toList());
            }
            return intsStr;
        }
    }
}
