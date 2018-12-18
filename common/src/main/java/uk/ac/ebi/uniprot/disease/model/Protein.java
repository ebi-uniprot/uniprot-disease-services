package uk.ac.ebi.uniprot.disease.model;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author sahmad
 */
@Data
@Builder
public class Protein implements Serializable {
    private static final long serialVersionUID = 2210840006115833880L;
    private String id;// e.g. AATM_RABIT
    private String name;// description of the entry
    private String accession;//Accession id e.g. P12345
    private String gene; // TODO should we create first class object for Gene??
    private List<String> functions;
    private Integer interactionCount;
    private Integer pathwayCount;
    private Integer variantCount;// TODO to be decided
    private Integer diseaseCount;
    private Integer drugCount;// TODO to be decided
    private Integer publicationCount;// TODO to be decided
}
