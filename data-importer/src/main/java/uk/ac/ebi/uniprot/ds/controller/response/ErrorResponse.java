/*
 * Created by sahmad on 05/02/19 09:20
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
public class ErrorResponse extends BaseEntityResponse{
    private String errorMessage;
    private Integer errorCode;

    public ErrorResponse(String requestId, List<String> warning, String errorMessage, Integer errorCode){
        super(requestId, true, warning);
        this.errorMessage = errorMessage;
        this.errorCode = errorCode;
    }

}
