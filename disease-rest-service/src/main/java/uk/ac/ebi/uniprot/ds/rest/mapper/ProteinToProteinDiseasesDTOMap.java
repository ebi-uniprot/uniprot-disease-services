/*
 * Created by sahmad on 07/02/19 12:22
 * UniProt Consortium.
 * Copyright (c) 2002-2019.
 *
 */

package uk.ac.ebi.uniprot.ds.rest.mapper;

import org.modelmapper.Converter;
import org.modelmapper.PropertyMap;
import org.modelmapper.spi.MappingContext;
import uk.ac.ebi.uniprot.ds.rest.dto.DiseaseDTO;
import uk.ac.ebi.uniprot.ds.rest.dto.ProteinDiseasesDTO;
import uk.ac.ebi.uniprot.ds.common.model.Disease;
import uk.ac.ebi.uniprot.ds.common.model.Protein;

import java.util.List;
import java.util.stream.Collectors;

public class ProteinToProteinDiseasesDTOMap extends PropertyMap<Protein, ProteinDiseasesDTO> {

    @Override
    protected void configure() {
        using(new DiseasesToDiseaseDTOs()).map(source.getDiseases()).setDiseases(null);

    }

    private class DiseasesToDiseaseDTOs implements Converter<List<Disease>, List<ProteinDiseasesDTO.BasicDiseaseDTO>>{
        @Override
        public List<ProteinDiseasesDTO.BasicDiseaseDTO> convert(MappingContext<List<Disease>, List<ProteinDiseasesDTO.BasicDiseaseDTO>> context) {

            List<Disease> diseases = context.getSource();
            List<ProteinDiseasesDTO.BasicDiseaseDTO> diseaseDTOs = null;

            if(diseases != null){
                diseaseDTOs = diseases.stream()
                        .map(disease -> new ProteinDiseasesDTO.BasicDiseaseDTO(disease.getDiseaseId(), disease.getAcronym()))
                        .collect(Collectors.toList());
            }

            return diseaseDTOs;
        }
    }
}
