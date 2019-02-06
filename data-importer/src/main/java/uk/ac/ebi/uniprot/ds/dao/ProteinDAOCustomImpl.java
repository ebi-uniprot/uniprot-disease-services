/*
 * Created by sahmad on 06/02/19 12:02
 * UniProt Consortium.
 * Copyright (c) 2002-2019.
 *
 */

package uk.ac.ebi.uniprot.ds.dao;

import org.springframework.stereotype.Repository;
import uk.ac.ebi.uniprot.ds.model.Protein;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;

@Repository
public class ProteinDAOCustomImpl implements ProteinDAOCustom {
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
}
