package uk.ac.ebi.uniprot.ds.rest.controller;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import uk.ac.ebi.uniprot.ds.common.model.Disease;
import uk.ac.ebi.uniprot.ds.common.model.Protein;
import uk.ac.ebi.uniprot.ds.rest.dto.DiseaseDTO;
import uk.ac.ebi.uniprot.ds.rest.dto.ProteinDTO;
import uk.ac.ebi.uniprot.ds.rest.filter.RequestCorrelation;
import uk.ac.ebi.uniprot.ds.rest.response.MultipleEntityResponse;
import uk.ac.ebi.uniprot.ds.rest.service.DiseaseService;
import uk.ac.ebi.uniprot.ds.rest.service.ProteinService;

import java.util.List;

/**
 * @author sahmad
 * Controller for drug related endpoints
 */
@Api(tags = {"drugs"})
@RestController
@RequestMapping("/v1/ds")
@Validated
public class DrugController {
    private final ProteinService proteinService;
    private final DiseaseService diseaseService;
    private final ModelMapper modelMapper;

    public DrugController(ProteinService proteinService, DiseaseService diseaseService, ModelMapper modelMapper) {
        this.proteinService = proteinService;
        this.diseaseService = diseaseService;
        this.modelMapper = modelMapper;
    }

    @ApiResponse(code = 200, message = "The proteins retrieved", response = ProteinDTO.class, responseContainer = "List")
    @ApiOperation(value = "Get the proteins for a given drug name.")
    @GetMapping(value={"/drug/{drugName}/proteins"}, name = "Get the proteins for a given drug name",
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public MultipleEntityResponse<ProteinDTO> getProteinsByDrug(
            @ApiParam(value = "The name of a drug", required = true)
            @PathVariable String drugName){
        String requestId = RequestCorrelation.getCorrelationId();
        List<Protein> proteins = this.proteinService.getProteinsByDrugName(drugName);
        List<ProteinDTO> dtoList = ProteinDTO.toProteinDTOList(proteins, this.modelMapper);
        return new MultipleEntityResponse<>(requestId, dtoList);
    }

    @ApiResponse(code = 200, message = "The proteins retrieved", response = DiseaseDTO.class, responseContainer = "List")
    @ApiOperation(value = "Get the diseases for a given drug name.")
    @GetMapping(value={"/drug/{drugName}/diseases"}, name = "Get the diseases for a given drug name",
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public MultipleEntityResponse<DiseaseDTO> getDiseasesByDrug(
            @ApiParam(value = "The name of a drug", required = true)
            @PathVariable String drugName){
            String requestId = RequestCorrelation.getCorrelationId();
            List<Disease> diseases = this.diseaseService.getDiseasesByDrugName(drugName);
            List<DiseaseDTO> diseaseDTOList = DiseaseDTO.toDiseaseDTOList(diseases, this.modelMapper);

            return new MultipleEntityResponse<>(requestId, diseaseDTOList);
    }
}
