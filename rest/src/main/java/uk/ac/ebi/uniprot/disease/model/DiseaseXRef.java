package uk.ac.ebi.uniprot.disease.model;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

/**
 * @author sahmad
 */
@Data
@Builder
public class DiseaseXRef implements Serializable {
    private static final long serialVersionUID = -3733750880606456079L;

    private Disease disease;
    private String refType;// e.g. MIM
    private String refId;// e.g. 616034
}
