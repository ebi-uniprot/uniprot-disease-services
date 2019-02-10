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
public class ProteinDTO {
    private String proteinId;
    private String proteinName;
    private String accession;
    private String gene;
    private String description;
    private List<String>  pathways;
    private List<String> interactions;
    private List<String> variants;
    private List<String> diseases;
}
