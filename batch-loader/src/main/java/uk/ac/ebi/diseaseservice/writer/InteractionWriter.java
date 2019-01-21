/*
 * Created by sahmad on 19/01/19 22:09
 * UniProt Consortium.
 * Copyright (c) 2002-2019.
 *
 */

package uk.ac.ebi.diseaseservice.writer;

import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import uk.ac.ebi.diseaseservice.model.Interaction;
import uk.ac.ebi.diseaseservice.model.Protein;
import uk.ac.ebi.kraken.interfaces.uniprot.UniProtEntry;
import uk.ac.ebi.kraken.interfaces.uniprot.comments.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class InteractionWriter extends BaseSwissProtWriter {
    @Override
    protected void doWrite(List<? extends UniProtEntry> entries) {

        MongoOperations mongoOperations = getTemplate();

        for(UniProtEntry entry : entries){
            Protein protein = findProteinbyProteinId(entry.getUniProtId().getValue());

            List<uk.ac.ebi.kraken.interfaces.uniprot.comments.Interaction> commentInteractions = getInteractions(entry);
            // write each interaction
            for(uk.ac.ebi.kraken.interfaces.uniprot.comments.Interaction commentInteraction : commentInteractions){
                // try to get the interaction first
                Interaction interaction = findInteraction(commentInteraction.getInteractionType(),
                        commentInteraction.getFirstInteractor(), commentInteraction.getSecondInteractor());

                if(interaction != null){ // update the interaction if exist
                    interaction.getProteinIds().add(protein.get_id());

                } else { // create a new interaction
                    Interaction.InteractionBuilder builder = Interaction.builder();
                    builder.interactionType(commentInteraction.getInteractionType());
                    builder.interactionGeneName(commentInteraction.getInteractionGeneName());
                    builder.numberOfExperiments(commentInteraction.getNumberOfExperiments());
                    builder.firstInteractor(commentInteraction.getFirstInteractor());
                    builder.secondInteractor(commentInteraction.getSecondInteractor());
                    builder.commentStatus(commentInteraction.getCommentStatus());
                    builder.evidences(commentInteraction.getEvidenceIds());
                    builder.commentStatus(commentInteraction.getCommentStatus());

                    List<String> proteinIds = new ArrayList<>();
                    proteinIds.add(protein.get_id());
                    builder.proteinIds(proteinIds);
                    interaction = builder.build();
                }
                mongoOperations.save(interaction);
                // update protein with interaction _id
                updateProtein(protein, interaction);

            }
        }
    }



    private Interaction findInteraction(InteractionType interactionType, InteractorAccession firstInteractor, InteractorAccession secondInteractor) {
        MongoOperations mongoOperations = getTemplate();

        Query query = new Query();

        Criteria criteria1 = Criteria.where("interactionType").is(interactionType.name())
                .and("firstInteractor.value").is(firstInteractor.getValue())
                .and("secondInteractor.value").is(secondInteractor.getValue());

        if(InteractionType.SELF == interactionType){
            query.addCriteria(criteria1);

        } else if(InteractionType.BINARY == interactionType){
            Criteria criteria2 = Criteria.where("interactionType").is(interactionType.name())
                    .and("secondInteractor.value").is(firstInteractor.getValue())
                    .and("firstInteractor.value").is(secondInteractor.getValue());

            Criteria criteria = new Criteria().orOperator(criteria1, criteria2);
            query.addCriteria(criteria);

        }
        Interaction interaction = mongoOperations.findOne(query, Interaction.class);

        return interaction;
    }
}
