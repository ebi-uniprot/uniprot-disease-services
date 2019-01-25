/*
 * Created by sahmad on 1/25/19 1:44 PM
 * UniProt Consortium.
 * Copyright (c) 2002-2019.
 *
 */

package uk.ac.ebi.uniprot.ds.dao.impl;

import uk.ac.ebi.uniprot.ds.dao.AbstractDAO;
import uk.ac.ebi.uniprot.ds.dao.VariantDAO;
import uk.ac.ebi.uniprot.ds.model.*;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import java.util.Optional;

public class VariantDAOImpl extends AbstractDAO<Variant> implements VariantDAO {
    private final EntityManager entityManager;

    public VariantDAOImpl(EntityManager entityManager) {
        super(entityManager);
        this.entityManager = entityManager;
        setClassh(Variant.class);
    }

    @Override
    public Optional<Variant> getVariantByFeatureLocation(FeatureLocation fl) {
        TypedQuery<Variant> query = this.entityManager.createQuery(
                "SELECT v FROM Variant v WHERE v.featureLocation.id = :flId",
                Variant.class);
        query.setParameter("flId", fl.getId());
        try {
            return Optional.of(query.getSingleResult());

        } catch (NoResultException nre) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<Variant> getVariantByEvidence(Evidence evidence) {
        TypedQuery<Variant> query = this.entityManager.createQuery(
                "SELECT v FROM Variant v WHERE v.evidence.id = :evId",
                Variant.class);
        query.setParameter("evId", evidence.getId());

        try {
            return Optional.of(query.getSingleResult());
        } catch (NoResultException nre) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<Variant> getVariantByProtein(Protein protein) {
        TypedQuery<Variant> query = this.entityManager.createQuery(
                "SELECT v FROM Variant v WHERE v.protein.id = :pId",
                Variant.class);
        query.setParameter("pId", protein.getId());
        try {
            return Optional.of(query.getSingleResult());

        } catch (NoResultException nre) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<Variant> getVariantByDisease(Disease disease) {
        TypedQuery<Variant> query = this.entityManager.createQuery(
                "SELECT v FROM Variant v WHERE v.disease.id = :dId",
                Variant.class);
        query.setParameter("dId", disease.getId());
        try {
            return Optional.of(query.getSingleResult());

        } catch (NoResultException nre) {
            return Optional.empty();
        }
    }
}
