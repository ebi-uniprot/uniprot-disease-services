/*
 * Created by sahmad on 07/02/19 12:19
 * UniProt Consortium.
 * Copyright (c) 2002-2019.
 *
 */

package uk.ac.ebi.uniprot.ds.rest.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import uk.ac.ebi.uniprot.ds.common.dao.DiseaseDAO;
import uk.ac.ebi.uniprot.ds.common.model.Disease;
import uk.ac.ebi.uniprot.ds.common.model.DiseaseProtein;
import uk.ac.ebi.uniprot.ds.common.model.Protein;
import uk.ac.ebi.uniprot.ds.rest.exception.AssetNotFoundException;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class DiseaseService {
    @Autowired
    private DiseaseDAO diseaseDAO;
    @Autowired
    private ProteinService proteinService;

    @Transactional
    public Disease createUpdateDisease(String diseaseId, String diseaseName, String description, String acronym){
        Disease.DiseaseBuilder builder = Disease.builder();
        // get the disease by diseaseId
        Optional<Disease> optDisease = this.diseaseDAO.findByDiseaseId(diseaseId);

        if(optDisease.isPresent() && optDisease.get().getId() != null){ // update the existing record
            builder.name(diseaseName);
            builder.desc(description);
            builder.acronym(acronym);
        } else { // create a new record
            builder.diseaseId(diseaseId);
            builder.name(diseaseName);
            builder.desc(description);
            builder.acronym(acronym);
        }
        Disease nDisease = builder.build();
        return this.diseaseDAO.save(nDisease);
    }

    @Transactional
    public Disease createUpdateDisease(Disease disease){
        return this.diseaseDAO.save(disease);
    }

    public Optional<Disease> findByDiseaseId(String diseaseId){
        Optional<Disease> optDisease = this.diseaseDAO.findByDiseaseId(diseaseId);
        if(!optDisease.isPresent()){
            throw new AssetNotFoundException("Unable to find the diseaseId '" + diseaseId + "'.");
        }
        return optDisease;
    }

    public Optional<Disease> findById(Long id){
        return this.diseaseDAO.findById(id);
    }

    @Transactional
    public void deleteDiseaseById(Long id){
        this.diseaseDAO.deleteById(id);
    }

    @Transactional
    public void deleteDiseaseByDiseaseId(String diseaseId){
        this.diseaseDAO.deleteByDiseaseId(diseaseId);
    }

    public List<Disease> searchDiseases(String keyword, Integer offset, Integer size) {

        log.info("Searching diseases with the name {}, offset {} and size {}", keyword, offset, size);

        PageRequest pageRequest = PageRequest.of(offset, size, Sort.by("id"));

        List<Disease> diseases = this.diseaseDAO.findByNameContainingIgnoreCaseOrDescContainingIgnoreCase(keyword.toLowerCase(),
                keyword.toLowerCase(), pageRequest);

        return diseases;
    }

    public List<Disease> getDiseasesByProteinAccession(String accession) {
        return this.proteinService.getProteinByAccession(accession)
                .map(
                        prot -> prot.getDiseaseProteins()
                                .stream()
                                .map(dp -> dp.getDisease())
                                .collect(Collectors.toList())
                    )
                .orElse(null);
    }

    public List<Disease> getDiseasesByDrugName(String drugName) {
        List<Protein> proteins = this.proteinService.getProteinsByDrugName(drugName);
        List<Disease> diseases = new ArrayList<>();
        if(!CollectionUtils.isEmpty(proteins)) {
            // get the diseases from proteins
            Set<Disease> diseasesSet = proteins
                    .stream()
                    .filter(protein -> !CollectionUtils.isEmpty(protein.getDiseaseProteins()))
                    .map(p -> p.getDiseaseProteins())
                    .flatMap(Set::stream)
                    .map(dp -> dp.getDisease())
                    .collect(Collectors.toSet());

            diseases.addAll(diseasesSet);
        }

        return diseases;
    }
}
