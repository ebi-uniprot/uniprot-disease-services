/*
 * Created by sahmad on 06/02/19 09:34
 * UniProt Consortium.
 * Copyright (c) 2002-2019.
 *
 */

package uk.ac.ebi.uniprot.ds.controller.response;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public abstract class BaseEntityResponse {
    private String requestId;
    private Boolean hasError;
    private List<String> warnings;

    public BaseEntityResponse(String requestId, Boolean hasError, List<String> warnings){
        this.requestId = requestId;
        this.hasError = hasError;
        this.warnings = warnings;
    }
}
