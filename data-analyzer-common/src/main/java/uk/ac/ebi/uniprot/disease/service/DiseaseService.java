/*
 * Created by sahmad on 12/21/18 9:01 AM
 * UniProt Consortium.
 * Copyright (c) 2002-2018.
 *
 */

package uk.ac.ebi.uniprot.disease.service;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import uk.ac.ebi.kraken.interfaces.uniprot.DatabaseCrossReference;
import uk.ac.ebi.kraken.interfaces.uniprot.comments.DiseaseCommentStructured;
import uk.ac.ebi.kraken.interfaces.uniprot.comments.Interaction;
import uk.ac.ebi.kraken.interfaces.uniprot.comments.InteractionType;
import uk.ac.ebi.kraken.interfaces.uniprot.features.VariantFeature;
import uk.ac.ebi.uniprot.disease.model.Disease;
import uk.ac.ebi.uniprot.disease.model.Protein;
import uk.ac.ebi.uniprot.disease.utils.Constants;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class DiseaseService {
    private Map<String, Disease> nameDisease = new HashMap<>();

    /**
     * Get a list of diseases from the protein
     * @param diseaseComments
     * @param protein
     * @return diseases
     */
    public Set<Disease> getDiseases(List<DiseaseCommentStructured> diseaseComments, Protein protein) {

        Set<Disease> diseases = diseaseComments.stream().filter(dc -> dc.hasDefinedDisease()).
                map(dc -> convertToDisease(dc, protein)).collect(Collectors.toSet());

        return diseases;
    }

    /**
     * Converts the DiseaseComment to Disease object using protein to fill other details
     * @param diseaseComment
     * @param protein
     * @return
     */
    public Disease convertToDisease(DiseaseCommentStructured diseaseComment, Protein protein) {
        uk.ac.ebi.kraken.interfaces.uniprot.comments.Disease disease = diseaseComment.getDisease();
        // get common fields
        String descr = disease.getDescription().getValue();
        String name = disease.getDiseaseId().getValue();
        String acronym = disease.getAcronym().getValue();
        List<VariantFeature> dVariants = getDiseaseVariants(acronym, protein.getVariants());

        Disease pDisease;

        if (nameDisease.containsKey(name)) {

            pDisease = nameDisease.get(name);
            pDisease.getProteins().add(protein);
            pDisease.setProteinCount(pDisease.getProteinCount() + Constants.ONE);

            // set pathways
            List<DatabaseCrossReference> mergedPathways = mergePathways(pDisease.getPathways(), protein.getPathways());
            pDisease.setPathways(mergedPathways);
            pDisease.setPathwayCount(mergedPathways.size());

            // set variants
            pDisease.getVariants().addAll(dVariants);
            pDisease.setVariantCount(pDisease.getVariants().size());

            // set interactions
            List<Interaction> mergedInteractions = mergeInteractions(pDisease.getInteractions(), protein.getInteractions());
            pDisease.setInteractions(mergedInteractions);
            pDisease.setInteractionCount(mergedInteractions.size());

        } else {
            Disease.DiseaseBuilder builder = Disease.builder();
            builder.description(descr).name(name).acronym(acronym);
            builder.proteinCount(Constants.ONE);
            // set pathways
            List<DatabaseCrossReference> pathways = protein.getPathways();
            builder.pathways(pathways);
            builder.pathwayCount(pathways.size());

            // set variant
            builder.variants(dVariants);
            builder.variantCount(dVariants.size());

            // set interaction
            builder.interactions(protein.getInteractions());
            builder.interactionCount(protein.getInteractionCount());

            // build the object
            pDisease = builder.build();
            Set<Protein> proteins = new HashSet<>();
            proteins.add(protein);
            pDisease.setProteins(proteins);

            nameDisease.put(name, pDisease);
        }
        return pDisease;
    }

    /*
            A -- B and B --- A are same, take one only of them
            SELF Interaction is taken e.g. A -- A
         */
    private List<Interaction> mergeInteractions(List<Interaction> dInteractions, List<Interaction> pInteractions) {
        List<Interaction> mergedInteractions = new ArrayList(dInteractions);
        for (Interaction pi : pInteractions) {
            if (!doesInteractionExist(pi, dInteractions)) {
                mergedInteractions.add(pi);
            }
        }

        return mergedInteractions;

    }

    private boolean doesInteractionExist(Interaction pi, List<Interaction> dInteractions) {
        InteractionType type = pi.getInteractionType();
        for (Interaction di : dInteractions) {
            if (
                    type == InteractionType.SELF
                            &&
                            Objects.equals(pi.getFirstInteractor(), di.getFirstInteractor())
                            &&
                            Objects.equals(pi.getSecondInteractor(), di.getSecondInteractor())
            ) {
                return true;
            } else if (
                    type == InteractionType.BINARY &&
                            (
                                    (
                                            Objects.equals(pi.getFirstInteractor(), di.getFirstInteractor())
                                                    &&
                                                    Objects.equals(pi.getSecondInteractor(), di.getSecondInteractor())
                                    )
                                            ||
                                            (
                                                    Objects.equals(pi.getFirstInteractor(), di.getSecondInteractor())
                                                            &&
                                                            Objects.equals(pi.getSecondInteractor(), di.getFirstInteractor())
                                            )
                            )
            ) {
                return true;
            }

        }

        return false;
    }

    private List<VariantFeature> getDiseaseVariants(String acronym, List<VariantFeature> variants) {

        List<VariantFeature> dVariants = variants.stream().filter(v -> !StringUtils.isEmpty(acronym)
                &&
                v.getVariantReport().getValue().contains(acronym))
                .collect(Collectors.toList());
        return dVariants;

    }

    private List<DatabaseCrossReference> mergePathways(List<DatabaseCrossReference> diseasePathways,
                                                       List<DatabaseCrossReference> proteinPathways) {

        List<DatabaseCrossReference> mergedPathways = new ArrayList(diseasePathways);

        for (DatabaseCrossReference pp : proteinPathways) {
            if (!doesPathwayExist(pp, diseasePathways)) {
                mergedPathways.add(pp);

            }
        }

        return mergedPathways;

    }

    private boolean doesPathwayExist(DatabaseCrossReference proteinPathway,
                                     List<DatabaseCrossReference> diseasePathways) {

        for (DatabaseCrossReference dp : diseasePathways) {
            if (proteinPathway.getPrimaryId().equals(dp.getPrimaryId())) {
                return true;
            }
        }

        return false;
    }
}
