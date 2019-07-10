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
    private String note;
    private List<ParentDiseaseDTO> parents;
    private List<String> synonyms;
    private List<BasicProtein> proteins;
    private List<String> variants;
    private List<PublicationDTO> publications;
    private List<String> drugs;

    @Getter
    @Setter
    public static class ParentDiseaseDTO {
        String diseaseId;
        String diseaseName;
        public ParentDiseaseDTO(String diseaseId, String diseaseName){
            this.diseaseId = diseaseId;
            this.diseaseName = diseaseName;
        }
    }

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
}