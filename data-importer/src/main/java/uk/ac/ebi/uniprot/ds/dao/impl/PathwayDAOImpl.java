/*
 * Created by sahmad on 1/25/19 11:21 AM
 * UniProt Consortium.
 * Copyright (c) 2002-2019.
 *
 */

package uk.ac.ebi.uniprot.ds.dao.impl;

import uk.ac.ebi.uniprot.ds.dao.AbstractDAO;
import uk.ac.ebi.uniprot.ds.dao.PathwayDAO;
import uk.ac.ebi.uniprot.ds.model.Pathway;
import uk.ac.ebi.uniprot.ds.model.Protein;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.List;

public class PathwayDAOImpl extends AbstractDAO<Pathway> implements PathwayDAO {
    private final EntityManager entityManager;

    public PathwayDAOImpl(EntityManager entityManager) {
        super(entityManager);
        this.entityManager = entityManager;
        setClassh(Pathway.class);
    }

    @Override
    public List<Pathway> getPathwaysByProtein(Protein protein) {
        TypedQuery<Pathway> query = this.entityManager.createQuery(
                "SELECT p from Pathway p WHERE p.protein.id = :proteinId",
                Pathway.class);

        query.setParameter("proteinId", protein.getId());

        return query.getResultList();
    }
}
