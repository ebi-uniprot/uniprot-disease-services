/*
 * Created by sahmad on 1/24/19 3:31 PM
 * UniProt Consortium.
 * Copyright (c) 2002-2019.
 *
 */

package uk.ac.ebi.uniprot.ds.dao;

import uk.ac.ebi.uniprot.ds.model.Disease;
import uk.ac.ebi.uniprot.ds.model.Synonym;

import java.util.List;

public interface SynonymDAO extends BaseDAO<Synonym>{
    List<Synonym> getSynonymsByDisease(Disease disease);
}
