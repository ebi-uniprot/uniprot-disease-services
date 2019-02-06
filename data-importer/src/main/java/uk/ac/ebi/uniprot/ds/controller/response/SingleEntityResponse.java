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
public class SingleEntityResponse<T> extends BaseEntityResponse {
    private T result;
    public SingleEntityResponse(String requestId, Boolean hasError, List<String> warnings, T result){
        super(requestId, hasError, warnings);
        this.result = result;
    }
}
