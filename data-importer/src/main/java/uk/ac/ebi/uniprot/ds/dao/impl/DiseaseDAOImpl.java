/*
 * Created by sahmad on 1/24/19 9:07 AM
 * UniProt Consortium.
 * Copyright (c) 2002-2019.
 *
 */

package uk.ac.ebi.uniprot.ds.dao.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.uniprot.ds.dao.DiseaseDAO;
import uk.ac.ebi.uniprot.ds.model.Disease;

import javax.persistence.EntityManager;


public class DiseaseDAOImpl extends DiseaseDAO {
    private static final Logger LOGGER = LoggerFactory.getLogger(DiseaseDAOImpl.class);

    public DiseaseDAOImpl(EntityManager entityManager) { //TODO use DI
        super(entityManager);
        setClassh(Disease.class);
    }

}
