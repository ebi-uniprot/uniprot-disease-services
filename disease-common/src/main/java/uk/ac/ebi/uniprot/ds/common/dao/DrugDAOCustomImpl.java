package uk.ac.ebi.uniprot.ds.common.dao;

import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

@Repository
public class DrugDAOCustomImpl implements DrugDAOCustom {
    /* do not change the order of the field. See method getDrugDTOsByDiseaseId in DrugService*/
    private static final String QUERY_DRUGS_BY_DISEASE_ID = "\n" +
            "select dr.name as name, dr.source_type as sourceType, dr.source_id as sourceId,\n" +
            "dr.molecule_type as moleculeType, dr.clinical_trial_phase as clinicalTrialPhase, \n" +
            "dr.mechanism_of_action as mechanismOfAction, dr.clinical_trial_link as clinicalTrialLink, dre.ref_url as evidences, p.accession as proteins, \n" +
            "d.disease_name as diseaseName, d.disease_id as diseaseId from (WITH RECURSIVE cd AS (\n" +
            "   SELECT id from ds_disease where disease_id = ? \n" +
            "   UNION ALL\n" +
            "   SELECT dr.ds_disease_id\n" +
            "   FROM ds_disease_relation AS dr\n" +
            "      JOIN cd ON cd.id = dr.ds_disease_parent_id\n" +
            ") \n" +
            "SELECT cd.id FROM cd ) ch \n" +
            "join ds_disease d on ch.id = d.id \n" +
            "join ds_disease_protein dp on d.id = dp.ds_disease_id \n" +
            "join ds_protein p on p.id = dp.ds_protein_id\n" +
            "join ds_protein_cross_ref pcr on pcr.ds_protein_id = p.id\n" +
            "join ds_drug dr on dr.ds_protein_cross_ref_id = pcr.id  \n" +
            "left join ds_drug_evidence dre on dre.ds_drug_id = dr.id\n" +
            "order by 1;";

    @PersistenceContext
    EntityManager entityManager;

    @Override
    public List<Object[]> getDrugsByDiseaseId(String diseaseId) {
        Query query = this.entityManager.createNativeQuery(QUERY_DRUGS_BY_DISEASE_ID);
        query.setParameter(1, diseaseId);
        List<Object[]> result = query.getResultList();
        return result;
    }
}
