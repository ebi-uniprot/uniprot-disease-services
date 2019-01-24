/*
 * Created by sahmad on 1/24/19 12:21 PM
 * UniProt Consortium.
 * Copyright (c) 2002-2019.
 *
 */

package uk.ac.ebi.uniprot.ds.exception;

public class AssetNotFoundException extends RuntimeException {
    public AssetNotFoundException(String message){
        super(message);
    }
}
