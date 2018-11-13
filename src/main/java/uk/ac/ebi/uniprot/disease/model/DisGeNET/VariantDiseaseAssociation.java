package uk.ac.ebi.uniprot.disease.model.DisGeNET;

import lombok.*;

/**
 * @author sahmad
 */
@Getter
@Setter
@EqualsAndHashCode
@ToString
@Builder
public class VariantDiseaseAssociation {
    private String snpId;
    private String diseaseId;
    private String diseaseName;
    private Double score; // Gene-Disease score
    private Integer pmidCount; // total number of PMIDs count supporting the association
    private String source;
}
