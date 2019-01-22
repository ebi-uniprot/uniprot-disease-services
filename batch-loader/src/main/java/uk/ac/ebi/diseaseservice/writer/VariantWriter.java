/*
 * Created by sahmad on 21/01/19 10:56
 * UniProt Consortium.
 * Copyright (c) 2002-2019.
 *
 */

package uk.ac.ebi.diseaseservice.writer;

import org.springframework.data.mongodb.core.MongoOperations;
import uk.ac.ebi.diseaseservice.model.Protein;
import uk.ac.ebi.diseaseservice.model.Variant;
import uk.ac.ebi.kraken.interfaces.uniprot.UniProtEntry;
import uk.ac.ebi.kraken.interfaces.uniprot.features.FeatureType;
import uk.ac.ebi.kraken.interfaces.uniprot.features.VariantFeature;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class VariantWriter extends BaseSwissProtWriter {
    @Override
    protected void doWrite(List<? extends UniProtEntry> entries) {
        MongoOperations mongoOperations = getTemplate();
        for(UniProtEntry entry : entries){
            Protein protein = findProteinbyProteinId(entry.getUniProtId().getValue());
            Collection<VariantFeature> vfs = entry.getFeatures(FeatureType.VARIANT);
            for(VariantFeature vf : vfs){
                Variant.VariantBuilder builder = Variant.builder();
                builder.originalSequence(vf.getOriginalSequence());
                builder.alternativeSequences(vf.getAlternativeSequences());
                builder.featureId(vf.getFeatureId()).variantReports(vf.getVariantReports());
                builder.featureLocation(vf.getFeatureLocation()).evidenceIds(vf.getEvidenceIds());
                builder.featureStatus(vf.getFeatureStatus());
                builder.proteinIds(Arrays.asList(protein.get_id()));
                Variant variant = builder.build();
                mongoOperations.save(variant);
                // update protein with interaction _id
                updateProtein(protein, variant);
            }
        }
    }
}

