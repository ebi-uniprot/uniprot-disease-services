/*
 * Created by sahmad on 06/02/19 09:32
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
public class MultipleEntityResponse<T> extends BaseEntityResponse {
    private List<T> results;

    public MultipleEntityResponse(String requestId, Boolean hasError, List<String> warnings, List<T> results){
        super(requestId, hasError, warnings);
        this.results = results;
    }

    public MultipleEntityResponse(String requestId, List<T> results){
        this(requestId, false, null, results);
    }
}
