/*
 * Created by sahmad on 06/02/19 19:32
 * UniProt Consortium.
 * Copyright (c) 2002-2019.
 *
 */

package uk.ac.ebi.uniprot.ds.controller.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProteinXYZDTO {
    private String accession;
    private String proteinId;
    private String proteinName;
    private String gene;
}
