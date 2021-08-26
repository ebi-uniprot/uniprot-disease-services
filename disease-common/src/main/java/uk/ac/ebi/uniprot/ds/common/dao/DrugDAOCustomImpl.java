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
            "select drug.name as name, drug.source_type as sourceType, drug.source_id as sourceId, " +
            "drug.molecule_type as moleculeType, drug.clinical_trial_phase as clinicalTrialPhase, " +
            "drug.mechanism_of_action as mechanismOfAction, drug.clinical_trial_link as clinicalTrialLink, " +
            "dre.ref_url as evidences, dp.accession as proteins, " +
            "coalesce(dis.disease_name, drug.chembl_disease_id) as diseaseName, " +
            "coalesce(dis.disease_id , drug.chembl_disease_id) as diseaseId " +
            "from ds_disease dis " +
            "join " +
            "(" +
            "   select ddd.ds_descendent_id as desc_id from ds_disease dd " +
            "   join ds_disease_descendent ddd on dd.id = ddd.ds_disease_id " +
            "   where dd.disease_id = ? " +
            ") as dis_chil " +
            "on dis.id = dis_chil.desc_id " +
            "join ds_drug drug on drug.ds_disease_id = dis.id " +
            "left join ds_protein_cross_ref dpc on dpc.id = drug.ds_protein_cross_ref_id " +
            "left join ds_protein dp on dp.id = dpc.ds_protein_id " +
            "left join ds_drug_evidence dre on dre.ds_drug_id = drug.id " +
            "order by name ";

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
