/*
 * Created by sahmad on 01/02/19 08:58
 * UniProt Consortium.
 * Copyright (c) 2002-2019.
 *
 */

package uk.ac.ebi.uniprot.ds.processor;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import uk.ac.ebi.kraken.interfaces.uniprot.UniProtEntry;
import uk.ac.ebi.kraken.interfaces.uniprot.comments.CommentType;
import uk.ac.ebi.kraken.interfaces.uniprot.comments.InteractionComment;
import uk.ac.ebi.uniprot.ds.dao.InteractionDAO;
import uk.ac.ebi.uniprot.ds.model.Interaction;
import uk.ac.ebi.uniprot.ds.model.Protein;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


public class InteractionProcessor implements ItemProcessor<UniProtEntry, UniProtEntry> {

    private List<Protein> proteins;

    public InteractionProcessor(List<Protein> proteins){
        this.proteins = proteins;
    }

    @Override
    public UniProtEntry process(UniProtEntry entry) throws Exception {
        // get the last protein and enrich it
        int size = this.proteins.size();

        Protein protein = this.proteins.get(size - 1);
        List<uk.ac.ebi.kraken.interfaces.uniprot.comments.Interaction> commentInteractions = getInteractionsFromEntry(entry);
        List<Interaction> interactions = createInteractions(protein, commentInteractions);
        protein.addInteractions(interactions);

        return entry;
    }

    private List<Interaction> createInteractions(Protein protein, List<uk.ac.ebi.kraken.interfaces.uniprot.comments.Interaction>
            commentInteractions){
        List<Interaction> interactions = commentInteractions.stream().map(ci -> createInteraction(protein, ci))
                .collect(Collectors.toList());
        return interactions;
    }

    private Interaction createInteraction(Protein protein, uk.ac.ebi.kraken.interfaces.uniprot.comments.Interaction
            commentInteraction) {
        Interaction.InteractionBuilder builder = Interaction.builder();
        builder.type(commentInteraction.getInteractionType().name());
        builder.accession(commentInteraction.getInteractorUniProtAccession().getValue());
        builder.gene(commentInteraction.getInteractionGeneName().getValue());
        builder.experimentCount(commentInteraction.getNumberOfExperiments());
        builder.firstInteractor(commentInteraction.getFirstInteractor().getValue());
        builder.secondInteractor(commentInteraction.getSecondInteractor().getValue());
        builder.protein(protein);
        Interaction interaction = builder.build();
        return interaction;
    }

    private List<uk.ac.ebi.kraken.interfaces.uniprot.comments.Interaction> getInteractionsFromEntry(UniProtEntry entry) {
        List<InteractionComment> comments = entry.getComments(CommentType.INTERACTION);

        List<uk.ac.ebi.kraken.interfaces.uniprot.comments.Interaction> interactions = comments.parallelStream()
                .map(InteractionComment::getInteractions)
                .flatMap(List::stream).collect(Collectors.toList());

        return interactions;
    }
}
