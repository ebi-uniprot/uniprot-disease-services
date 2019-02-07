/*
 * Created by sahmad on 1/25/19 1:38 PM
 * UniProt Consortium.
 * Copyright (c) 2002-2019.
 *
 */

package uk.ac.ebi.uniprot.ds.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.ac.ebi.uniprot.ds.model.*;

import java.util.List;
import java.util.Optional;

public interface VariantDAO extends JpaRepository<Variant, Long> {

    Optional<Variant> findByFeatureLocation(FeatureLocation fl);

    List<Variant> findAllByProtein(Protein protein);

    List<Variant> findAllByDisease(Disease disease);

    List<Variant> findAllByReportContainingAndFeatureIdIsNotNull(String diseaseAcronym);
}
