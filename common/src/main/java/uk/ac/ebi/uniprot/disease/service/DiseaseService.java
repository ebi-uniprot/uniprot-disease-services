package uk.ac.ebi.uniprot.disease.service;

import uk.ac.ebi.kraken.interfaces.uniprot.comments.DiseaseCommentStructured;
import uk.ac.ebi.uniprot.disease.model.Disease;
import uk.ac.ebi.uniprot.disease.model.Protein;
import uk.ac.ebi.uniprot.disease.utils.Constants;

import java.util.*;
import java.util.stream.Collectors;

public class DiseaseService {
    private Map<String, Boolean> diseaseExist = new HashMap<>();
    private Map<String, Disease> nameDisease = new HashMap<>();

    public Set<Disease> getDiseases(List<DiseaseCommentStructured> diseaseComments, Protein protein) {
        Set<Disease> diseases = diseaseComments.stream().filter(dc -> dc.hasDefinedDisease()).
                map(dc -> convertToDisease(dc, protein)).collect(Collectors.toSet());
        return diseases;
    }

    public Disease convertToDisease(DiseaseCommentStructured diseaseComment, Protein protein){
        uk.ac.ebi.kraken.interfaces.uniprot.comments.Disease disease = diseaseComment.getDisease();
        String descr = disease.getDescription().getValue();
        String name = disease.getDiseaseId().getValue();
        String acronym = disease.getAcronym().getValue();
        Disease pDisease;
        if(diseaseExist.containsKey(name)){
            pDisease = nameDisease.get(name);
            pDisease.getProteins().add(protein);
            pDisease.setProteinCount(pDisease.getProteinCount() + Constants.ONE);
            pDisease.setPathwayCount(pDisease.getPathwayCount() + protein.getPathwayCount());
            pDisease.setVariantCount(pDisease.getVariantCount() + protein.getVariantCount());
        } else {
            Disease.DiseaseBuilder builder = Disease.builder();
            builder.description(descr).name(name).acronym(acronym);
            builder.proteinCount(Constants.ONE);
            builder.pathwayCount(protein.getPathwayCount());
            builder.variantCount(protein.getVariantCount());
            pDisease = builder.build();
            Set<Protein> proteins = new HashSet<>();
            proteins.add(protein);
            pDisease.setProteins(proteins);
            diseaseExist.put(name, Boolean.TRUE);
            nameDisease.put(name, pDisease);
        }
        return pDisease;
    }
}
