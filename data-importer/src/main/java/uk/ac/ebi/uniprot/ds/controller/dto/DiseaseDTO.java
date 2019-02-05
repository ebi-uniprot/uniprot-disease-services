/*
 * Created by sahmad on 04/02/19 12:08
 * UniProt Consortium.
 * Copyright (c) 2002-2019.
 *
 */

package uk.ac.ebi.uniprot.ds.controller.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
public class DiseaseDTO {
    private String diseaseId;
    private String diseaseName;
    private String acronym;
    private String description;
    private List<String> synonyms;
    private List<String> proteins;
    private List<String> variants;
}
