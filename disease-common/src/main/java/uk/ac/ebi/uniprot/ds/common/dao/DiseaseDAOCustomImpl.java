package uk.ac.ebi.uniprot.ds.common.dao;

import org.springframework.stereotype.Repository;
import uk.ac.ebi.uniprot.ds.common.model.Disease;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

@Repository
public class DiseaseDAOCustomImpl implements DiseaseDAOCustom {
    private static final String QUERY_DISEASE_CHILDREN_BY_DISEASE_ID = "" +
            "select dd2.* from ds_disease dd2 " +
            "join (" +
            "select ddd.ds_descendent_id " +
            "from ds_disease dd " +
            "join ds_disease_descendent ddd on " +
            "dd.id = ddd.ds_disease_id " +
            "where dd.disease_id=?) disease_descendents " +
            "on dd2.id = disease_descendents.ds_descendent_id";

    @PersistenceContext
    EntityManager entityManager;

    @Override
    public List<Disease> getDiseaseAndItsChildren(String diseaseId) {
        Query query = this.entityManager.createNativeQuery(QUERY_DISEASE_CHILDREN_BY_DISEASE_ID, Disease.class);
        query.setParameter(1, diseaseId);
        return query.getResultList();
    }
}
