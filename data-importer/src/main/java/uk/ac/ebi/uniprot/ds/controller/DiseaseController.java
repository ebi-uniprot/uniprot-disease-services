/*
 * Created by sahmad on 04/02/19 09:29
 * UniProt Consortium.
 * Copyright (c) 2002-2019.
 *
 */

package uk.ac.ebi.uniprot.ds.controller;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.ac.ebi.uniprot.ds.controller.dto.DiseaseDTO;
import uk.ac.ebi.uniprot.ds.model.Disease;
import uk.ac.ebi.uniprot.ds.service.DiseaseService;

import javax.websocket.server.PathParam;
import java.util.Optional;

@RestController
@RequestMapping("/v1/ds")
public class DiseaseController {

    @Autowired
    private DiseaseService diseaseService;

    @Autowired
    private ModelMapper modelMapper;

    @GetMapping("/disease/{diseaseId}")
    public DiseaseDTO getDisease(@PathParam("diseaseId") String diseaseId){
        Optional<Disease> optDisease = this.diseaseService.findByDiseaseId(diseaseId);
        Disease disease = optDisease.get();
        DiseaseDTO diseaseDTO = convertToDTO(disease);
        return diseaseDTO;
    }

    private DiseaseDTO convertToDTO(Disease disease) {
        DiseaseDTO diseaseDTO = modelMapper.map(disease, DiseaseDTO.class);
        return diseaseDTO;
    }
}
