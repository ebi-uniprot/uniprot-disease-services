package uk.ac.ebi.uniprot.ds.rest.mapper;

import org.modelmapper.Converter;
import org.modelmapper.PropertyMap;
import org.modelmapper.spi.MappingContext;
import uk.ac.ebi.uniprot.ds.common.model.Disease;
import uk.ac.ebi.uniprot.ds.common.model.Protein;
import uk.ac.ebi.uniprot.ds.common.model.Synonym;
import uk.ac.ebi.uniprot.ds.rest.dto.BasicDrugDTO;
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
        using(new DiseasesToParentDiseaseDTOs()).map(source.getParents()).setParents(null);
        using(new PublicationsToPublicationDTOs()).map(source.getPublications()).setPublications(null);
        using(new ProteinsToDrugs()).map(source.getProteins()).setDrugs(null);
    }

    private class ProteinsToDrugs implements Converter<List<Protein>, List<BasicDrugDTO>> {

        @Override
        public List<BasicDrugDTO> convert(MappingContext<List<Protein>, List<BasicDrugDTO>> context) {
            List<Protein> proteins = context.getSource();
            List<BasicDrugDTO> drugs = null;

            if(proteins != null){
                // get the drugs from protein --> protein xref --> drug --> basicdrugdto
                drugs = proteins
                        .stream()
                        .filter(p -> p.getProteinCrossRefs() != null && !p.getProteinCrossRefs().isEmpty())
                        .map(p -> p.getProteinCrossRefs())
                        .flatMap(List::stream)
                        .filter(xref -> xref.getDrugs() != null && !xref.getDrugs().isEmpty())
                        .map(xref -> xref.getDrugs())
                        .flatMap(List::stream)
                        .map(d -> new BasicDrugDTO(d.getName()))
                        .collect(Collectors.toList());

            }

            return drugs;
        }
    }

    private class DiseasesToParentDiseaseDTOs implements Converter<List<Disease>, List<DiseaseDTO.ParentDiseaseDTO>>{

        @Override
        public List<DiseaseDTO.ParentDiseaseDTO> convert(MappingContext<List<Disease>, List<DiseaseDTO.ParentDiseaseDTO>> context) {
            List<Disease> children = context.getSource();
            List<DiseaseDTO.ParentDiseaseDTO> childDiseases = null;
            if(children != null) {
                childDiseases = children.stream().map(child -> new DiseaseDTO.ParentDiseaseDTO(child.getDiseaseId(), child.getName()))
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
