package uk.ac.ebi.uniprot.ds.rest.mapper;

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
        using(new DiseaseNameToBasicDiseaseDTO()).map(source.getDiseases()).setDiseases(null);
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

    private class DiseaseNameToBasicDiseaseDTO implements Converter<Set<String>, Set<DrugDTO.BasicDiseaseDTO>> {
        @Override
        public Set<DrugDTO.BasicDiseaseDTO> convert(MappingContext<Set<String>, Set<DrugDTO.BasicDiseaseDTO>> context) {
            Set<String> names = context.getSource();
            Set<DrugDTO.BasicDiseaseDTO> diseases = null;
            if(names != null && !names.isEmpty()){
                diseases = names.stream().map(name -> DrugDTO.BasicDiseaseDTO.builder().diseaseName(name).build())
                        .collect(Collectors.toSet());
            }
            return diseases;
        }
    }
}
