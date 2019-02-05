/*
 * Created by sahmad on 05/02/19 09:20
 * UniProt Consortium.
 * Copyright (c) 2002-2019.
 *
 */

package uk.ac.ebi.uniprot.ds.controller.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ErrorResponse {
    private String requestId;
    private Boolean hasError = true;
    private String errorMessage;
    private Integer errorCode;

    public ErrorResponse(String requestId, String errorMessage, Integer errorCode){
        this.requestId = requestId;
        this.errorMessage = errorMessage;
        this.errorCode = errorCode;
    }

}
