package uk.ac.ebi.uniprot.disease.model.sources.disgenet;

import lombok.*;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@Builder
public class VariantDiseasePMIDAssociation {
    private String snpId;
    private String diseaseId;
    private String sentence;
    private Long pmid;
    private Double score;
    private String originalSource;
    private String diseaseName;
    private String diseaseType;
    private Integer chromosome;
    private Long chromosomePosition;
}
