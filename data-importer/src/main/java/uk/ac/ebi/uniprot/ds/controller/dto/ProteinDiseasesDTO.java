/*
 * Created by sahmad on 06/02/19 19:30
 * UniProt Consortium.
 * Copyright (c) 2002-2019.
 *
 */

package uk.ac.ebi.uniprot.ds.controller.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ProteinDiseasesDTO extends ProteinXYZDTO {
    private List<BasicDiseaseDTO> diseases;

    @Getter
    @Setter
    public static class BasicDiseaseDTO {
        String diseaseId;
        String acronym;
        public BasicDiseaseDTO(String diseaseId, String acronym){
            this.diseaseId = diseaseId;
            this.acronym = acronym;
        }
    }
}
