/*
 * Created by sahmad on 21/01/19 14:46
 * UniProt Consortium.
 * Copyright (c) 2002-2019.
 *
 */

package uk.ac.ebi.diseaseservice.writer;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import uk.ac.ebi.diseaseservice.model.Disease;
import uk.ac.ebi.diseaseservice.model.Protein;
import uk.ac.ebi.diseaseservice.model.Variant;
import uk.ac.ebi.kraken.interfaces.uniprot.UniProtEntry;
import uk.ac.ebi.kraken.interfaces.uniprot.features.FeatureType;
import uk.ac.ebi.kraken.interfaces.uniprot.features.VariantFeature;
import uk.ac.ebi.kraken.interfaces.uniprot.features.VariantReport;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class DiseaseVariantWriter extends BaseSwissProtWriter {
    @Override
    protected void doWrite(List<? extends UniProtEntry> entries) {
        MongoOperations mongoOperations = getTemplate();
        for(UniProtEntry entry : entries){
            Protein protein = findProteinbyProteinId(entry.getUniProtId().getValue());
            List<Disease> diseases = getDiseases(entry);
            // update variant for each disease
            for (Disease d : diseases) {
                Disease storedDisease = findDiseaseByName(d.getName());
                if(protein.getVariantIds() != null && !protein.getVariantIds().isEmpty()) {
                    List<String> vIds = getDiseaseVariants(d.getAcronym(), protein.getVariantIds());
                    List<String> existingVids = storedDisease.getVariantIds();
                    if (existingVids != null) {
                        vIds.addAll(existingVids);
                    }
                    storedDisease.setVariantIds(vIds);
                    mongoOperations.save(storedDisease);
                }
            }

        }
    }


    private List<String> getDiseaseVariants(String acronym, List<String> pVids){
        MongoOperations mongoOp = getTemplate();
        Query query = new Query();
        query.addCriteria(Criteria.where("_id").in(pVids));
        List<Variant> variants = mongoOp.find(query, Variant.class);
        List<String> diseaseVids = getDiseaseVariantIds(acronym, variants);
        return diseaseVids;
    }

    private List<String> getDiseaseVariantIds(String acronym, List<Variant> variants) {
        List<String> dVids = new ArrayList<>();
        if(!StringUtils.isEmpty(acronym)) {
            for (Variant v : variants) {
                if(!(v.getVariantReports().isEmpty()) && v.getVariantReports().get(0).getValue().contains(acronym)){
                    dVids.add(v.get_id());
                }
            }
        }
        return dVids;
    }
}

