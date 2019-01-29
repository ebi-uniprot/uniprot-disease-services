/*
 * Created by sahmad on 1/23/19 8:43 PM
 * UniProt Consortium.
 * Copyright (c) 2002-2019.
 *
 */

package uk.ac.ebi.uniprot.ds.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.ac.ebi.uniprot.ds.model.Disease;

public interface DiseaseDAO extends JpaRepository<Disease, Long> {
    // custom methods related to Disease only if any

}
