/*
 * Created by sahmad on 07/02/19 10:37
 * UniProt Consortium.
 * Copyright (c) 2002-2019.
 *
 */

package uk.ac.ebi.uniprot.ds.common.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.ac.ebi.uniprot.ds.common.model.ProteinCrossRef;
import uk.ac.ebi.uniprot.ds.common.model.Protein;

import java.util.List;

public interface ProteinCrossRefDAO extends JpaRepository<ProteinCrossRef, Long> {
    List<ProteinCrossRef> findAllByProtein(Protein protein);
    List<ProteinCrossRef> findAllByDbType(String dbType);
}
