/*
 * Created by sahmad on 04/02/19 22:46
 * UniProt Consortium.
 * Copyright (c) 2002-2019.
 *
 */

package uk.ac.ebi.uniprot.ds.controller.filter;

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
