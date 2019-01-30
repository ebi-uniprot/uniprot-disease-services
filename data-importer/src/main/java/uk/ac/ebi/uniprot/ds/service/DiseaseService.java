/*
 * Created by sahmad on 1/28/19 8:59 AM
 * UniProt Consortium.
 * Copyright (c) 2002-2019.
 *
 */

package uk.ac.ebi.uniprot.ds.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.ebi.uniprot.ds.dao.DiseaseDAO;
import uk.ac.ebi.uniprot.ds.model.Disease;

import java.util.List;
import java.util.Optional;

@Service
public class DiseaseService {
    @Autowired
    private DiseaseDAO diseaseDAO;

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
        return this.diseaseDAO.findByDiseaseId(diseaseId);
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

    @Transactional
    public List<Disease> saveAll(List<Disease> diseases){
        return this.diseaseDAO.saveAll(diseases);
    }

    public Optional<Disease> findByDiseaseIdOrNameOrAcronym(String diseaseId, String diseaseName, String acronym) {
        return this.diseaseDAO.findDiseaseByDiseaseIdOrNameOrAcronym(diseaseId, diseaseName, acronym);
    }
}
