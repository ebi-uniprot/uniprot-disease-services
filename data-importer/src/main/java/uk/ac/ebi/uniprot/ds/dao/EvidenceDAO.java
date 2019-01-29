/*
 * Created by sahmad on 28/01/19 22:50
 * UniProt Consortium.
 * Copyright (c) 2002-2019.
 *
 */

package uk.ac.ebi.uniprot.ds.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.ac.ebi.uniprot.ds.model.Evidence;

public interface EvidenceDAO extends JpaRepository<Evidence, Long> {
}
