/*
 * Created by sahmad on 1/25/19 11:18 AM
 * UniProt Consortium.
 * Copyright (c) 2002-2019.
 *
 */

package uk.ac.ebi.uniprot.ds.dao;

import uk.ac.ebi.uniprot.ds.model.Pathway;
import uk.ac.ebi.uniprot.ds.model.Protein;

import java.util.List;

public interface PathwayDAO extends BaseDAO<Pathway> {
    List<Pathway> getPathwaysByProtein(Protein protein);
}
