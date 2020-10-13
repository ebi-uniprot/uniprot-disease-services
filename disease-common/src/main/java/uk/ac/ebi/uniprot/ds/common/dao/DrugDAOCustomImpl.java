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
            "select " +
            "drug1.name as name, drug1.source_type as sourceType, drug1.source_id as sourceId, " +
            "drug1.molecule_type as moleculeType, drug1.clinical_trial_phase as clinicalTrialPhase, " +
            "drug1.mechanism_of_action as mechanismOfAction, drug1.clinical_trial_link as clinicalTrialLink, " +
            "dre.ref_url as evidences, dp.accession as proteins, " +
            "coalesce(dis.disease_name, drug1.chembl_disease_id) as diseaseName, " +
            "coalesce(dis.disease_id , drug1.chembl_disease_id) as diseaseId, " +
            "drug1.ds_disease_id as did, drug1.id as drugId " +
            "from ds_drug drug1 join " +
            "(select distinct drug.\"name\" from ds_disease dd " +
            "join ds_disease_descendent ddd on " +
            "dd.id = ddd.ds_disease_id " +
            "join ds_drug drug on drug.ds_disease_id = ddd.ds_descendent_id " +
            "where dd.disease_id=?) disease_drugs " +
            "on drug1.\"name\"=disease_drugs.\"name\" " +
            "left join ds_disease dis on dis.id=drug1.ds_disease_id " +
            "left join ds_protein_cross_ref dpc on dpc.id = drug1.ds_protein_cross_ref_id " +
            "left join ds_protein dp on dp.id = dpc.ds_protein_id " +
            "left join ds_drug_evidence dre on dre.ds_drug_id = drug1.id " +
            "order by drugId";

    private static final String QUERY_DRUGS_BY_PROTEIN_ACCESSION = "" +
            "select dd2.* from ds_drug dd2 join ( " +
            "select distinct dd1.\"name\" from ds_protein dp  " +
            "join ds_protein_cross_ref dpc " +
            "on dp.id = dpc.ds_protein_id " +
            "join ds_drug dd1 " +
            "on dd1.ds_protein_cross_ref_id = dpc.id " +
            "where dp.accession = ? ) protein_drugs " +
            "on dd2.\"name\" = protein_drugs.name order by  1";


    @PersistenceContext
    EntityManager entityManager;

    @Override
    public List<Object[]> getDrugsByDiseaseId(String diseaseId) {
        Query query = this.entityManager.createNativeQuery(QUERY_DRUGS_BY_DISEASE_ID);
        query.setParameter(1, diseaseId);
        return query.getResultList();
    }

    @Override
    public List<Drug> getDrugsByProtein(String accession) {
        Query query = this.entityManager.createNativeQuery(QUERY_DRUGS_BY_PROTEIN_ACCESSION, Drug.class);
        query.setParameter(1, accession);
        return query.getResultList();
    }
}
