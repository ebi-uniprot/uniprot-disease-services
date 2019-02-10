/*
 * Created by sahmad on 07/02/19 12:22
 * UniProt Consortium.
 * Copyright (c) 2002-2019.
 *
 */

package uk.ac.ebi.uniprot.ds.rest.response;

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
