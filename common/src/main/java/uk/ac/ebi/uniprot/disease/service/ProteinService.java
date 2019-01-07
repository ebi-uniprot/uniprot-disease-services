/*
 * Created by sahmad on 12/21/18 9:07 AM
 * UniProt Consortium.
 * Copyright (c) 2002-2018.
 *
 */

package uk.ac.ebi.uniprot.disease.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.ac.ebi.kraken.interfaces.uniprot.*;
import uk.ac.ebi.kraken.interfaces.uniprot.comments.*;
import uk.ac.ebi.kraken.interfaces.uniprot.description.FieldType;
import uk.ac.ebi.kraken.interfaces.uniprot.description.Name;
import uk.ac.ebi.kraken.interfaces.uniprot.features.Feature;
import uk.ac.ebi.kraken.interfaces.uniprot.features.FeatureType;
import uk.ac.ebi.uniprot.disease.model.Disease;
import uk.ac.ebi.uniprot.disease.model.Protein;
import uk.ac.ebi.uniprot.disease.utils.Constants;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ProteinService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ProteinService.class);
    @Autowired
    private DiseaseService diseaseService;

    public void createProtein(UniProtEntry uniProtEntry) {
        Protein protein = convertToProtein(uniProtEntry);
        // DAO layer code goes here to persist the protein object
        LOGGER.debug("The protein saved: {}", protein);
    }

    public Protein convertToProtein(UniProtEntry entry) {

        Protein.ProteinBuilder builder = Protein.builder();
        builder.id(entry.getUniProtId().getValue());
        builder.name(getDescription(entry.getProteinDescription()));
        builder.accession(entry.getPrimaryUniProtAccession().getValue());
        builder.gene(getGene(entry.getGenes()));
        builder.functions(getFunctions(entry.getComments(CommentType.FUNCTION)));

        // set interactions
        List<Interaction> interactions = getInteractions(entry.getComments(CommentType.INTERACTION));
        builder.interactions(interactions);
        builder.interactionCount(interactions.size());
        builder.diseaseCount(getDiseaseCount(entry.getComments(CommentType.DISEASE)));

        // set variants
        Collection<Feature> variants = entry.getFeatures(FeatureType.VARIANT);
        builder.variants(new ArrayList(variants));
        builder.variantCount(variants.size());

        // set pathways
        List<DatabaseCrossReference> pathways = getPathways(entry);
        builder.pathways(pathways);
        builder.pathwayCount(pathways.size());

        builder.publicationCount(entry.getCitationsNew().size());

        Protein protein = builder.build();
        // get and set the diseases caused by the protein
        Set<Disease> diseases = diseaseService.getDiseases(entry.getComments(CommentType.DISEASE), protein);
        protein.setDiseases(diseases);

        return protein;
    }

    private Integer getDiseaseCount(List<DiseaseCommentStructured> comments) {
        return Math.toIntExact(comments.parallelStream().filter(dc -> dc.hasDefinedDisease()).count());
    }

    private List<DatabaseCrossReference> getPathways(UniProtEntry entry) {
        List<DatabaseCrossReference> dbXRefs = entry.getDatabaseCrossReferences(DatabaseType.REACTOME);
        return dbXRefs;
    }

    private List<Interaction> getInteractions(List<InteractionComment> comments) {

        List<Interaction> interactions = comments.parallelStream().map(InteractionComment::getInteractions)
                                                                  .flatMap(List::stream).collect(Collectors.toList());

        return interactions;
    }

    private List<String> getFunctions(List<FunctionComment> comments) {

        List<String> functions = comments.stream().map(FunctionComment::getValue).collect(Collectors.toList());

        return functions;
    }

    private String getDescription(ProteinDescription pd) {
        Name name;

        if (pd.hasRecommendedName()) {
            name = pd.getRecommendedName();
        } else {
            name = pd.getSubNames().get(Constants.ZERO);
        }

        return name.getFieldsByType(FieldType.FULL).get(Constants.ZERO).getValue();
    }

    private String getGene(List<Gene> genes) {
        String geneName = null;
        String orfName = null;
        String olnName = null;

        for (Gene gene : genes) {
            if (gene.hasGeneName()) {
                geneName = gene.getGeneName().getValue();
                break;
            } else if (!isListEmpty(gene.getOrderedLocusNames())) {
                olnName = gene.getOrderedLocusNames().get(Constants.ZERO).getValue();
            } else if (!isListEmpty(gene.getORFNames())) {
                orfName = gene.getORFNames().get(Constants.ZERO).getValue();
            }
        }

        return (geneName != null) ? geneName : ((olnName != null) ? olnName : orfName);
    }

    // probably move it to a util class
    private boolean isListEmpty(List<?> list) {
        return list == null || list.isEmpty();
    }
}
