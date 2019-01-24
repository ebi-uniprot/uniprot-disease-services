/*
 * Created by sahmad on 1/24/19 8:01 PM
 * UniProt Consortium.
 * Copyright (c) 2002-2019.
 *
 */

package uk.ac.ebi.uniprot.ds.dao.impl;

import uk.ac.ebi.uniprot.ds.dao.AbstractDAO;
import uk.ac.ebi.uniprot.ds.dao.ProteinDAO;
import uk.ac.ebi.uniprot.ds.model.Protein;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.util.Optional;

public class ProteinDAOImpl extends AbstractDAO<Protein> implements ProteinDAO {
    private EntityManager entityManager;
    public ProteinDAOImpl(EntityManager entityManager) {
        super(entityManager);
        this.entityManager = entityManager;
        setClassh(Protein.class);
    }

    @Override
    public Optional<Protein> getProteinById(String proteinId) {
        CriteriaBuilder builder = this.entityManager.getCriteriaBuilder();
        CriteriaQuery<Protein> criteriaQuery = builder.createQuery(Protein.class);
        Root<Protein> from = criteriaQuery.from(Protein.class);
        ParameterExpression<String> paramProteinId = builder.parameter(String.class);
        Predicate predicate = builder.equal(from.get("proteinId"), paramProteinId);
        criteriaQuery.select(from).where(predicate);
        TypedQuery<Protein> query = this.entityManager.createQuery(criteriaQuery);
        query.setParameter(paramProteinId, proteinId);
        try {
            return Optional.of(query.getSingleResult());
        }catch (NoResultException nre) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<Protein> getProteinByAccession(String accession) {
        TypedQuery<Protein> query = this.entityManager.createQuery(
                "SELECT p from Protein p WHERE p.accession = :accession",
                Protein.class);
        query.setParameter("accession", accession);
        try{
            return Optional.of(query.getSingleResult());
        }catch (NoResultException nre) {
            return Optional.empty();
        }
    }
}
