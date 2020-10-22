/*
 * Created by sahmad on 07/02/19 12:18
 * UniProt Consortium.
 * Copyright (c) 2002-2019.
 *
 */

package uk.ac.ebi.uniprot.ds.rest.controller;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import uk.ac.ebi.uniprot.ds.common.model.Variant;
import uk.ac.ebi.uniprot.ds.rest.dto.VariantDTO;
import uk.ac.ebi.uniprot.ds.rest.filter.RequestCorrelation;
import uk.ac.ebi.uniprot.ds.rest.response.MultipleEntityResponse;
import uk.ac.ebi.uniprot.ds.rest.service.VariantService;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping
@Validated
@Slf4j
@Tag(name = "Variant", description = "Variant related operations")
public class VariantController {

    private final VariantService variantService;

    private final ModelMapper modelMapper;

    public VariantController(VariantService variantService, ModelMapper modelMapper) {
        this.variantService = variantService;
        this.modelMapper = modelMapper;
    }

    @Operation(
            summary = "Get variants by a protein accession.",
            responses = {
                    @ApiResponse(
                            content = {
                                    @Content(
                                            mediaType = APPLICATION_JSON_VALUE,
                                            array =
                                            @ArraySchema(
                                                    schema =
                                                    @Schema(
                                                            implementation =
                                                                    VariantDTO.class)))
                            })
            })
    @GetMapping(value = {"/protein/{accession}/variants"}, name = "Get the variants for the given protein accession",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public MultipleEntityResponse<VariantDTO> getVariants(@PathVariable("accession") String accession){

        String requestId = RequestCorrelation.getCorrelationId();

        List<Variant> variants = this.variantService.getVariantsByAccession(accession);

        List<VariantDTO> dtos = toVariantDTOList(variants);

        MultipleEntityResponse<VariantDTO> resp = new MultipleEntityResponse<>(requestId, dtos);

        return resp;
    }

    @Operation(
            summary = "Get variants by a disease id.",
            responses = {
                    @ApiResponse(
                            content = {
                                    @Content(
                                            mediaType = APPLICATION_JSON_VALUE,
                                            array =
                                            @ArraySchema(
                                                    schema =
                                                    @Schema(
                                                            implementation =
                                                                    VariantDTO.class)))
                            })
            })
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
