/*
 * Created by sahmad on 01/02/19 10:25
 * UniProt Consortium.
 * Copyright (c) 2002-2019.
 *
 */

package uk.ac.ebi.uniprot.ds.processor;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import uk.ac.ebi.kraken.interfaces.uniprot.UniProtEntry;
import uk.ac.ebi.kraken.interfaces.uniprot.features.FeatureType;
import uk.ac.ebi.kraken.interfaces.uniprot.features.VariantFeature;
import uk.ac.ebi.uniprot.ds.dao.VariantDAO;
import uk.ac.ebi.uniprot.ds.model.Disease;
import uk.ac.ebi.uniprot.ds.model.FeatureLocation;
import uk.ac.ebi.uniprot.ds.model.Protein;
import uk.ac.ebi.uniprot.ds.model.Variant;

import java.util.*;

public class VariantProcessor implements ItemProcessor<UniProtEntry, UniProtEntry> {
    private List<Protein> proteins;

    public VariantProcessor(List<Protein> proteins){
        this.proteins = proteins;
    }

    @Override
    public UniProtEntry process(UniProtEntry entry) throws Exception {
        // get the last protein and enrich it
        int size = this.proteins.size();

        Protein protein = this.proteins.get(size - 1);
        // set variants on protein
        List<Variant> variants = getVariants(entry, protein);
        protein.addVariants(variants);

        // set variants on diseases
        Set<Disease> diseases = protein.getDiseases();
        for(Disease disease: diseases){
            List<Variant> dVars = new ArrayList<>();
            String acronym = disease.getAcronym();
            for(Variant variant:variants){
                if(variant.getReport() != null && variant.getReport().contains(acronym)){
                    variant.setDisease(disease);
                    dVars.add(variant);
                }
            }
            disease.addVariants(dVars);
        }
        return entry;
    }

    private List<Variant> getVariants(UniProtEntry entry, Protein protein) {
        Collection<VariantFeature> vfs = entry.getFeatures(FeatureType.VARIANT);

        List<Variant> variants = new ArrayList<>();

        for(VariantFeature vf : vfs){
            Variant.VariantBuilder builder = Variant.builder();
            builder.origSeq(vf.getOriginalSequence().getValue());
            builder.altSeq(!vf.getAlternativeSequences().isEmpty() ? vf.getAlternativeSequences().get(0).getValue():null);
            builder.featureId(vf.getFeatureId().getValue()).report(vf.getVariantReport().getValue());

            // create feature location
            uk.ac.ebi.kraken.interfaces.uniprot.features.FeatureLocation upFl = vf.getFeatureLocation();
            FeatureLocation.FeatureLocationBuilder flBuilder = FeatureLocation.builder();
            flBuilder.startId(upFl.getStart()).endId(upFl.getEnd());
            flBuilder.startModifier(upFl.getStartModifier().name()).endModifier(upFl.getEndModifier().name());
            FeatureLocation fl = flBuilder.build();

            // set the fl to variant
            builder.featureLocation(fl);

            // create evidence TODO what to do if more than one evidences
            // FIXME evidence should have variant id, a variant can have many evidences

            //builder.evidenceIds(vf.getEvidenceIds());
            builder.featureStatus(vf.getFeatureStatus().getName());
            builder.protein(protein);
            Variant variant = builder.build();
            variants.add(variant);
        }

        return variants;
    }
}
