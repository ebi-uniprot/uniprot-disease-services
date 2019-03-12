/*
 * Created by sahmad on 07/02/19 12:18
 * UniProt Consortium.
 * Copyright (c) 2002-2019.
 *
 */

package uk.ac.ebi.uniprot.ds.rest.controller;

import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.ac.ebi.uniprot.ds.common.model.Interaction;
import uk.ac.ebi.uniprot.ds.rest.dto.InteractionDTO;
import uk.ac.ebi.uniprot.ds.rest.filter.RequestCorrelation;
import uk.ac.ebi.uniprot.ds.rest.response.MultipleEntityResponse;
import uk.ac.ebi.uniprot.ds.rest.service.ProteinService;

import java.util.List;

@RestController
@RequestMapping("/v1/ds")
@Validated
@Slf4j
public class ProteinInteractionController {

    @Autowired
    private ProteinService proteinService;

    @Autowired
    private ModelMapper modelMapper;

    @GetMapping(value={"/protein/{accession}/interactions"}, name = "Get the protein interactions for a given accession")
    public MultipleEntityResponse<InteractionDTO> getProteinInteractions(@PathVariable(name = "accession") String accession) {
        String requestId = RequestCorrelation.getCorrelationId();
        List<Interaction> interactions = this.proteinService.getProteinInteractions(accession);
        List<InteractionDTO> dtoList = toInteractionDTOList(interactions);
        return new MultipleEntityResponse<>(requestId, dtoList);
    }

    private List<InteractionDTO> toInteractionDTOList(List<Interaction> intrxn){
        List<InteractionDTO> dtoList = modelMapper.map(intrxn,
                new TypeToken<List<InteractionDTO>>(){}.getType());
        return dtoList;
    }
}
