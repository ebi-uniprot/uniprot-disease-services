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
public class MultipleEntityResponse<T> extends BaseEntityResponse {
    private Integer offset;
    private Integer maxReturn;
    private Integer total;
    private List<T> results;


    public MultipleEntityResponse(String requestId, Boolean hasError, List<String> warnings, List<T> results,
                                  Integer offset, Integer maxReturn, Integer total){
        super(requestId, hasError, warnings);
        this.results = results;
        this.offset = offset;
        this.maxReturn = maxReturn;
        this.total = total;
    }

    public MultipleEntityResponse(String requestId, List<T> results){
        this(requestId, false, null, results, null, null, null);
    }

    public MultipleEntityResponse(String requestId, List<T> results, Integer offset, Integer maxReturn){
        this(requestId, false, null, results, offset, maxReturn, null);
    }
}
