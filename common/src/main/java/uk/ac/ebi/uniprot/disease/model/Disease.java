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
    private Set<Protein> proteins;

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
                '}';
    }
}
