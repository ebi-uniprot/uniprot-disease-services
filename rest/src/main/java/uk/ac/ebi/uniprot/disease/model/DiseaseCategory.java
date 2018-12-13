package uk.ac.ebi.uniprot.disease.model;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

/**
 * @author sahmad
 */
@Data
@Builder
public class DiseaseCategory implements Serializable {
    private static final long serialVersionUID = 179982175464744395L;
    private Category category;
    private Disease disease;
}
