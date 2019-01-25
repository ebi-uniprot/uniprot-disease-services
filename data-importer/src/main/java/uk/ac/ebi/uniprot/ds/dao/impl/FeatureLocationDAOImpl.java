/*
 * Created by sahmad on 1/25/19 9:17 AM
 * UniProt Consortium.
 * Copyright (c) 2002-2019.
 *
 */

package uk.ac.ebi.uniprot.ds.dao.impl;

import uk.ac.ebi.uniprot.ds.dao.AbstractDAO;
import uk.ac.ebi.uniprot.ds.model.FeatureLocation;

import javax.persistence.EntityManager;

public class FeatureLocationDAOImpl extends AbstractDAO<FeatureLocation> {

    public FeatureLocationDAOImpl(EntityManager entityManager) {
        super(entityManager);
        setClassh(FeatureLocation.class);
    }
}
