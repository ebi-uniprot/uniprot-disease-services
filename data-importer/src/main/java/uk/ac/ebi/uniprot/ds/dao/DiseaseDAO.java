/*
 * Created by sahmad on 1/23/19 8:43 PM
 * UniProt Consortium.
 * Copyright (c) 2002-2019.
 *
 */

package uk.ac.ebi.uniprot.ds.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.ebi.uniprot.ds.model.Disease;

import java.util.Optional;

public interface DiseaseDAO extends JpaRepository<Disease, Long> {
    @Transactional
    Optional<Disease> findByDiseaseId(String diseaseId);

    @Transactional
    void deleteByDiseaseId(String diseaseId);
}
