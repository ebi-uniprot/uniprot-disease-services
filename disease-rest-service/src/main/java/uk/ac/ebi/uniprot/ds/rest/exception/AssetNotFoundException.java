/*
 * Created by sahmad on 07/02/19 12:19
 * UniProt Consortium.
 * Copyright (c) 2002-2019.
 *
 */

package uk.ac.ebi.uniprot.ds.rest.exception;

public class AssetNotFoundException extends RuntimeException {
    public AssetNotFoundException(String message){
        super(message);
    }
}
