package uk.ac.ebi.uniprot.ds.rest.dto;

import lombok.*;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DrugDTO {
    private String name;// part of unique key - use this in equal and hash code
    private String proteinAccession;// part of unique key
    private BasicDiseaseDTO disease;// part of unique key
    private String moleculeType;//part of unique key
    private String mechanismOfAction;// part of unique key
    private Integer maxTrialPhase;
    private String clinicalTrialLink;
    private String sourceType;
    private Set<String> sourceIds = new HashSet<>();
    private Set<String> evidences = new HashSet<>();

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @EqualsAndHashCode
    public static class BasicDiseaseDTO{
        private String diseaseId;
        private String diseaseName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DrugDTO drugDTO = (DrugDTO) o;// only significant fields
        return  Objects.equals(name, drugDTO.getName()) &&
                Objects.equals(proteinAccession, drugDTO.proteinAccession) &&
                Objects.equals(disease, drugDTO.disease) &&
                moleculeType.equals(drugDTO.moleculeType) &&
                Objects.equals(mechanismOfAction, drugDTO.mechanismOfAction);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, proteinAccession, disease, moleculeType, mechanismOfAction);
    }

    @Override
    public String toString() {
        return "DrugDTO{" +
                ", name='" + name + '\'' +
                ", proteinAccession='" + proteinAccession + '\'' +
                ", diseases=" + disease +
                ", moleculeType='" + moleculeType + '\'' +
                ", mechanismOfAction='" + mechanismOfAction + '\'' +
                ", maxTrialPhase=" + maxTrialPhase +
                ", clinicalTrialLink='" + clinicalTrialLink + '\'' +
                ", sourceType='" + sourceType + '\'' +
                ", sourceId='" + sourceIds + '\'' +
                ", evidences=" + evidences +
                '}';
    }
}
