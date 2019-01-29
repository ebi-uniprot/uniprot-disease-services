/*
 * Created by sahmad on 1/25/19 9:31 AM
 * UniProt Consortium.
 * Copyright (c) 2002-2019.
 *
 */

package uk.ac.ebi.uniprot.ds.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.ac.ebi.uniprot.ds.model.Interaction;
import uk.ac.ebi.uniprot.ds.model.Protein;

import java.util.List;

public interface InteractionDAO extends JpaRepository<Interaction, Long> {
    List<Interaction> findAllByProtein(Protein protein);
}
