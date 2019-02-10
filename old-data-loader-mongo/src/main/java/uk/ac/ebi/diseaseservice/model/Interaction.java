/*
 * Created by sahmad on 1/18/19 9:31 AM
 * UniProt Consortium.
 * Copyright (c) 2002-2019.
 *
 */

package uk.ac.ebi.diseaseservice.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import uk.ac.ebi.kraken.interfaces.uniprot.CommentStatus;
import uk.ac.ebi.kraken.interfaces.uniprot.comments.*;
import uk.ac.ebi.kraken.interfaces.uniprot.evidences.EvidenceId;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@Builder
public class Interaction implements Serializable {
    private static final long serialVersionUID = -3768593674237091888L;
    private String _id;
    private InteractionType interactionType;
    private InteractorUniProtAccession interactorUniProtAccession;
    private InteractionGeneName interactionGeneName;
    private int numberOfExperiments;
    private InteractorAccession firstInteractor;
    private InteractorAccession secondInteractor;
    private CommentType type;
    private List<EvidenceId> evidences;
    private CommentStatus commentStatus;
    private List<String> diseaseIds;
    private List<String> proteinIds;

    @Override
    public String toString() {
        return "Interaction{" +
                "_id='" + _id + '\'' +
                ", interactionType=" + interactionType +
                ", interactorUniProtAccession=" + interactorUniProtAccession +
                ", interactionGeneName=" + interactionGeneName +
                ", numberOfExperiments=" + numberOfExperiments +
                ", firstInteractor=" + firstInteractor +
                ", secondInteractor=" + secondInteractor +
                ", type=" + type +
                ", evidences=" + evidences +
                ", commentStatus=" + commentStatus +
                ", diseaseIds=" + diseaseIds +
                ", proteinIds=" + proteinIds +
                '}';
    }
}
