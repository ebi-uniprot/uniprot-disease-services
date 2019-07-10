/*
 * Created by sahmad on 29/01/19 13:27
 * UniProt Consortium.
 * Copyright (c) 2002-2019.
 *
 */

package uk.ac.ebi.uniprot.ds.importer.writer;

import java.util.*;
import java.util.stream.Collectors;

import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;

import uk.ac.ebi.kraken.interfaces.uniprot.UniProtEntry;
import uk.ac.ebi.kraken.interfaces.uniprot.comments.CommentType;
import uk.ac.ebi.kraken.interfaces.uniprot.comments.DiseaseCommentStructured;
import uk.ac.ebi.kraken.interfaces.uniprot.evidences.EvidenceId;
import uk.ac.ebi.uniprot.ds.common.common.PublicationType;
import uk.ac.ebi.uniprot.ds.common.common.SourceType;
import uk.ac.ebi.uniprot.ds.common.dao.DiseaseDAO;
import uk.ac.ebi.uniprot.ds.common.dao.VariantDAO;
import uk.ac.ebi.uniprot.ds.common.model.*;

public class DiseaseWriter implements ItemWriter<UniProtEntry> {

    private final Map<String, Protein> proteinIdProteinMap;
    @Autowired
    private DiseaseDAO diseaseDAO;
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

            Optional<Disease> optDisease = this.diseaseDAO.findDiseaseByDiseaseIdOrNameOrAcronym
                    (disease.getDiseaseId(), disease.getName(), disease.getAcronym());

            Disease pDisease;
            if(optDisease.isPresent()){
                pDisease = optDisease.get();
                pDisease.setNote(disease.getNote());
                pDisease.setDiseaseId(disease.getDiseaseId());
                DiseaseProtein dp = new DiseaseProtein(pDisease, protein, false);
                pDisease.getDiseaseProteins().add(dp);
                pDisease.setPublications(disease.getPublications());
                // update the disease to the publications
                pDisease.getPublications().stream().forEach(pub -> pub.setDisease(pDisease));
            } else {
                Set<DiseaseProtein> dps = new HashSet<>();
                DiseaseProtein dp = new DiseaseProtein(disease, protein, false);
                dps.add(dp);
                pDisease = disease;
                pDisease.setDiseaseProteins(dps);
                pDisease.setSource(SourceType.UniProt.name());
            }
            List<Variant> variants = getDiseaseVariants(pDisease);
            pDisease.setVariants(variants);
            this.diseaseDAO.save(pDisease);
        }
    }

    private List<Variant> getDiseaseVariants(Disease pDisease) {
        String likeValue = "in " + pDisease.getAcronym() + ";";
        //String likeValue = "in " + pDisease.getAcronym() + ".";
        //in COD4 and ACHM5;
        //missing in ACHM5.
        //missing in ACHM5;
        List<Variant> variants = this.variantDAO.findByReportContains(likeValue);
        variants.forEach(v -> v.setDisease(pDisease));
        return variants;
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
        String desc = upDisease.getDescription().getValue();
        String name = upDisease.getDiseaseId().getValue();
        String acronym = upDisease.getAcronym().getValue();
        Disease.DiseaseBuilder builder = Disease.builder();
        builder.diseaseId(diseaseId).name(name).acronym(acronym).desc(desc)
        .note(getDiseaseNote(dcs));
        Disease disease = builder.build();
        List<Publication> pubs = getPublications(upDisease, disease);
        disease.setPublications(pubs);
        return disease;

    }
    
    private String getDiseaseNote(DiseaseCommentStructured dcs){
    	String result =dcs.getNote().getTexts().stream().map(val ->val.getValue())
    			.collect(Collectors.joining(". "));
    	if(result.isEmpty()) {
    		return result;
    	}else
    		return result+".";
    }

    private List<Publication> getPublications(uk.ac.ebi.kraken.interfaces.uniprot.comments.Disease upDisease, Disease disease) {
        List<EvidenceId> eIds = upDisease.getDescription().getEvidenceIds();

        List<Publication> pubs = eIds
                .stream()
                .filter(e -> PublicationType.PubMed.name().equals(e.getTypeValue()))
                .map(e -> new Publication(PublicationType.PubMed.name(), e.getAttribute().getValue(), disease))
                .collect(Collectors.toList());

        return pubs;
    }
}
