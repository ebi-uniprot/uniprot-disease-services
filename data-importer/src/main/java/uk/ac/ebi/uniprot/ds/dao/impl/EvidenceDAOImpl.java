/*
 * Created by sahmad on 1/25/19 8:52 AM
 * UniProt Consortium.
 * Copyright (c) 2002-2019.
 *
 */

package uk.ac.ebi.uniprot.ds.dao.impl;

import uk.ac.ebi.uniprot.ds.dao.AbstractDAO;
import uk.ac.ebi.uniprot.ds.model.Evidence;

import javax.persistence.EntityManager;

public class EvidenceDAOImpl extends AbstractDAO<Evidence> {
    public EvidenceDAOImpl(EntityManager entityManager) {
        super(entityManager);
        setClassh(Evidence.class);
    }
}
