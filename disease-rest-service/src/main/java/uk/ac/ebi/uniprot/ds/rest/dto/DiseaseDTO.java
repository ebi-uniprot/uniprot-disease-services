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
public class DiseaseDTO {
    private String diseaseId;
    private String diseaseName;
    private String acronym;
    private String description;
    private List<ChildDiseaseDTO> children;
    private List<String> synonyms;
    private List<String> proteins;
    private List<String> variants;

    @Getter
    @Setter
    public static class ChildDiseaseDTO {
        String diseaseId;
        String diseaseName;
        public ChildDiseaseDTO(String diseaseId, String diseaseName){
            this.diseaseId = diseaseId;
            this.diseaseName = diseaseName;
        }
    }
}
