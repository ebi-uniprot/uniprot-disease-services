package uk.ac.ebi.uniprot.disease.model.disgenet;


import lombok.*;

/**
 * @author sahmad
 * Class responsinble for holding disease mapping
 */
@Getter
@Setter
@EqualsAndHashCode
@ToString
@Builder
public class DiseaseMapping {
    private String diseaseId;
    private String name;
    private String vocab;
    private String code;
    private String vocabName;
}
