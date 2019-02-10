/*
 * Created by sahmad on 07/02/19 12:18
 * UniProt Consortium.
 * Copyright (c) 2002-2019.
 *
 */

package uk.ac.ebi.uniprot.ds.rest.controller;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.ac.ebi.uniprot.ds.common.model.Disease;
import uk.ac.ebi.uniprot.ds.rest.dto.DiseaseDTO;
import uk.ac.ebi.uniprot.ds.rest.filter.RequestCorrelation;
import uk.ac.ebi.uniprot.ds.rest.response.SingleEntityResponse;
import uk.ac.ebi.uniprot.ds.rest.service.DiseaseService;

import java.util.Optional;

@RestController
@RequestMapping("/v1/ds")
public class DiseaseController {

    @Autowired
    private DiseaseService diseaseService;

    @Autowired
    private ModelMapper modelMapper;

    @GetMapping(value = {"/diseases/{diseaseId}"})
    public SingleEntityResponse<DiseaseDTO> getDisease(@PathVariable("diseaseId") String diseaseId){
        String requestId = RequestCorrelation.getCorrelationId();
        Optional<Disease> optDisease = this.diseaseService.findByDiseaseId(diseaseId);
        Disease disease = optDisease.orElse(new Disease());
        DiseaseDTO diseaseDTO = convertToDTO(disease);
        return new SingleEntityResponse<>(requestId, false, null, diseaseDTO) ;
    }

    private DiseaseDTO convertToDTO(Disease disease) {
        DiseaseDTO diseaseDTO = modelMapper.map(disease, DiseaseDTO.class);
        return diseaseDTO;
    }
}
