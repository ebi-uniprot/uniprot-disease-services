/*
 * Created by sahmad on 07/02/19 12:22
 * UniProt Consortium.
 * Copyright (c) 2002-2019.
 *
 */

package uk.ac.ebi.uniprot.ds.rest.filter;

public class RequestCorrelation {

    public static final String CORRELATION_ID_HEADER = "correlationId";


    private static final ThreadLocal<String> id = new ThreadLocal<>();


    public static void setCorrelationId(String correlationId) {
        id.set(correlationId);
    }

    public static String getCorrelationId() {
        return id.get();
    }
}
