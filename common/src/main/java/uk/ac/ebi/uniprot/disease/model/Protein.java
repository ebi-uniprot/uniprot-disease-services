package uk.ac.ebi.uniprot.disease.model;

import lombok.*;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

/**
 * @author sahmad
 */
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
                '}';
    }
}
