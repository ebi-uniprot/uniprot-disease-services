/*
 * Created by sahmad on 1/18/19 9:21 AM
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
public class Protein implements Serializable {
    private static final long serialVersionUID = -8122356497989269770L;
    private String _id;
    private String proteinId;
    private String name;
    private String accession;
    private String gene;
    private String function;
    private List<String> diseaseIds;
    private List<String> pathwayIds;
    private List<String> variantIds;
    private List<String> interactionIds;

    @Override
    public String toString() {
        return "Protein{" +
                "id='" + proteinId + '\'' +
                ", name='" + name + '\'' +
                ", accession='" + accession + '\'' +
                ", gene='" + gene + '\'' +
                ", functionIds=" + function +
                ", diseaseIds=" + diseaseIds +
                ", pathwayIds=" + pathwayIds +
                ", variantIds=" + variantIds +
                ", interactionIds=" + interactionIds +
                '}';
    }
}

