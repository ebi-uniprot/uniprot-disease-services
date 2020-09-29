/*
 * Created by sahmad on 29/01/19 15:31
 * UniProt Consortium.
 * Copyright (c) 2002-2019.
 *
 */

package uk.ac.ebi.uniprot.ds.importer.writer;

import org.apache.commons.lang3.StringUtils;

import uk.ac.ebi.kraken.interfaces.uniprot.UniProtEntry;
import uk.ac.ebi.kraken.interfaces.uniprot.comments.CommentType;
import uk.ac.ebi.kraken.interfaces.uniprot.comments.InteractionComment;
import uk.ac.ebi.uniprot.ds.common.model.Interaction;
import uk.ac.ebi.uniprot.ds.common.model.Protein;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;


public class InteractionHelper {

    public List<Interaction> getInteractions(UniProtEntry entry, Protein protein){
        List<InteractionComment> comments = entry.getComments(CommentType.INTERACTION);
        List<Interaction> interactions = comments
                .stream()
                .filter(cmt -> cmt.getInteractions() != null && !cmt.getInteractions().isEmpty())
                .map(cmt -> cmt.getInteractions())
                .flatMap(List::stream)
                .map(ci -> createInteraction(protein, ci))
                .collect(Collectors.toList());
        return interactions;
    }

    private Interaction createInteraction(Protein protein,
                                          uk.ac.ebi.kraken.interfaces.uniprot.comments.Interaction commentInteraction) {
        Interaction.InteractionBuilder builder = Interaction.builder();
        builder.type(commentInteraction.getInteractionType().name());

        if(Objects.nonNull(commentInteraction.getSecondInteractantParent()) &&
                StringUtils.isNotBlank(commentInteraction.getSecondInteractantParent().getValue())){
            builder.accession(commentInteraction.getSecondInteractantParent().getValue());
        } else if(Objects.nonNull(commentInteraction.getSecondInteractant())){
            builder.accession(commentInteraction.getSecondInteractant().getValue());
        }

        builder.gene(commentInteraction.getInteractionGeneName().getValue());
        builder.experimentCount(commentInteraction.getNumberOfExperiments());
        builder.firstInteractor(commentInteraction.getFirstInteractor().getValue());
        builder.secondInteractor(commentInteraction.getSecondInteractor().getValue());
        builder.protein(protein);
        Interaction interaction = builder.build();
        return interaction;
    }
}
