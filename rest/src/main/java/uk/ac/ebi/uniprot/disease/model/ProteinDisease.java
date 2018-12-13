package uk.ac.ebi.uniprot.disease.model;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

/**
 * @author sahmad
 */
@Data
@Builder
public class ProteinDisease implements Serializable {
    private static final long serialVersionUID = 8644639054048410910L;
    private Protein protein;
    private Disease disease;
}
