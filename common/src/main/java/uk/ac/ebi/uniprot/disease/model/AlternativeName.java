package uk.ac.ebi.uniprot.disease.model;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

/**
 * @author sahmad
 * Alternative name of the disease
 */

@Data
@Builder
public class AlternativeName implements Serializable {
    private static final long serialVersionUID = 2159311718197568305L;
    private Disease disease;
    private String title;
    private String source;
}
