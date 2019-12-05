package uk.ac.ebi.uniprot.ds.rest.mapper;

import org.modelmapper.Converter;
import org.modelmapper.PropertyMap;
import org.modelmapper.spi.MappingContext;
import uk.ac.ebi.uniprot.ds.common.model.Disease;
import uk.ac.ebi.uniprot.ds.common.model.DiseaseProtein;
import uk.ac.ebi.uniprot.ds.common.model.Drug;
import uk.ac.ebi.uniprot.ds.common.model.Synonym;
import uk.ac.ebi.uniprot.ds.rest.dto.DiseaseDTO;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class DiseaseToDiseaseDTOMap extends PropertyMap<Disease, DiseaseDTO> {
    @Override
    protected void configure() {
        map().setDescription(source.getDesc());
        map().setDiseaseName(source.getName());
        map().setNote(source.getNote());
        map().setChildren(mapAll(source.getChildren()));
        using(context -> "MONDO".equals(context.getSource())).map(source.getSource()).setIsGroup(null);
        using(new DisProtsToProtAccessions()).map(source.getDiseaseProteins()).setProteins(null);
        using(new SynonymsToNames()).map(source.getSynonyms()).setSynonyms(null);
        using(new VariantsToFeatureIdsConverter()).map(source.getVariants()).setVariants(null);
        using(new PublicationsToPublicationDTOs()).map(source.getPublications()).setPublications(null);
        using(new DisProtsToDrugs()).map(source.getDiseaseProteins()).setDrugs(null);
    }

    private static class DisProtsToDrugs implements Converter<Set<DiseaseProtein>, List<String>> {

        @Override
        public List<String> convert(MappingContext<Set<DiseaseProtein>, List<String>> context) {
            Set<DiseaseProtein> disProts = context.getSource();
            List<String> drugNames = null;

            if(disProts != null && !disProts.isEmpty()){
                // get the drugs from protein --> protein xref --> drug
                Set<Drug> drugs = disProts
                        .stream()
                        .filter(dp -> dp.getProtein().getProteinCrossRefs() != null && !dp.getProtein().getProteinCrossRefs().isEmpty())
                        .map(dp -> dp.getProtein().getProteinCrossRefs())
                        .flatMap(List::stream)
                        .filter(xref -> xref.getDrugs() != null && !xref.getDrugs().isEmpty())
                        .map(xref -> xref.getDrugs())
                        .flatMap(List::stream)
                        .collect(Collectors.toSet());

                if(drugs != null && !drugs.isEmpty()) { // drug --> name
                    // get just the name
                    drugNames = drugs.stream().map(d -> d.getName()).collect(Collectors.toList());
                }

            }

            return drugNames;
        }
    }

    private static class SynonymsToNames implements Converter<List<Synonym>, List<String>> {
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

    private static class DisProtsToProtAccessions implements Converter<Set<DiseaseProtein>, List<DiseaseDTO.BasicProtein>> {

        @Override
        public List<DiseaseDTO.BasicProtein> convert(MappingContext<Set<DiseaseProtein>, List<DiseaseDTO.BasicProtein>> context) {
            Set<DiseaseProtein> disProts = context.getSource();
            return disProts != null && !disProts.isEmpty() ? disProts
                    .stream()
                    .map(dp -> new DiseaseDTO.BasicProtein(dp.getProtein().getAccession(), dp.isMapped()))
                    .collect(Collectors.toList())
                    : null;
        }
    }

    public static List<DiseaseDTO> mapAll(final List<Disease> entityList) {
        List<DiseaseDTO> result = null;
        if(entityList != null) {
            result = entityList.stream()
                    .map(entity -> map(entity, DiseaseDTO.class))
                    .collect(Collectors.toList());
        }

        return result;
    }

    public static DiseaseDTO map(Disease entity, Class<DiseaseDTO> outClass) {
        return map(entity, outClass);
    }
}
