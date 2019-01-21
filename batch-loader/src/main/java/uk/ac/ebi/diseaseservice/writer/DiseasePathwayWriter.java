/*
 * Created by sahmad on 21/01/19 14:42
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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DiseasePathwayWriter extends BaseSwissProtWriter {
    @Override
    protected void doWrite(List<? extends UniProtEntry> entries) {
        MongoOperations mongoOperations = getTemplate();
        for (UniProtEntry entry : entries) {
            Protein protein = findProteinbyProteinId(entry.getUniProtId().getValue());
            List<Disease> diseases = getDiseases(entry);
            // update pathways ids for each disease
            for (Disease d : diseases) {
                Disease storedDisease = findDiseaseByName(d.getName());
                List<String> uniqueIds = mergeTwoLists(storedDisease.getPathwayIds(), protein.getPathwayIds());
                storedDisease.setPathwayIds(uniqueIds);
                mongoOperations.save(storedDisease);
            }
        }
    }
}
