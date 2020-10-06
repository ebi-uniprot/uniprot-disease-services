package uk.ac.ebi.uniprot.ds.common.dao;

import org.springframework.stereotype.Repository;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import uk.ac.ebi.uniprot.ds.common.model.Disease;

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

    private static final String QUERY_TO_FLATTEN_HIERARCHY = "" +
            "WITH RECURSIVE cd AS ( " +
            "SELECT id from ds_disease where id=? " +
            "UNION " +
            "SELECT dr.ds_disease_id " +
            "FROM ds_disease_relation AS dr " +
            "JOIN cd ON cd.id = dr.ds_disease_parent_id " +
            ") " +
            "SELECT ? as parentId, cd.id as childId FROM cd order by 2 ";
    // Performance is not good. Use table ds_disease_descendent instead
    @Override
    public List<Object[]> getParentAndItsDescendents(Long id) {
        Query query = this.entityManager.createNativeQuery(QUERY_TO_FLATTEN_HIERARCHY);
        query.setParameter(1, id);
        query.setParameter(2, id);
        return query.getResultList();
    }

    private static final String INSERT_DESCENDENTS = "" +
            "INSERT INTO ds_disease_descendent" +
            "(ds_disease_id, ds_descendent_id) " +
            "VALUES(?, ?) ";

    @Override
    public int insertDiseaseIdAndDescendentId(Long id, Long descendentId) {
        Query query = this.entityManager.createNativeQuery(INSERT_DESCENDENTS);
        query.setParameter(1, id);
        query.setParameter(2, descendentId);
        return query.executeUpdate();
    }

    @Override
    public void truncateDiseaseRelation() {
        Query query = this.entityManager.createNativeQuery("truncate table ds_disease_relation");
        query.executeUpdate();

    }

    @Override
    public void truncateDiseaseDescendent() {
        Query query = this.entityManager.createNativeQuery("truncate table ds_disease_descendent");
        query.executeUpdate();
    }

}
