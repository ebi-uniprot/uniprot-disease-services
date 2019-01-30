/*
 * Created by sahmad on 29/01/19 13:27
 * UniProt Consortium.
 * Copyright (c) 2002-2019.
 *
 */

package uk.ac.ebi.uniprot.ds.writer;

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

public class DiseaseWriter implements ItemWriter<UniProtEntry> {

    private final Map<String, Protein> proteinIdProteinMap;

    @Autowired
    private DiseaseService diseaseService;
    @Autowired
    private VariantDAO variantDAO;
    public DiseaseWriter(Map<String, Protein> proteinIdProteinMap){
        this.proteinIdProteinMap = proteinIdProteinMap;
    }

    @Override
    public void write(List<? extends UniProtEntry> entries){
        // get the list of proteins from entries
        for(UniProtEntry entry : entries){
            // get the protein from the uniprot entry
            Protein protein = this.proteinIdProteinMap.remove(entry.getUniProtId().getValue());
            assert protein != null;
            List<Disease> diseases = getDiseasesFromUniProtEntry(entry);
            upsertDiseases(protein, diseases);
        }
    }

    private void upsertDiseases(Protein protein, List<Disease> diseases) {
        for(Disease disease : diseases){

            Optional<Disease> optDisease = this.diseaseService.findByDiseaseIdOrNameOrAcronym
                    (disease.getDiseaseId(), disease.getName(), disease.getAcronym());

            Disease pDisease;
            if(optDisease.isPresent()){
                pDisease = optDisease.get();
                pDisease.setDiseaseId(disease.getDiseaseId());
                pDisease.getProteins().add(protein);
            } else {
                Set<Protein> proteins = new HashSet<>();
                proteins.add(protein);
                pDisease = disease;
                pDisease.setProteins(proteins);
            }
            this.diseaseService.createUpdateDisease(pDisease);
            // update the variant
            updateDiseaseVariants(pDisease);

        }
    }

    private void updateDiseaseVariants(Disease pDisease) {
        List<Variant> variants = this.variantDAO.findAllByReportContaining(pDisease.getAcronym());
        variants.forEach(v -> v.setDisease(pDisease));
        this.variantDAO.saveAll(variants);
    }

    private List<Disease> getDiseasesFromUniProtEntry(UniProtEntry entry) {
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
        String diseaseId = upDisease.getDiseaseId().getValue();
        String descr = upDisease.getDescription().getValue();
        String name = upDisease.getDiseaseId().getValue();
        String acronym = upDisease.getAcronym().getValue();
        Disease.DiseaseBuilder builder = Disease.builder();
        builder.diseaseId(diseaseId).name(name).acronym(acronym).desc(descr);
        return builder.build();

    }
}
