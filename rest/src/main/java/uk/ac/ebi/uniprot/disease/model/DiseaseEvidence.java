package uk.ac.ebi.uniprot.disease.model;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

/**
 * @author sahmad
 */
@Data
@Builder
public class DiseaseEvidence implements Serializable {
    private static final long serialVersionUID = -2411682706537854657L;

    private Disease disease;
    private String source;
    private String pubMedId;//pmid
}
