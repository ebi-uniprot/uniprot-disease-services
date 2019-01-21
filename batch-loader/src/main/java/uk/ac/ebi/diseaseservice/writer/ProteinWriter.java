/*
 * Created by sahmad on 1/18/19 12:14 PM
 * UniProt Consortium.
 * Copyright (c) 2002-2019.
 *
 */

package uk.ac.ebi.diseaseservice.writer;

import org.springframework.data.mongodb.core.MongoOperations;
import uk.ac.ebi.diseaseservice.model.Protein;
import uk.ac.ebi.kraken.interfaces.uniprot.UniProtEntry;
import java.util.List;

public class ProteinWriter extends BaseSwissProtWriter {

    private String collection;

    public ProteinWriter(String collection){
        this.collection = collection;
    }

    protected void doWrite(List<? extends UniProtEntry> entries) {
        // get the list of proteins from entries
        MongoOperations template = getTemplate();
        for(UniProtEntry entry : entries){
            // get the protein from the uniprot entry
            Protein protein = getProtein(entry);
            // save the protein
            template.save(protein);
        }

    }
}
