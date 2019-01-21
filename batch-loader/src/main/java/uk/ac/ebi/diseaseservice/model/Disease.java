/*
 * Created by sahmad on 1/18/19 9:13 AM
 * UniProt Consortium.
 * Copyright (c) 2002-2019.
 *
 */

package uk.ac.ebi.diseaseservice.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@Builder
public class Disease implements Serializable {
    private static final long serialVersionUID = 1417442979180829008L;
    private String _id;
    private String diseaseId;
    private String name;
    private String description;
    private String acronym;
    private List<String> synonyms;
    private List<String> proteinIds;
    private List<String> pathwayIds;
    private List<String> variantIds;
    private List<String> interactionIds;

    @Override
    public String toString() {
        return "Disease{" +
                "id='" + _id + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", acronym='" + acronym + '\'' +
                ", synonyms=" + synonyms +
                ", proteinIds=" + proteinIds +
                ", pathwayIds=" + pathwayIds +
                ", variantIds=" + variantIds +
                ", interactionIds=" + interactionIds +
                '}';
    }
}