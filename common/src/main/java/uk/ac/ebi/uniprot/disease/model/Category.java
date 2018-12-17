package uk.ac.ebi.uniprot.disease.model;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

/**
 * @author sahmad
 */
@Data
@Builder
public class Category implements Serializable {
    private static final long serialVersionUID = -5178570190210932008L;
    private Integer id;
    private String name;
}
