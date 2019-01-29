/*
 * Created by sahmad on 29/01/19 15:31
 * UniProt Consortium.
 * Copyright (c) 2002-2019.
 *
 */

package uk.ac.ebi.uniprot.ds.writer;

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


public class InteractionWriter implements ItemWriter<UniProtEntry> {

    private final Map<String, Protein> proteinIdProteinMap;

    @Autowired
    InteractionDAO interactionDAO;

    public InteractionWriter(Map<String, Protein> proteinIdProteinMap){
        this.proteinIdProteinMap = proteinIdProteinMap;
    }

    @Override
    public void write(List<? extends UniProtEntry> entries) throws Exception {
        // get the list of proteins from entries
        for(UniProtEntry entry : entries){
            // get the protein from the uniprot entry
            Protein protein = this.proteinIdProteinMap.get(entry.getUniProtId().getValue());
            assert protein != null;
            List<uk.ac.ebi.kraken.interfaces.uniprot.comments.Interaction> commentInteractions = getInteractions(entry);
            createInteractions(protein, commentInteractions);
        }
    }

    //TODO use batch insert,
    private void createInteractions(Protein protein, List<uk.ac.ebi.kraken.interfaces.uniprot.comments.Interaction> commentInteractions){
        commentInteractions.forEach(ci -> createInteraction(protein, ci));
    }

    private void createInteraction(Protein protein, uk.ac.ebi.kraken.interfaces.uniprot.comments.Interaction commentInteraction) {
        Interaction.InteractionBuilder builder = Interaction.builder();
        builder.type(commentInteraction.getInteractionType().name());
        builder.accession(commentInteraction.getInteractorUniProtAccession().getValue());
        builder.gene(commentInteraction.getInteractionGeneName().getValue());
        builder.experimentCount(commentInteraction.getNumberOfExperiments());
        builder.firstInteractor(commentInteraction.getFirstInteractor().getValue());
        builder.secondInteractor(commentInteraction.getSecondInteractor().getValue());
        builder.protein(protein);
        Interaction interaction = builder.build();
        this.interactionDAO.save(interaction);
    }

    protected List<uk.ac.ebi.kraken.interfaces.uniprot.comments.Interaction> getInteractions(UniProtEntry entry) {
        List<InteractionComment> comments = entry.getComments(CommentType.INTERACTION);

        List<uk.ac.ebi.kraken.interfaces.uniprot.comments.Interaction> interactions = comments.parallelStream().map(InteractionComment::getInteractions)
                .flatMap(List::stream).collect(Collectors.toList());

        return interactions;
    }
}
