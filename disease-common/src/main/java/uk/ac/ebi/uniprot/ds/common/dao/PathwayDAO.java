/*
 * Created by sahmad on 07/02/19 10:37
 * UniProt Consortium.
 * Copyright (c) 2002-2019.
 *
 */

package uk.ac.ebi.uniprot.ds.common.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.ac.ebi.uniprot.ds.common.model.Pathway;
import uk.ac.ebi.uniprot.ds.common.model.Protein;

import java.util.List;

public interface PathwayDAO extends JpaRepository<Pathway, Long> {
    List<Pathway> findAllByProtein(Protein protein);
}
