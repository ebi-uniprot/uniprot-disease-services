package uk.ac.ebi.uniprot.ds.rest.mapper;

import org.modelmapper.Converter;
import org.modelmapper.PropertyMap;
import org.modelmapper.spi.MappingContext;
import uk.ac.ebi.uniprot.ds.common.model.Disease;
import uk.ac.ebi.uniprot.ds.common.model.Protein;
import uk.ac.ebi.uniprot.ds.common.model.Synonym;
import uk.ac.ebi.uniprot.ds.rest.dto.DiseaseDTO;

import java.util.List;
import java.util.stream.Collectors;

public class DiseaseToDiseaseDTOMap extends PropertyMap<Disease, DiseaseDTO> {
    @Override
    protected void configure() {
        map().setDescription(source.getDesc());
        map().setDiseaseName(source.getName());
        using(new ProteinsToAccessions()).map(source.getProteins()).setProteins(null);
        using(new SynonymsToNames()).map(source.getSynonyms()).setSynonyms(null);
        using(new VariantsToFeatureIdsConverter()).map(source.getVariants()).setVariants(null);
        using(new DiseasesToChildDiseaseDTOs()).map(source.getChildren()).setChildren(null);
    }

    private class DiseasesToChildDiseaseDTOs implements Converter<List<Disease>, List<DiseaseDTO.ChildDiseaseDTO>>{

        @Override
        public List<DiseaseDTO.ChildDiseaseDTO> convert(MappingContext<List<Disease>, List<DiseaseDTO.ChildDiseaseDTO>> context) {
            List<Disease> children = context.getSource();
            List<DiseaseDTO.ChildDiseaseDTO> childDiseases = null;
            if(children != null) {
                childDiseases = children.stream().map(child -> new DiseaseDTO.ChildDiseaseDTO(child.getDiseaseId(), child.getName()))
                        .collect(Collectors.toList());
            }

            return childDiseases;
        }
    }

    private class SynonymsToNames implements Converter<List<Synonym>, List<String>> {
        @Override
        public List<String> convert(MappingContext<List<Synonym>, List<String>> ctx) {
            List<Synonym> syns = ctx.getSource();
            List<String> synsStr = null;
            if (syns != null) {
                synsStr = syns.stream().map(syn -> syn.getName()).collect(Collectors.toList());
            }
            return synsStr;
        }
    }

    private class ProteinsToAccessions implements Converter<List<Protein>, List<String>> {

        @Override
        public List<String> convert(MappingContext<List<Protein>, List<String>> context) {
            List<Protein> proteins = context.getSource();
            return proteins != null ? proteins.stream().map(pr -> pr.getAccession()).collect(Collectors.toList()) : null;
        }
    }
}
