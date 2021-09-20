/*
 * Created by sahmad on 07/02/19 12:18
 * UniProt Consortium.
 * Copyright (c) 2002-2019.
 *
 */

package uk.ac.ebi.uniprot.ds.rest.controller;

import org.hibernate.validator.constraints.Range;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

import javax.validation.constraints.Pattern;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import uk.ac.ebi.uniprot.ds.common.model.Disease;
import uk.ac.ebi.uniprot.ds.rest.dto.DiseaseDTO;
import uk.ac.ebi.uniprot.ds.rest.dto.DrugDTO;
import uk.ac.ebi.uniprot.ds.rest.filter.RequestCorrelation;
import uk.ac.ebi.uniprot.ds.rest.response.MultipleEntityResponse;
import uk.ac.ebi.uniprot.ds.rest.response.SingleEntityResponse;
import uk.ac.ebi.uniprot.ds.rest.service.DiseaseService;
import uk.ac.ebi.uniprot.ds.rest.service.DrugService;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping
@Validated
@Tag(name = "Disease", description = "Disease related operations")
public class DiseaseController {
    public static final String ACCESSION_REGEX = "DI-[A-Z]?(\\d+)";
    private final DiseaseService diseaseService;
    private final DrugService drugService;

    private final ModelMapper modelMapper;

    public DiseaseController(DiseaseService diseaseService, DrugService drugService, ModelMapper modelMapper) {
        this.diseaseService = diseaseService;
        this.drugService = drugService;
        this.modelMapper = modelMapper;
    }

    @Operation(
            summary = "Get the disease by disease id",
            responses = {
                    @ApiResponse(
                            content = {
                                    @Content(
                                            mediaType = APPLICATION_JSON_VALUE,
                                            schema = @Schema(implementation = DiseaseDTO.class))
                            })
            })
    @GetMapping(value = {"/diseases/{diseaseId}"}, name = "Get disease by diseaseId")
    public SingleEntityResponse<DiseaseDTO> getDisease(@PathVariable("diseaseId")
                                                       @Pattern(
                                                               regexp = ACCESSION_REGEX,
                                                               message = "Invalid diseaseId format. Valid format 'DI-[A-Z]?(\\d+)'")
                                                               String diseaseId) {
        String requestId = RequestCorrelation.getCorrelationId();
        Optional<Disease> optDisease = this.diseaseService.findByDiseaseId(diseaseId);
        Disease disease = optDisease.orElse(new Disease());
        DiseaseDTO diseaseDTO = convertToDTO(disease);
        return new SingleEntityResponse<>(requestId, false, null, diseaseDTO);
    }

    @Operation(hidden = true)
    @GetMapping(value = {"/diseases/search/{keyword}"}, name = "Fetches a list of diseases which have the given keyword in name")
    public MultipleEntityResponse<DiseaseDTO> searchDiseases(
            @PathVariable("keyword") String keyword,

            @Range(min = 0, message = "The offset cannot be negative.")
            @RequestParam(value = "offset", required = false, defaultValue = "0")
                    Integer offset,

            @Range(min = 1, max = 200, message = "The size must be between 1 and 200 both inclusive.")
            @RequestParam(value = "size", required = false, defaultValue = "200")
                    Integer size) {

        String requestId = RequestCorrelation.getCorrelationId();

        List<Disease> diseases = this.diseaseService.searchDiseases(keyword, offset, size);
        List<DiseaseDTO> diseaseDTOList = toDiseaseDTOList(diseases);

        return new MultipleEntityResponse<>(requestId, diseaseDTOList, offset, size);

    }

    @Operation(
            summary = "Get diseases by a protein accession.",
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
                                                                    DiseaseDTO.class)))
                            })
            })
    @GetMapping(value = {"/protein/{accession}/diseases"}, name = "Get the diseases for a given protein accession")
    public MultipleEntityResponse<DiseaseDTO> getProteinDiseases(@PathVariable(name = "accession") String accession) {
        String requestId = RequestCorrelation.getCorrelationId();

        List<Disease> diseases = this.diseaseService.getDiseasesByProteinAccession(accession);
        List<DiseaseDTO> diseaseDTOList = toDiseaseDTOList(diseases);

        return new MultipleEntityResponse<>(requestId, diseaseDTOList, null, null);
    }

    @Operation(
            summary = "Get drugs by a disease id.",
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
                                                                    DrugDTO.class)))
                            })
            })
    @GetMapping(value = {"/disease/{diseaseId}/drugs"}, name = "Get the drugs for a given diseaseId")
    public MultipleEntityResponse<DrugDTO> getDrugsByDiseaseId(@PathVariable(name = "diseaseId") @Pattern(
            regexp = ACCESSION_REGEX,
            message = "Invalid diseaseId format. Valid format 'DI-[A-Z]?(\\d+)'") String diseaseId) {
        String requestId = RequestCorrelation.getCorrelationId();
        List<DrugDTO> dtoList = this.drugService.getDrugDTOsByDiseaseId(diseaseId);
        return new MultipleEntityResponse<>(requestId, dtoList);
    }

    private DiseaseDTO convertToDTO(Disease disease) {
        DiseaseDTO diseaseDTO = modelMapper.map(disease, DiseaseDTO.class);
        return diseaseDTO;
    }

    private List<DiseaseDTO> toDiseaseDTOList(List<Disease> from) {
        return this.modelMapper.map(from, new TypeToken<List<DiseaseDTO>>() {
        }.getType());
    }
}
