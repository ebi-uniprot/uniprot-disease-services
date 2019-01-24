/*
 * Created by sahmad on 1/24/19 12:23 PM
 * UniProt Consortium.
 * Copyright (c) 2002-2019.
 *
 */

package uk.ac.ebi.uniprot.ds.exception;

public class DiseaseNotFoundException extends AssetNotFoundException {
    public DiseaseNotFoundException(String message){
        super(message);
    }
}
