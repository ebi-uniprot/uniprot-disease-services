/*
 * Created by sahmad on 07/02/19 10:37
 * UniProt Consortium.
 * Copyright (c) 2002-2019.
 *
 */

package uk.ac.ebi.uniprot.ds.common.dao;

import java.util.List;

public interface DrugDAOCustom {
    List<Object[]> getDrugsByDiseaseId(String diseaseId);
}
