/*
 * Created by sahmad on 1/23/19 8:43 PM
 * UniProt Consortium.
 * Copyright (c) 2002-2019.
 *
 */

package uk.ac.ebi.uniprot.ds.dao;

import uk.ac.ebi.uniprot.ds.model.Disease;

import javax.persistence.EntityManager;

public abstract class DiseaseDAO extends AbstractDAO<Disease>{
    public DiseaseDAO(EntityManager entityManager) {
        super(entityManager);
    }
    // custom methods related to Disease only if any
}
