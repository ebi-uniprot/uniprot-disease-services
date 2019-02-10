/*
 * Created by sahmad on 07/02/19 12:20
 * UniProt Consortium.
 * Copyright (c) 2002-2019.
 *
 */

package uk.ac.ebi.uniprot.ds.rest.dto;

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
