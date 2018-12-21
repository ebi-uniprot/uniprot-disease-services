/*
 * Created by sahmad on 12/21/18 9:09 AM
 * UniProt Consortium.
 * Copyright (c) 2002-2018.
 *
 */

package uk.ac.ebi.uniprot.disease.model;


import lombok.*;
import uk.ac.ebi.kraken.interfaces.uniprot.DatabaseCrossReference;
import uk.ac.ebi.kraken.interfaces.uniprot.comments.Interaction;
import uk.ac.ebi.kraken.interfaces.uniprot.features.VariantFeature;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

/**
 * @author sahmad
 */
@Getter
@Setter
@Builder
public class Disease implements Serializable {
    private static final long serialVersionUID = 680211621946161865L;
    private String id;
    private String name;
    private String description;
    private String acronym;
    private List<String> synonyms;
    private Integer proteinCount;
    private Integer drugCount;
    private Integer pathwayCount;
    private Integer variantCount;
    private Integer interactionCount;
    private Set<Protein> proteins;
    private List<DatabaseCrossReference> pathways;
    private List<VariantFeature> variants;
    private List<Interaction> interactions;

    @Override
    public String toString() {
        return "Disease{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", acronym='" + acronym + '\'' +
                ", synonyms=" + synonyms +
                ", proteinCount=" + proteinCount +
                ", drugCount=" + drugCount +
                ", pathwayCount=" + pathwayCount +
                ", variantCount=" + variantCount +
                ", interactionCount=" + interactionCount +
                ", pathways=" + pathways +
                ", variants=" + variants +
                ", interactions=" + interactions +
                '}';
    }
}
