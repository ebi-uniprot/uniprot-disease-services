/*
 * Created by sahmad on 01/02/19 10:40
 * UniProt Consortium.
 * Copyright (c) 2002-2019.
 *
 */

package uk.ac.ebi.uniprot.ds.processor;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import uk.ac.ebi.kraken.interfaces.uniprot.UniProtEntry;
import uk.ac.ebi.kraken.interfaces.uniprot.comments.CommentType;
import uk.ac.ebi.kraken.interfaces.uniprot.comments.DiseaseCommentStructured;
import uk.ac.ebi.uniprot.ds.dao.VariantDAO;
import uk.ac.ebi.uniprot.ds.model.Disease;
import uk.ac.ebi.uniprot.ds.model.Protein;
import uk.ac.ebi.uniprot.ds.model.Variant;
import uk.ac.ebi.uniprot.ds.service.DiseaseService;

import java.util.*;
import java.util.stream.Collectors;

public class DiseaseProcessor implements ItemProcessor<UniProtEntry, UniProtEntry> {

    private List<Protein> proteins;

    @Autowired
    private DiseaseService diseaseService;

    public DiseaseProcessor(List<Protein> proteins) {
        this.proteins = proteins;
    }

    @Override
    public UniProtEntry process(UniProtEntry entry) throws Exception {
        // get the last protein and enrich it
        int size = this.proteins.size();

        Protein protein = this.proteins.get(size - 1);
        Set<Disease> diseases = getDiseases(entry, protein);
        protein.setDiseases(diseases);

        return entry;
    }

    private Set<Disease> getDiseases(UniProtEntry entry, Protein protein) {
        List<DiseaseCommentStructured> dcs = entry.getComments(CommentType.DISEASE);

        Set<Disease> diseases = dcs
                .parallelStream()
                .filter(dc -> dc.hasDefinedDisease())
                .map(dc -> mergeDisease(dc, protein))
                .collect(Collectors.toSet());

        return diseases;
    }

    protected Disease mergeDisease(DiseaseCommentStructured dcs, Protein protein) {
        uk.ac.ebi.kraken.interfaces.uniprot.comments.Disease upDisease = dcs.getDisease();
        String diseaseId = upDisease.getDiseaseId().getValue();
        String descr = upDisease.getDescription().getValue();
        String name = upDisease.getDiseaseId().getValue();
        String acronym = upDisease.getAcronym().getValue();
        Disease.DiseaseBuilder builder = Disease.builder();
        builder.diseaseId(diseaseId).name(name).acronym(acronym).desc(descr);
        Disease disease = builder.build();

        if("Sitosterolemia".equals(diseaseId)){
            int found = 0;
        }

        // get the diseases created by hum disease or other protein
        Optional<Disease> optDisease = this.diseaseService.findByDiseaseIdOrNameOrAcronym(diseaseId, name, acronym);
        if (optDisease.isPresent()) {
            disease.setId(optDisease.get().getId());
            if(disease.getProteins() == null){
                disease.setProteins(new HashSet<>());
            }
            disease.getProteins().add(protein);
        } else {
            Set<Protein> proteins = new HashSet<>();
            proteins.add(protein);
            disease.setProteins(proteins);
        }

        return disease;
    }
}
