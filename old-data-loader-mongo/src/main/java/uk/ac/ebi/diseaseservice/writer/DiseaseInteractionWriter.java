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
import uk.ac.ebi.diseaseservice.model.Interaction;
import uk.ac.ebi.diseaseservice.model.Protein;
import uk.ac.ebi.kraken.interfaces.uniprot.UniProtEntry;

import java.util.ArrayList;
import java.util.List;

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
                updateInteractionsWithDiseaseId(uniqueIds, storedDisease.get_id());
            }
        }
    }

    private void updateInteractionsWithDiseaseId(List<String> iIds, String diseaseId) {
        MongoOperations op = getTemplate();
        // get the interaction and update its diseaseids
        for(String id : iIds){
            Query query = new Query();
            query.addCriteria(Criteria.where("_id").is(id));
            Interaction interaction = op.findOne(query, Interaction.class);
            List<String> diseaseIds = interaction.getDiseaseIds();
            if(diseaseIds == null){
                diseaseIds = new ArrayList<>();
            }
            diseaseIds.add(diseaseId);
            interaction.setDiseaseIds(diseaseIds);
            op.save(interaction);
        }
    }
}
