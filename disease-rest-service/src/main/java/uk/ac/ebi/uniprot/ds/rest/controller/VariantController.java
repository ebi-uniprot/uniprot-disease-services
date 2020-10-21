/*
 * Created by sahmad on 07/02/19 12:18
 * UniProt Consortium.
 * Copyright (c) 2002-2019.
 *
 */

package uk.ac.ebi.uniprot.ds.rest.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
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

import springfox.documentation.annotations.ApiIgnore;
import uk.ac.ebi.uniprot.ds.common.model.Variant;
import uk.ac.ebi.uniprot.ds.rest.dto.DiseaseDTO;
import uk.ac.ebi.uniprot.ds.rest.dto.VariantDTO;
import uk.ac.ebi.uniprot.ds.rest.filter.RequestCorrelation;
import uk.ac.ebi.uniprot.ds.rest.response.MultipleEntityResponse;
import uk.ac.ebi.uniprot.ds.rest.service.VariantService;

import java.util.List;
@Api
@RestController
@RequestMapping("/v1/ds")
@Validated
@Slf4j
public class VariantController {

    private final VariantService variantService;

    private final ModelMapper modelMapper;

    public VariantController(VariantService variantService, ModelMapper modelMapper) {
        this.variantService = variantService;
        this.modelMapper = modelMapper;
    }

    @ApiResponse(code = 200, message = "The variants retrieved", response = VariantDTO.class, responseContainer = "List")
    @ApiOperation(tags = {"proteins"}, value = "Get the variants for the given protein accession.")
    @GetMapping(value = {"/protein/{accession}/variants"}, name = "Get the variants for the given protein accession",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public MultipleEntityResponse<VariantDTO> getVariants(@ApiParam(value = "Protein accession", required = true)
                                                              @PathVariable("accession") String accession){

        String requestId = RequestCorrelation.getCorrelationId();

        List<Variant> variants = this.variantService.getVariantsByAccession(accession);

        List<VariantDTO> dtos = toVariantDTOList(variants);

        MultipleEntityResponse<VariantDTO> resp = new MultipleEntityResponse<>(requestId, dtos);

        return resp;
    }

    @ApiResponse(code = 200, message = "The variants retrieved", response = VariantDTO.class, responseContainer = "List")
    @ApiOperation(tags = {"diseases"}, value = "Get the variants for the given disease name.")
    @GetMapping(value = {"/disease/{diseaseId}/variants"}, name = "Get the variants for the given diseaseId",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public MultipleEntityResponse<VariantDTO> getVariantsByDiseaseId(@ApiParam(value = "Disease name", required = true)
                                                                         @PathVariable("diseaseId") String diseaseId){

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
