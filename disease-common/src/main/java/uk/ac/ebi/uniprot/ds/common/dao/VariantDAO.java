/*
 * Created by sahmad on 07/02/19 10:37
 * UniProt Consortium.
 * Copyright (c) 2002-2019.
 *
 */

package uk.ac.ebi.uniprot.ds.common.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import uk.ac.ebi.uniprot.ds.common.model.Disease;
import uk.ac.ebi.uniprot.ds.common.model.FeatureLocation;
import uk.ac.ebi.uniprot.ds.common.model.Protein;
import uk.ac.ebi.uniprot.ds.common.model.Variant;

import java.util.List;
import java.util.Optional;

public interface VariantDAO extends JpaRepository<Variant, Long> {

    Optional<Variant> findByFeatureLocation(FeatureLocation fl);

    List<Variant> findAllByProtein(Protein protein);

    List<Variant> findAllByDisease(Disease disease);

    @Query(nativeQuery = true,
            value = "select * from ds_variant where variant_report like %?1% and coalesce(TRIM(feature_id), '') != ''")
    List<Variant> findByReportContains(String diseaseAcronym);
}
