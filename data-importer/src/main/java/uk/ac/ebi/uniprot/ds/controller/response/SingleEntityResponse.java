/*
 * Created by sahmad on 04/02/19 20:03
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
public class SingleEntityResponse<T> {
    private String requestId;
    private Boolean hasError;
    private List<String> warnings;
    private T result;
    public SingleEntityResponse(String requestId, Boolean hasError, List<String> warnings, T result){
        this.requestId = requestId;
        this.hasError = hasError;
        this.warnings = warnings;
        this.result = result;
    }
}
