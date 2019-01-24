/*
 * Created by sahmad on 1/24/19 8:06 PM
 * UniProt Consortium.
 * Copyright (c) 2002-2019.
 *
 */

package uk.ac.ebi.uniprot.ds.dao;

import uk.ac.ebi.uniprot.ds.model.Protein;

import java.util.Optional;

public interface ProteinDAO extends BaseDAO<Protein> {
    Optional<Protein> getProteinById(String proteinId);
    Optional<Protein> getProteinByAccession(String accession);
}
