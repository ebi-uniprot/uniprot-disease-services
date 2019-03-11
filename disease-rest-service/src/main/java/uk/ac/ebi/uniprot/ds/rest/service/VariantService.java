/*
 * Created by sahmad on 07/02/19 12:19
 * UniProt Consortium.
 * Copyright (c) 2002-2019.
 *
 */

package uk.ac.ebi.uniprot.ds.rest.service;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.ebi.uniprot.ds.common.dao.ProteinDAO;
import uk.ac.ebi.uniprot.ds.common.model.Protein;
import uk.ac.ebi.uniprot.ds.common.model.Variant;
import uk.ac.ebi.uniprot.ds.rest.exception.AssetNotFoundException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class VariantService {

    @Autowired
    private ProteinDAO proteinDAO;

    public List<Variant> getVariantsByAccession(String accession){

        Optional<Protein> protein = this.proteinDAO.findProteinByAccession(accession);
        List<Variant> variants = new ArrayList<>();

        if(!protein.isPresent()) {
            log.warn("There is no protein associated with accession {}", accession);
        } else {
            // get the variants under the protein
            variants = protein.get().getVariants();
        }

        return variants;
    }
}
