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
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.ac.ebi.uniprot.ds.common.model.Variant;
import uk.ac.ebi.uniprot.ds.rest.dto.VariantDTO;
import uk.ac.ebi.uniprot.ds.rest.filter.RequestCorrelation;
import uk.ac.ebi.uniprot.ds.rest.response.MultipleEntityResponse;
import uk.ac.ebi.uniprot.ds.rest.service.VariantService;

import java.util.List;

@RestController
@RequestMapping("/v1/ds")
@Validated
@Slf4j
public class VariantController {

    @Autowired
    private VariantService variantService;

    @Autowired
    private ModelMapper modelMapper;

    @GetMapping(value = {"/protein/{accession}/variants"}, name = "Get the variants for the given accession",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public MultipleEntityResponse<VariantDTO> getVariants(@PathVariable("accession") String accession){

        String requestId = RequestCorrelation.getCorrelationId();

        List<Variant> variants = this.variantService.getVariantsByAccession(accession);

        List<VariantDTO> dtos = toVariantDTOList(variants);

        MultipleEntityResponse<VariantDTO> resp = new MultipleEntityResponse<>(requestId, dtos);

        return resp;
    }

    @GetMapping(value = {"/disease/{diseaseId}/variants"}, name = "Get the variants for the given diseaseId",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public MultipleEntityResponse<VariantDTO> getVariantsByDiseaseId(@PathVariable("diseaseId") String diseaseId){

        String requestId = RequestCorrelation.getCorrelationId();

        List<Variant> variants = this.variantService.getVariantsByDiseaseId(diseaseId);

        List<VariantDTO> dtos = toVariantDTOList(variants);

        MultipleEntityResponse<VariantDTO> resp = new MultipleEntityResponse<>(requestId, dtos);

        return resp;
    }

    private List<VariantDTO> toVariantDTOList(List<Variant> from){
        return this.modelMapper.map(from, new TypeToken<List<VariantDTO>>(){}.getType());
    }
}
