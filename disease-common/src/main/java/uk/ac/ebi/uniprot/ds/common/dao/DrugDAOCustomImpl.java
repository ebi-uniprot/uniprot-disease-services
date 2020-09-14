package uk.ac.ebi.uniprot.ds.common.dao;

import org.springframework.stereotype.Repository;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import uk.ac.ebi.uniprot.ds.common.model.Drug;

@Repository
public class DrugDAOCustomImpl implements DrugDAOCustom {
    /* do not change the order of the field. See method getDrugDTOsByDiseaseId in DrugService*/
    private static final String QUERY_DRUGS_BY_DISEASE_ID = "\n" +
            "select \n" +
            "dd.name as name, dd.source_type as sourceType, dd.source_id as sourceId,\n" +
            "dd.molecule_type as moleculeType, dd.clinical_trial_phase as clinicalTrialPhase, \n" +
            "dd.mechanism_of_action as mechanismOfAction, dd.clinical_trial_link as clinicalTrialLink,\n" +
            "dre.ref_url as evidences, dp.accession as proteins, \n" +
            "coalesce(dis.disease_name, dd.chembl_disease_id) as diseaseName, \n" +
            "coalesce(dis.disease_id , dd.chembl_disease_id) as diseaseId\n" +
            "from ds_drug dd\n" +
            "join (\n" +
            "select dr.\"name\", d.disease_id, d.id \n" +
            "from ds_drug dr join\n" +
            "(WITH RECURSIVE cd AS (\n" +
            "   SELECT id from ds_disease where disease_id = ? \n" +
            "   UNION ALL\n" +
            "   SELECT dr.ds_disease_id\n" +
            "   FROM ds_disease_relation AS dr\n" +
            "      JOIN cd ON cd.id = dr.ds_disease_parent_id\n" +
            ") \n" +
            "SELECT cd.id FROM cd) disease_family\n" +
            "join ds_disease d on disease_family.id = d.id \n" +
            "on dr.ds_disease_id = d.id) tmp \n" +
            "on dd.\"name\"=tmp.\"name\" \n" +
            "left join ds_disease dis on dis.id=dd.ds_disease_id\n" +
            "join ds_protein_cross_ref dpc on dpc.id = dd.ds_protein_cross_ref_id \n" +
            "join ds_protein dp on dp.id = dpc.ds_protein_id\n" +
            "left join ds_drug_evidence dre on dre.ds_drug_id = dd.id\n" +
            "order by 1";

    private static final String QUERY_DRUGS_BY_PROTEIN_ACCESSION = "\n" +
            "select dd3.* from (\n" +
            "select dd.* from ds_protein dp \n" +
            "join ds_protein_cross_ref dpc \n" +
            "on dp.id = dpc.ds_protein_id \n" +
            "join ds_drug dd \n" +
            "on dd.ds_protein_cross_ref_id = dpc.id \n" +
            "where dp.accession = ? \n" +
            "union \n" +
            "select dd1.* from ds_protein dp \n" +
            "join ds_protein_cross_ref dpc \n" +
            "on dp.id = dpc.ds_protein_id \n" +
            "join ds_drug dd \n" +
            "on dd.ds_protein_cross_ref_id = dpc.id \n" +
            "join ds_drug dd1 on dd.\"name\" = dd1.\"name\" \n" +
            "where dp.accession = ?) dd3 order by 2";


    @PersistenceContext
    EntityManager entityManager;

    @Override
    public List<Object[]> getDrugsByDiseaseId(String diseaseId) {
        Query query = this.entityManager.createNativeQuery(QUERY_DRUGS_BY_DISEASE_ID);
        query.setParameter(1, diseaseId);
        List<Object[]> result = query.getResultList();
        return result;
    }

    @Override
    public List<Drug> getDrugsByProtein(String accession) {
        Query query = this.entityManager.createNativeQuery(QUERY_DRUGS_BY_PROTEIN_ACCESSION, Drug.class);
        query.setParameter(1, accession);
        query.setParameter(2, accession);
        List<Drug> result = query.getResultList();
        return result;
    }

}
