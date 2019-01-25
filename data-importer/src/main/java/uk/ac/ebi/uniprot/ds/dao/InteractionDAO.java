/*
 * Created by sahmad on 1/25/19 9:31 AM
 * UniProt Consortium.
 * Copyright (c) 2002-2019.
 *
 */

package uk.ac.ebi.uniprot.ds.dao;

import uk.ac.ebi.uniprot.ds.model.Interaction;
import uk.ac.ebi.uniprot.ds.model.Protein;

import java.util.List;

public interface InteractionDAO extends BaseDAO<Interaction> {
    List<Interaction> getInteractionsByProtein(Protein protein);
}
