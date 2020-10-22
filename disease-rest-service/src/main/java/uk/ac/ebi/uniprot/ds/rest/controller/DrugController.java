package uk.ac.ebi.uniprot.ds.rest.controller;

import org.modelmapper.ModelMapper;
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
import uk.ac.ebi.uniprot.ds.common.model.Disease;
import uk.ac.ebi.uniprot.ds.common.model.Protein;
import uk.ac.ebi.uniprot.ds.rest.dto.DiseaseDTO;
import uk.ac.ebi.uniprot.ds.rest.dto.ProteinDTO;
import uk.ac.ebi.uniprot.ds.rest.filter.RequestCorrelation;
import uk.ac.ebi.uniprot.ds.rest.response.MultipleEntityResponse;
import uk.ac.ebi.uniprot.ds.rest.service.DiseaseService;
import uk.ac.ebi.uniprot.ds.rest.service.ProteinService;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * @author sahmad
 * Controller for drug related endpoints
 */
@RestController
@RequestMapping
@Validated
@Tag(name = "Drug", description = "Drug related operations")
public class DrugController {
    private final ProteinService proteinService;

    private final DiseaseService diseaseService;

    private final ModelMapper modelMapper;

    public DrugController(ProteinService proteinService, DiseaseService diseaseService, ModelMapper modelMapper) {
        this.proteinService = proteinService;
        this.diseaseService = diseaseService;
        this.modelMapper = modelMapper;
    }

    @Operation(
            summary = "Get proteins by a drug name.",
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
                                                                    ProteinDTO.class)))
                            })
            })
    @GetMapping(value={"/drug/{drugName}/proteins"}, name = "Get the proteins for a given drug name")
    public MultipleEntityResponse<ProteinDTO> getProteinsByDrug(@PathVariable String drugName){
        String requestId = RequestCorrelation.getCorrelationId();
        List<Protein> proteins = this.proteinService.getProteinsByDrugName(drugName);
        List<ProteinDTO> dtoList = ProteinDTO.toProteinDTOList(proteins, this.modelMapper);
        return new MultipleEntityResponse<>(requestId, dtoList);
    }

    @Operation(
            summary = "Get diseases by a drug name.",
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
    @GetMapping(value={"/drug/{drugName}/diseases"}, name = "Get the diseases for a given drug name")
    public MultipleEntityResponse<DiseaseDTO> getDiseasesByDrug(@PathVariable String drugName){
        String requestId = RequestCorrelation.getCorrelationId();
        List<Disease> diseases = this.diseaseService.getDiseasesByDrugName(drugName);
        List<DiseaseDTO> diseaseDTOList = DiseaseDTO.toDiseaseDTOList(diseases, this.modelMapper);

        return new MultipleEntityResponse<>(requestId, diseaseDTOList);
    }
}
