/*
 * Created by sahmad on 1/24/19 8:06 PM
 * UniProt Consortium.
 * Copyright (c) 2002-2019.
 *
 */

package uk.ac.ebi.uniprot.ds.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.ac.ebi.uniprot.ds.model.Protein;


public interface ProteinDAO extends JpaRepository<Protein, Long> {
    Protein findByProteinId(String proteinId);
    Protein findByAccession(String accession);
}
