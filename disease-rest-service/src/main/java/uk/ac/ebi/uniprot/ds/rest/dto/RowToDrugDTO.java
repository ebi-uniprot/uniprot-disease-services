package uk.ac.ebi.uniprot.ds.rest.dto;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;

/**
 * @author sahmad
 * @created 26/08/2021
 */
public class RowToDrugDTO implements Function<Object[], DrugDTO> {
    @Override
    public DrugDTO apply(Object[] drugRow) {
        DrugDTO.DrugDTOBuilder builder = DrugDTO.builder();
        builder.name((String) drugRow[0]);
        builder.proteinAccession((String) drugRow[8]);
        builder.sourceType((String) drugRow[1]);
        Set<String> srcIds = new HashSet<>();
        srcIds.add((String) drugRow[2]);
        builder.sourceIds(srcIds);
        builder.moleculeType((String) drugRow[3]);
        builder.maxTrialPhase((Integer) drugRow[4]);
        builder.mechanismOfAction((String) drugRow[5]);
        builder.clinicalTrialLink((String) drugRow[6]);
        DrugDTO.BasicDiseaseDTO disease = new DrugDTO.BasicDiseaseDTO((String) drugRow[10], (String) drugRow[9]);
        builder.disease(disease);
        Set<String> evidences = new HashSet<>();
        String evidence = (String) drugRow[7];
        if(Objects.nonNull(evidence)) {
            evidences.add(evidence);
        }
        builder.evidences(evidences);
        return builder.build();
    }
}
