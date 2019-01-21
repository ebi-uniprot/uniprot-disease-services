/*
 * Created by sahmad on 1/18/19 1:57 PM
 * UniProt Consortium.
 * Copyright (c) 2002-2019.
 *
 */

package uk.ac.ebi.diseaseservice.writer;

import org.springframework.data.mongodb.core.MongoOperations;
import uk.ac.ebi.diseaseservice.model.Disease;
import uk.ac.ebi.diseaseservice.model.Protein;
import uk.ac.ebi.kraken.interfaces.uniprot.UniProtEntry;

import java.util.ArrayList;
import java.util.List;

public class DiseaseWriter extends BaseSwissProtWriter {
    private String collection;
    public DiseaseWriter(String collection){
        this.collection = collection;
    }


    @Override
    public void write(List<? extends UniProtEntry> entries){
        doWrite(entries);
    }

    protected void doWrite(List<? extends UniProtEntry> entries) {
        // get the list of proteins from entries
        for(UniProtEntry entry : entries){
            // get the protein from the uniprot entry
            Protein protein = findProteinbyProteinId(entry.getUniProtId().getValue());
            List<Disease> diseases = getDiseases(entry);
            upsertDiseases(protein, diseases);

        }
    }

    private void upsertDiseases(Protein protein, List<Disease> diseases) {
        // get the disease if exist, update the protein
        MongoOperations mongoOperations = getTemplate();
        String pId = protein.get_id();
        for(Disease disease : diseases){
            // check if the disease exist
            Disease storedDisease = findDiseaseByName(disease.getName());

            if(storedDisease != null){ // update the protein ids list
                storedDisease.getProteinIds().add(pId);
            } else {// insert the disease
                List<String> pIds = new ArrayList<>();
                pIds.add(pId);
                disease.setProteinIds(pIds);
                storedDisease = disease;
            }

            mongoOperations.save(storedDisease);
            // update protein with disease id
            updateProtein(protein, storedDisease.get_id());
        }
    }

    private void updateProtein(Protein protein, String diseaseDBId) {
        MongoOperations mongoOperations = getTemplate();
        if(protein.getDiseaseIds() == null){
            List<String> dIds = new ArrayList<>();
            dIds.add(diseaseDBId);
            protein.setDiseaseIds(dIds);
        } else {// if protein has already other disease, update it
            protein.getDiseaseIds().add(diseaseDBId);
        }
        mongoOperations.save(protein);
    }
}
