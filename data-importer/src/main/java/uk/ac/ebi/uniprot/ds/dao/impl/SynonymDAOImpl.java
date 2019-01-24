/*
 * Created by sahmad on 1/24/19 3:27 PM
 * UniProt Consortium.
 * Copyright (c) 2002-2019.
 *
 */

package uk.ac.ebi.uniprot.ds.dao.impl;

import uk.ac.ebi.uniprot.ds.dao.AbstractDAO;
import uk.ac.ebi.uniprot.ds.dao.SynonymDAO;
import uk.ac.ebi.uniprot.ds.model.Disease;
import uk.ac.ebi.uniprot.ds.model.Synonym;

import javax.persistence.EntityManager;
import java.util.List;

public class SynonymDAOImpl extends AbstractDAO<Synonym> implements SynonymDAO {

    private EntityManager entityManager;

    public SynonymDAOImpl(EntityManager entityManager) {
        super(entityManager);
        this.entityManager = entityManager;
        setClassh(Synonym.class);
    }

    @Override
    public List<Synonym> getSynonymsByDisease(Disease disease) {

        List<Synonym> synonyms = this.entityManager.createQuery(
                "SELECT sn " +
                        "FROM Synonym sn " +
                        "WHERE sn.disease.id = :diseaseId", Synonym.class)
                .setParameter( "diseaseId", disease.getId() )
                .getResultList();

        return synonyms;
    }
}
