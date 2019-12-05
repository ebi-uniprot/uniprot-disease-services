package uk.ac.ebi.uniprot.ds.common.dao;

import org.springframework.stereotype.Repository;
import uk.ac.ebi.uniprot.ds.common.model.Disease;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

@Repository
public class DiseaseDAOCustomImpl implements DiseaseDAOCustom {
    private static final String QUERY_DISEASE_CHILDREN_BY_DISEASE_ID = "select d.* from (WITH RECURSIVE cd AS ( " +
            "   SELECT id from ds_disease where disease_id = ? " +
            "   UNION ALL " +
            "   SELECT dr.ds_disease_id " +
            "   FROM ds_disease_relation AS dr " +
            "     JOIN cd ON cd.id = dr.ds_disease_parent_id " +
            ") " +
            "SELECT cd.id FROM cd ) ch " +
            "join ds_disease d on d.id = ch.id ";

    @PersistenceContext
    EntityManager entityManager;

    @Override
    public List<Disease> getDiseaseAndItsChildren(String diseaseId) {
        Query query = this.entityManager.createNativeQuery(QUERY_DISEASE_CHILDREN_BY_DISEASE_ID, Disease.class);
        query.setParameter(1, diseaseId);
        List<Disease> result = query.getResultList();
        return result;
    }
}
