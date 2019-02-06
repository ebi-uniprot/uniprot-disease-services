/*
 * Created by sahmad on 06/02/19 12:01
 * UniProt Consortium.
 * Copyright (c) 2002-2019.
 *
 */

package uk.ac.ebi.uniprot.ds.dao;

import uk.ac.ebi.uniprot.ds.model.Protein;

import java.util.List;

public interface ProteinDAOCustom {
    List<Protein> getProteinsByAccessions(List<String> accessions);
}
