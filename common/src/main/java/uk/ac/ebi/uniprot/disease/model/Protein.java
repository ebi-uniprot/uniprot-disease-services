package uk.ac.ebi.uniprot.disease.model;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

/**
 * @author sahmad
 */
@Data
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
}
