/*
 * Created by sahmad on 1/25/19 11:18 AM
 * UniProt Consortium.
 * Copyright (c) 2002-2019.
 *
 */

package uk.ac.ebi.uniprot.ds.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.ac.ebi.uniprot.ds.model.Pathway;
import uk.ac.ebi.uniprot.ds.model.Protein;

import java.util.List;

public interface PathwayDAO extends JpaRepository<Pathway, Long> {
    List<Pathway> findAllByProtein(Protein protein);
}
