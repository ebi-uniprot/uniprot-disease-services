/*
 * Created by sahmad on 29/01/19 16:30
 * UniProt Consortium.
 * Copyright (c) 2002-2019.
 *
 */

package uk.ac.ebi.uniprot.ds.importer.writer;

import uk.ac.ebi.kraken.interfaces.uniprot.UniProtEntry;
import uk.ac.ebi.kraken.interfaces.uniprot.evidences.EvidenceId;
import uk.ac.ebi.kraken.interfaces.uniprot.features.FeatureType;
import uk.ac.ebi.kraken.interfaces.uniprot.features.VariantFeature;
import uk.ac.ebi.uniprot.ds.common.model.Evidence;
import uk.ac.ebi.uniprot.ds.common.model.FeatureLocation;
import uk.ac.ebi.uniprot.ds.common.model.Protein;
import uk.ac.ebi.uniprot.ds.common.model.Variant;

import java.util.*;

public class VariantHelper {

    public List<Variant> getProteinVariants(UniProtEntry entry, Protein protein){
        Collection<VariantFeature> vfs = entry.getFeatures(FeatureType.VARIANT);
        List<Variant> variants = new ArrayList<>();
        for(VariantFeature vf : vfs) {
            Variant.VariantBuilder builder = Variant.builder();
            builder.origSeq(vf.getOriginalSequence().getValue());
            builder.altSeq(!vf.getAlternativeSequences().isEmpty() ? vf.getAlternativeSequences().get(0).getValue() : null);
            builder.featureId(vf.getFeatureId().getValue()).report(vf.getVariantReport().getValue());

            // create feature location
            uk.ac.ebi.kraken.interfaces.uniprot.features.FeatureLocation upFl = vf.getFeatureLocation();
            FeatureLocation.FeatureLocationBuilder flBuilder = FeatureLocation.builder();
            flBuilder.startId(upFl.getStart()).endId(upFl.getEnd());
            flBuilder.startModifier(upFl.getStartModifier().name()).endModifier(upFl.getEndModifier().name());
            FeatureLocation fl = flBuilder.build();

            // set the fl to variant
            builder.featureLocation(fl);

            builder.featureStatus(vf.getFeatureStatus().getName());
            builder.protein(protein);
            Variant variant = builder.build();
            List<Evidence> evidences = getEvidences(vf, variant);
            variant.addEvidences(evidences);
            variants.add(variant);
        }
        return variants;
    }

    private List<Evidence> getEvidences(VariantFeature vf, Variant variant) {
        List<EvidenceId> eIds = vf.getEvidenceIds();
        List<Evidence> evidences = new ArrayList<>();
        for(EvidenceId eId: eIds){
            Evidence.EvidenceBuilder eBuilder = Evidence.builder();
            eBuilder.evidenceId(eId.getValue());
            eBuilder.type(eId.getType().getValue());
            eBuilder.attribute(eId.getAttribute().getValue());
            eBuilder.code(eId.getEvidenceCode().getDisplayName());
            eBuilder.useECOCode(eId.useECOCode());
            eBuilder.typeValue(eId.getTypeValue());
            if(Objects.isNull(eId.getTypeValue()) || eId.getTypeValue().trim().isEmpty()){
                eBuilder.hasTypeValue(false);
            } else {
                eBuilder.hasTypeValue(true);
            }
            Evidence evidence = eBuilder.build();
            evidence.setVariant(variant);
            evidences.add(evidence);
        }

        return evidences;
    }
}
