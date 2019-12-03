/*
 * Created by sahmad on 07/02/19 12:20
 * UniProt Consortium.
 * Copyright (c) 2002-2019.
 *
 */

package uk.ac.ebi.uniprot.ds.rest.dto;

import lombok.Getter;
import lombok.Setter;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import uk.ac.ebi.uniprot.ds.common.model.Disease;

import java.util.List;

@Getter
@Setter
public class DiseaseDTO {
    private String diseaseId;
    private String diseaseName;
    private String acronym;
    private String description;
    private String note;
    private List<DiseaseDTO> children;
    private List<String> synonyms;
    private List<BasicProtein> proteins;
    private List<String> variants;
    private List<PublicationDTO> publications;
    private List<String> drugs;

    @Getter
    @Setter
    public static class BasicProtein{
        String accession;
        Boolean isExternallyMapped;
        public BasicProtein(String accession, Boolean isExternallyMapped){
            this.accession = accession;
            this.isExternallyMapped = isExternallyMapped;
        }
    }

    public static List<DiseaseDTO> toDiseaseDTOList(List<Disease> from, ModelMapper modelMapper){
        return modelMapper.map(from, new TypeToken<List<DiseaseDTO>>(){}.getType());
    }
}