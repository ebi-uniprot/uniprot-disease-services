package uk.ac.ebi.uniprot.disease.model;


import lombok.Data;
import lombok.Builder;

import java.io.Serializable;

/**
 * @author sahmad
 */
@Data
@Builder
public class Disease implements Serializable {
    private static final long serialVersionUID = -821277980902852754L;

    private String id;
    private String name;
    private String description;
    private String acronym;
}
