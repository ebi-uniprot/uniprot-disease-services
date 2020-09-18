package uk.ac.ebi.uniprot.ds.rest.mapper;

import org.apache.commons.lang3.tuple.Pair;
import org.modelmapper.Converter;
import org.modelmapper.PropertyMap;
import org.modelmapper.spi.MappingContext;
import uk.ac.ebi.uniprot.ds.common.model.*;
import uk.ac.ebi.uniprot.ds.rest.dto.DrugDTO;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class DrugToDrugDTOMap extends PropertyMap<Drug, DrugDTO> {
    
    @Override
    protected void configure() {
        using(new DrugEvidencesToEvidences()).map(source.getDrugEvidences()).setEvidences(null);
        using(new DiseaseNameToBasicDiseaseDTO()).map(source.getDiseaseProteinCount()).setDiseases(null);
    }

    private static class DrugEvidencesToEvidences implements Converter<List<DrugEvidence>, Set<String>> {
        @Override
        public Set<String> convert(MappingContext<List<DrugEvidence>, Set<String>> context) {

            List<DrugEvidence> drugEvidences = context.getSource();
            Set<String> urls = null;

            if (drugEvidences != null) {
                urls = drugEvidences.stream().map(de -> de.getRefUrl()).collect(Collectors.toSet());
            }

            return urls;
        }

    }

    private class DiseaseNameToBasicDiseaseDTO implements Converter<Set<Pair<String, Integer>>, Set<DrugDTO.BasicDiseaseDTO>> {
        @Override
        public Set<DrugDTO.BasicDiseaseDTO> convert(MappingContext<Set<Pair<String, Integer>>, Set<DrugDTO.BasicDiseaseDTO>> context) {
            Set<Pair<String, Integer>> names = context.getSource();
            Set<DrugDTO.BasicDiseaseDTO> diseases = null;
            if(names != null && !names.isEmpty()){
                diseases = names.stream().map(name -> DrugDTO.BasicDiseaseDTO.builder()
                        .diseaseName(name.getLeft()).proteinCount(name.getRight()).build())
                        .collect(Collectors.toSet());
            }
            return diseases;
        }
    }
}
