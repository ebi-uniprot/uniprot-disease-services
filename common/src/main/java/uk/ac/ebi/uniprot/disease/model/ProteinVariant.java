package uk.ac.ebi.uniprot.disease.model;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

/**
 * @author sahmad
 */
@Data
@Builder
public class ProteinVariant implements Serializable {
    private static final long serialVersionUID = 7293588633307338914L;

    private String snpId;
    private String hgvsGenomic;
    private String consequence; // Is it same as description? TODO ask Andrew
    private String transcript;

}
