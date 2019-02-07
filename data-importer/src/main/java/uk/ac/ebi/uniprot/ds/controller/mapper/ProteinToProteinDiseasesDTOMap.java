/*
 * Created by sahmad on 06/02/19 19:34
 * UniProt Consortium.
 * Copyright (c) 2002-2019.
 *
 */

package uk.ac.ebi.uniprot.ds.controller.mapper;

import org.modelmapper.Converter;
import org.modelmapper.PropertyMap;
import org.modelmapper.spi.MappingContext;
import uk.ac.ebi.uniprot.ds.controller.dto.DiseaseDTO;
import uk.ac.ebi.uniprot.ds.controller.dto.ProteinDiseasesDTO;
import uk.ac.ebi.uniprot.ds.model.Disease;
import uk.ac.ebi.uniprot.ds.model.Protein;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ProteinToProteinDiseasesDTOMap extends PropertyMap<Protein, ProteinDiseasesDTO> {

    @Override
    protected void configure() {
        using(new DiseasesToDiseaseDTOs()).map(source.getDiseases()).setDiseases(null);

    }

    private class DiseasesToDiseaseDTOs implements Converter<Set<Disease>, List<ProteinDiseasesDTO.BasicDiseaseDTO>>{

        @Override
        public List<ProteinDiseasesDTO.BasicDiseaseDTO> convert(MappingContext<Set<Disease>, List<ProteinDiseasesDTO.BasicDiseaseDTO>> context) {

            Set<Disease> diseases = context.getSource();
            List<ProteinDiseasesDTO.BasicDiseaseDTO> diseaseDTOs = null;

            if(diseases != null){
                diseaseDTOs = diseases.stream()
                        .map(disease -> new ProteinDiseasesDTO.BasicDiseaseDTO(disease.getDiseaseId(), disease.getAcronym()))
                        .collect(Collectors.toList());
            }

            return diseaseDTOs;
        }
    }

    private DiseaseDTO convertToDiseaseDTO(Disease disease) {
        DiseaseDTO dto = new DiseaseDTO();
        dto.setDiseaseId(disease.getDiseaseId());
        dto.setAcronym(disease.getAcronym());
        return dto;
    }
}
