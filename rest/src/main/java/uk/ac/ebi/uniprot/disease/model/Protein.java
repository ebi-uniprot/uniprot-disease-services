package uk.ac.ebi.uniprot.disease.model;

import lombok.Builder;
import lombok.Data;
import java.io.Serializable;

/**
 * @author sahmad
 */
@Data
@Builder
public class Protein implements Serializable {
    private static final long serialVersionUID = 8158091020640796976L;

    private String id;
    private String name;
    private Boolean isCurated;
    private String function;
    private String cellularLocation;
    private String expression;
    private String interaction;
    private String structure;

}
