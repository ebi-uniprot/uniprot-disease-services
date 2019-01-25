/*
 * Created by sahmad on 1/25/19 9:32 AM
 * UniProt Consortium.
 * Copyright (c) 2002-2019.
 *
 */

package uk.ac.ebi.uniprot.ds.dao.impl;

import uk.ac.ebi.uniprot.ds.dao.AbstractDAO;
import uk.ac.ebi.uniprot.ds.dao.InteractionDAO;
import uk.ac.ebi.uniprot.ds.model.Interaction;
import uk.ac.ebi.uniprot.ds.model.Protein;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.List;

public class InteractionDAOImpl extends AbstractDAO<Interaction> implements InteractionDAO  {
    private final EntityManager entityManager;

    public InteractionDAOImpl(EntityManager entityManager) {
        super(entityManager);
        this.entityManager = entityManager;
        setClassh(Interaction.class);
    }

    @Override
    public List<Interaction> getInteractionsByProtein(Protein protein) {
        TypedQuery<Interaction> query = this.entityManager.createQuery(
                "SELECT i from Interaction i WHERE i.protein.id = :proteinId",
                Interaction.class);

        query.setParameter("proteinId", protein.getId());

        return query.getResultList();
    }
}
