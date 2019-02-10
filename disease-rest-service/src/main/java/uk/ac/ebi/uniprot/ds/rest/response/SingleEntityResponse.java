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
public class SingleEntityResponse<T> extends BaseEntityResponse {
    private T result;
    public SingleEntityResponse(String requestId, Boolean hasError, List<String> warnings, T result){
        super(requestId, hasError, warnings);
        this.result = result;
    }
}
