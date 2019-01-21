/*
 * Created by sahmad on 1/18/19 2:09 PM
 * UniProt Consortium.
 * Copyright (c) 2002-2019.
 *
 */

package uk.ac.ebi.diseaseservice.writer;

import org.springframework.batch.item.data.MongoItemWriter;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import uk.ac.ebi.diseaseservice.model.*;
import uk.ac.ebi.kraken.interfaces.uniprot.*;
import uk.ac.ebi.kraken.interfaces.uniprot.comments.CommentType;
import uk.ac.ebi.kraken.interfaces.uniprot.comments.DiseaseCommentStructured;
import uk.ac.ebi.kraken.interfaces.uniprot.comments.InteractionComment;
import uk.ac.ebi.kraken.interfaces.uniprot.description.FieldType;
import uk.ac.ebi.kraken.interfaces.uniprot.description.Name;
import uk.ac.ebi.uniprot.disease.utils.Constants;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public abstract class BaseSwissProtWriter extends MongoItemWriter<UniProtEntry> {


    @Override
    public void write(List<? extends UniProtEntry> entries){
        doWrite(entries);
    }

    protected abstract void doWrite(List<? extends UniProtEntry> entries);

    protected Protein getProtein(UniProtEntry entry) {
        Protein.ProteinBuilder builder = Protein.builder();
        builder.proteinId(entry.getUniProtId().getValue());
        builder.name(getDescription(entry.getProteinDescription()));
        builder.accession(entry.getPrimaryUniProtAccession().getValue());
        builder.gene(getGene(entry.getGenes()));
        return builder.build();
    }

    protected List<Disease> getDiseases(UniProtEntry entry){

        List<DiseaseCommentStructured> dcs = entry.getComments(CommentType.DISEASE);

        List<Disease> diseases = dcs
                .parallelStream()
                .filter(dc -> dc.hasDefinedDisease())
                .map(dc -> getDisease(dc))
                .collect(Collectors.toList());

        return diseases;
    }

    protected Disease getDisease(DiseaseCommentStructured dcs){
        uk.ac.ebi.kraken.interfaces.uniprot.comments.Disease upDisease = dcs.getDisease();
        String descr = upDisease.getDescription().getValue();
        String name = upDisease.getDiseaseId().getValue();
        String acronym = upDisease.getAcronym().getValue();
        Disease.DiseaseBuilder builder = Disease.builder();
        builder.name(name).acronym(acronym).description(descr);
        return builder.build();

    }

    protected String getDescription(ProteinDescription pd) {
        Name name;

        if (pd.hasRecommendedName()) {
            name = pd.getRecommendedName();
        } else {
            name = pd.getSubNames().get(Constants.ZERO);
        }

        return name.getFieldsByType(FieldType.FULL).get(Constants.ZERO).getValue();
    }

    protected String getGene(List<Gene> genes) {
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
    protected boolean isListEmpty(List<?> list) {
        return list == null || list.isEmpty();
    }

    protected Protein findProteinbyProteinId(String proteinId) {
        MongoOperations mongoOperations = getTemplate();
        Query query = new Query();
        query.addCriteria(Criteria.where("proteinId").is(proteinId));

        return mongoOperations.findOne(query, Protein.class);
    }

    protected List<uk.ac.ebi.kraken.interfaces.uniprot.comments.Interaction> getInteractions(UniProtEntry entry) {
        List<InteractionComment> comments = entry.getComments(CommentType.INTERACTION);

        List<uk.ac.ebi.kraken.interfaces.uniprot.comments.Interaction> interactions = comments.parallelStream().map(InteractionComment::getInteractions)
                .flatMap(List::stream).collect(Collectors.toList());

        return interactions;
    }

    protected void updateProtein(Protein protein, Variant variant) {
        List<String> vids = protein.getVariantIds();

        if(vids == null){
            vids = new ArrayList<>();
        }

        vids.add(variant.get_id());
        protein.setVariantIds(vids);

        // update the vids in protein
        getTemplate().save(protein);

    }

    protected void updateProtein(Protein protein, Interaction interaction) {
        List<String> interactionIds = protein.getInteractionIds();

        if(interactionIds == null){
            interactionIds = new ArrayList<>();
        }

        interactionIds.add(interaction.get_id());
        protein.setInteractionIds(interactionIds);

        // update the interactionIds in protein
        getTemplate().save(protein);

    }

    protected void updateProtein(Protein protein, Pathway pathway) {
        List<String> pathwayIds = protein.getPathwayIds();

        if(pathwayIds == null){
            pathwayIds = new ArrayList<>();
        }

        pathwayIds.add(pathway.get_id());
        protein.setPathwayIds(pathwayIds);

        // update the pathway ids in protein
        getTemplate().save(protein);

    }

    protected List<DatabaseCrossReference> getPathways(UniProtEntry entry) {
        List<DatabaseCrossReference> dbXRefs = entry.getDatabaseCrossReferences(DatabaseType.REACTOME);
        return dbXRefs;
    }

    protected Disease findDiseaseByName(String name) {
        MongoOperations mongoOperations = getTemplate();
        Query query = new Query();
        query.addCriteria(Criteria.where("name").is(name));
        Disease disease = mongoOperations.findOne(query, Disease.class);
        return disease;
    }

    protected List<String> mergeTwoLists(List<String> iIds1, List<String> iIds2) {

        Set<String> uIds = new HashSet<>();

        if (iIds1 != null) {
            uIds.addAll(iIds1);
        }

        if (iIds2 != null) {
            uIds.addAll(iIds2);
        }

        List<String> uniqueIds = new ArrayList<>();
        uniqueIds.addAll(uIds);
        return uniqueIds;
    }


}
