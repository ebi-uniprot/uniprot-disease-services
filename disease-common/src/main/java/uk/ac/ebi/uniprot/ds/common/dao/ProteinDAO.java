/*
 * Created by sahmad on 07/02/19 10:37
 * UniProt Consortium.
 * Copyright (c) 2002-2019.
 *
 */

package uk.ac.ebi.uniprot.ds.common.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import uk.ac.ebi.uniprot.ds.common.model.Protein;
import uk.ac.ebi.uniprot.ds.common.model.Variant;

import java.util.List;
import java.util.Optional;


@Repository
public interface ProteinDAO extends JpaRepository<Protein, Long>, ProteinDAOCustom {
    Optional<Protein> findByProteinId(String proteinId);
    Optional<Protein> findProteinByAccession(String accession);
    @Query(nativeQuery = true,
            value = "select p.* from ds_protein p " +
                    "join ds_protein_cross_ref pcr on p.id = pcr.ds_protein_id " +
                    "join ds_drug d on d.ds_protein_cross_ref_id = pcr.id " +
                    "where d.name = ?1")
    List<Protein> findAllByDrugName(String name);
}
