/*
 * Created by sahmad on 12/21/18 9:10 AM
 * UniProt Consortium.
 * Copyright (c) 2002-2018.
 *
 */

package uk.ac.ebi.uniprot.disease.model;

import lombok.*;
import uk.ac.ebi.kraken.interfaces.uniprot.DatabaseCrossReference;
import uk.ac.ebi.kraken.interfaces.uniprot.comments.Interaction;
import uk.ac.ebi.kraken.interfaces.uniprot.comments.InteractionComment;
import uk.ac.ebi.kraken.interfaces.uniprot.features.VariantFeature;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@Builder
public class Protein implements Serializable {
    private static final long serialVersionUID = 2210840006115833880L;
    private String id;
    private String name;
    private String accession;
    private String gene;
    private List<String> functions;
    private Integer interactionCount;
    private Integer pathwayCount;
    private Integer variantCount;
    private Integer diseaseCount;
    private Integer drugCount;// TODO to be decided
    private Integer publicationCount;
    private Set<Disease> diseases;
    private List<DatabaseCrossReference> pathways;
    private List<VariantFeature> variants;
    private List<Interaction> interactions;

    @Override
    public String toString() {
        return "Protein{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", accession='" + accession + '\'' +
                ", gene='" + gene + '\'' +
                ", functions=" + functions +
                ", interactionCount=" + interactionCount +
                ", pathwayCount=" + pathwayCount +
                ", variantCount=" + variantCount +
                ", diseaseCount=" + diseaseCount +
                ", drugCount=" + drugCount +
                ", publicationCount=" + publicationCount +
                ", diseases=" + diseases +
                ", pathways=" + pathways +
                ", variants=" + variants +
                ", interactions=" + interactions +
                '}';
    }
}
