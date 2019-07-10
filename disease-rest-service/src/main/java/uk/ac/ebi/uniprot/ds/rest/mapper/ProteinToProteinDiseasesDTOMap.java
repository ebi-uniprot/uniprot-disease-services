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
import uk.ac.ebi.uniprot.ds.common.model.DiseaseProtein;
import uk.ac.ebi.uniprot.ds.rest.dto.DiseaseDTO;
import uk.ac.ebi.uniprot.ds.rest.dto.ProteinDiseasesDTO;
import uk.ac.ebi.uniprot.ds.common.model.Disease;
import uk.ac.ebi.uniprot.ds.common.model.Protein;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ProteinToProteinDiseasesDTOMap extends PropertyMap<Protein, ProteinDiseasesDTO> {

    @Override
    protected void configure() {
        using(new DisProteinsToDiseaseDTOs()).map(source.getDiseaseProteins()).setDiseases(null);

    }

    private static class DisProteinsToDiseaseDTOs implements Converter<Set<DiseaseProtein>, List<ProteinDiseasesDTO.BasicDiseaseDTO>>{
        @Override
        public List<ProteinDiseasesDTO.BasicDiseaseDTO> convert(MappingContext<Set<DiseaseProtein>, List<ProteinDiseasesDTO.BasicDiseaseDTO>> context) {

            Set<DiseaseProtein> disProts = context.getSource();
            List<ProteinDiseasesDTO.BasicDiseaseDTO> diseaseDTOs = null;

            if(disProts != null && !disProts.isEmpty()){
                diseaseDTOs = disProts.stream()
                        .map(dp -> new ProteinDiseasesDTO.BasicDiseaseDTO(dp.getDisease().getDiseaseId(), dp.getDisease().getAcronym()))
                        .collect(Collectors.toList());
            }

            return diseaseDTOs;
        }
    }
}
