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
public class ProteinWithCrossRefsDTO {
    private String accession;
    private String proteinId;
    private String proteinName;
    private String gene;
    private List<ProteinCrossRefDTO> xrefs;
}
