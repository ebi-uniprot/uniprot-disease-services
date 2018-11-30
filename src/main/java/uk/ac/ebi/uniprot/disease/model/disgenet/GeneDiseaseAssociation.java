package uk.ac.ebi.uniprot.disease.model.disgenet;

import lombok.*;

/**
 * @author sahmad
 */
@Getter
@Setter
@ToString
@EqualsAndHashCode
@Builder
public class GeneDiseaseAssociation {
    private Integer geneId;
    private String geneSymbol;
    private String diseaseId;
    private String diseaseName;
    private Double score; // Gene-Disease score
    private Integer pmidCount; // total number of PMIDs count supporting the association
    private Integer snpCount; // total number of associated SNPs
    private String source;
}
