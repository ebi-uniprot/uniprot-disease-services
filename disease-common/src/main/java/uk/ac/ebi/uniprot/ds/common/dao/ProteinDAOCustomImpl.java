/*
 * Created by sahmad on 07/02/19 10:37
 * UniProt Consortium.
 * Copyright (c) 2002-2019.
 *
 */

package uk.ac.ebi.uniprot.ds.common.dao;

import org.springframework.stereotype.Repository;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import uk.ac.ebi.uniprot.ds.common.model.Protein;

@Repository
public class ProteinDAOCustomImpl implements ProteinDAOCustom {
    // get proteins for the given disease id and its descendents
    private static final String QUERY_GET_PROTEINS_BY_DISEASE_ID = "" +
            "select dp.* " +
            "from ds_disease dd " +
            "join ds_disease_descendent ddd on " +
            "dd.id = ddd.ds_disease_id " +
            "join ds_disease_protein ddp " +
            "on ddp.ds_disease_id = ddd.ds_descendent_id " +
            "join ds_protein dp " +
            "on dp.id = ddp.ds_protein_id " +
            "where dd.disease_id=? group by 1";

    @PersistenceContext
    EntityManager entityManager;
    @Override
    public List<Protein> getProteinsByAccessions(List<String> accessions) {
        CriteriaBuilder cb =
                entityManager.getCriteriaBuilder();
        CriteriaQuery<Protein> cq =
                cb.createQuery(Protein.class);
        Root<Protein> root =
                cq.from(Protein.class);

        CriteriaBuilder.In<String> in = cb.in(root.get("accession"));
        accessions.forEach(acc -> in.value(acc));
        return   entityManager
                .createQuery(cq.select(root)
                        .where(in))
                        .getResultList();
    }


    @Override
    public List<Protein> getProteinsByDiseaseId(String diseaseId) {
        Query query = this.entityManager.createNativeQuery(QUERY_GET_PROTEINS_BY_DISEASE_ID, Protein.class);
        query.setParameter(1, diseaseId);
        return query.getResultList();
    }
}
