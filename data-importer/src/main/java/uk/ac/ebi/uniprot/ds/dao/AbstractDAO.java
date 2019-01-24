/*
 * Created by sahmad on 1/24/19 9:32 AM
 * UniProt Consortium.
 * Copyright (c) 2002-2019.
 *
 */

package uk.ac.ebi.uniprot.ds.dao;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.uniprot.ds.exception.AssetNotFoundException;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;
import java.util.Optional;

public abstract class AbstractDAO<T> implements BaseDAO<T> {
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractDAO.class);

    private Class<T> classh;

    private EntityManager entityManager;

    public AbstractDAO(EntityManager entityManager){
        this.entityManager = entityManager;
    }

    public final void setClassh(Class<T> classh) {
        this.classh = classh;
    }

    @Override
    public Optional<T> get(Long entityId) {
        return Optional.ofNullable(this.entityManager.find(this.classh, entityId));
    }

    @Override
    public List<T> getAll(Integer offset, Integer maxReturn) {
        CriteriaBuilder criteriaBuilder = this.entityManager.getCriteriaBuilder();
        CriteriaQuery<T> criteriaQuery = criteriaBuilder.createQuery(this.classh);
        Root<T> from = criteriaQuery.from(this.classh);
        CriteriaQuery<T> select = criteriaQuery.select(from);
        TypedQuery<T> query = this.entityManager.createQuery(select);
        query.setFirstResult(offset);
        query.setMaxResults(maxReturn);

        List<T> entities = query.getResultList();

        return entities;
    }

    @Override
    public void createOrUpdate(T entity) {
        entityManager.persist(entity);
    }

    @Override
    public void delete(T entity) {
        entityManager.remove(entity);
    }

    @Override
    public void deleteById(Long entityId) {
        Optional<T> optT = get(entityId);

        if (!optT.isPresent()) {
            throw new AssetNotFoundException("Unable to find the asset with id " + entityId);
        }

        delete(optT.get());
    }
}
