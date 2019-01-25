/*
 * Created by sahmad on 1/25/19 1:38 PM
 * UniProt Consortium.
 * Copyright (c) 2002-2019.
 *
 */

package uk.ac.ebi.uniprot.ds.dao;

import uk.ac.ebi.uniprot.ds.model.*;

import java.util.Optional;

public interface VariantDAO extends BaseDAO<Variant> {

    Optional<Variant> getVariantByFeatureLocation(FeatureLocation fl);

    Optional<Variant> getVariantByEvidence(Evidence evidence);

    Optional<Variant> getVariantByProtein(Protein protein);

    Optional<Variant> getVariantByDisease(Disease disease);
}
