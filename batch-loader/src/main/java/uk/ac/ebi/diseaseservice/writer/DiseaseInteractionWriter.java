/*
 * Created by sahmad on 21/01/19 14:16
 * UniProt Consortium.
 * Copyright (c) 2002-2019.
 *
 */

package uk.ac.ebi.diseaseservice.writer;

import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import uk.ac.ebi.diseaseservice.model.Disease;
import uk.ac.ebi.diseaseservice.model.Protein;
import uk.ac.ebi.kraken.interfaces.uniprot.UniProtEntry;
import uk.ac.ebi.kraken.interfaces.uniprot.comments.Interaction;
import uk.ac.ebi.kraken.interfaces.uniprot.comments.InteractionType;
import uk.ac.ebi.kraken.interfaces.uniprot.comments.InteractorAccession;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class DiseaseInteractionWriter extends BaseSwissProtWriter {
    @Override
    protected void doWrite(List<? extends UniProtEntry> entries) {
        MongoOperations mongoOperations = getTemplate();
        for (UniProtEntry entry : entries) {
            Protein protein = findProteinbyProteinId(entry.getUniProtId().getValue());

            List<Disease> diseases = getDiseases(entry);
            // update interactions for each disease
            for (Disease d : diseases) {
                Disease storedDisease = findDiseaseByName(d.getName());
                List<String> uniqueIds = mergeTwoLists(storedDisease.getInteractionIds(), protein.getInteractionIds());
                storedDisease.setInteractionIds(uniqueIds);
                mongoOperations.save(storedDisease);
            }
        }
    }
}
