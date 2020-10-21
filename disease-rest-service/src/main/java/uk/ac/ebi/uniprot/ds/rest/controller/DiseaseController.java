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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import uk.ac.ebi.uniprot.ds.common.model.Disease;
import uk.ac.ebi.uniprot.ds.common.model.Drug;
import uk.ac.ebi.uniprot.ds.rest.dto.DiseaseDTO;
import uk.ac.ebi.uniprot.ds.rest.dto.DrugDTO;
import uk.ac.ebi.uniprot.ds.rest.filter.RequestCorrelation;
import uk.ac.ebi.uniprot.ds.rest.response.MultipleEntityResponse;
import uk.ac.ebi.uniprot.ds.rest.response.SingleEntityResponse;
import uk.ac.ebi.uniprot.ds.rest.service.DiseaseService;
import uk.ac.ebi.uniprot.ds.rest.service.DrugService;

import java.util.List;
import java.util.Optional;

@Api(tags = {"diseases"})
@RestController
@RequestMapping("/v1/ds")
@Validated
public class DiseaseController {

    private final DiseaseService diseaseService;
    private final DrugService drugService;

    private final ModelMapper modelMapper;

    public DiseaseController(DiseaseService diseaseService, DrugService drugService, ModelMapper modelMapper) {
        this.diseaseService = diseaseService;
        this.drugService = drugService;
        this.modelMapper = modelMapper;
    }

    @ApiResponse(code = 200, message = "The disease is retrieved.", response = DiseaseDTO.class)
    @ApiOperation(value = "Get disease by disease name.")
    @GetMapping(value = {"/diseases/{diseaseId}"}, name = "Get disease by diseaseId",
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public SingleEntityResponse<DiseaseDTO> getDisease(@ApiParam(value = "The disease name", required = true)
                                                           @PathVariable("diseaseId") String diseaseId){
        String requestId = RequestCorrelation.getCorrelationId();
        Optional<Disease> optDisease = this.diseaseService.findByDiseaseId(diseaseId);
        Disease disease = optDisease.orElse(new Disease());
        DiseaseDTO diseaseDTO = convertToDTO(disease);
        return new SingleEntityResponse<>(requestId, false, null, diseaseDTO) ;
    }

    @ApiOperation(value = "", hidden = true)
    @GetMapping(value = {"/diseases/search/{keyword}"}, name = "Fetches a list of diseases which have the given keyword in name")
    public MultipleEntityResponse<DiseaseDTO> searchDiseases(
             @PathVariable("keyword") String keyword,

             @Range(min = 0, message = "The offset cannot be negative.")
             @RequestParam(value = "offset", required = false, defaultValue = "0")
             Integer offset,

             @Range(min = 1, max = 200, message = "The size must be between 1 and 200 both inclusive.")
             @RequestParam(value = "size", required = false, defaultValue = "200")
             Integer size){

        String requestId = RequestCorrelation.getCorrelationId();

        List<Disease> diseases = this.diseaseService.searchDiseases(keyword, offset, size);
        List<DiseaseDTO> diseaseDTOList = toDiseaseDTOList(diseases);

        return new MultipleEntityResponse<>(requestId, diseaseDTOList, offset, size);

    }

    @ApiResponse(code = 200, message = "The diseases retrieved", response = DiseaseDTO.class, responseContainer = "List")
    @ApiOperation(value = "Get the diseases for a given protein accession.")
    @GetMapping(value={"/protein/{accession}/diseases"}, name = "Get the diseases for a given protein accession",
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public MultipleEntityResponse<DiseaseDTO> getProteinDiseases(
            @ApiParam(value = "The accession of a protein", required = true)
            @PathVariable(name = "accession")
                    String accession) {
        String requestId = RequestCorrelation.getCorrelationId();

        List<Disease> diseases = this.diseaseService.getDiseasesByProteinAccession(accession);
        List<DiseaseDTO> diseaseDTOList = toDiseaseDTOList(diseases);

        return new MultipleEntityResponse<>(requestId, diseaseDTOList, null, null);
    }

    @ApiResponse(code = 200, message = "The drugs retrieved", response = DrugDTO.class, responseContainer = "List")
    @ApiOperation(value = "Get the drugs for a given disease name.")
    @GetMapping(value={"/disease/{diseaseId}/drugs"}, name = "Get the drugs for a given diseaseId",
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public MultipleEntityResponse<DrugDTO> getDrugsByDiseaseId(
            @ApiParam(value = "The name of a disease", required = true)
            @PathVariable(name = "diseaseId") String diseaseId) {
        String requestId = RequestCorrelation.getCorrelationId();
        List<DrugDTO>  dtoList = this.drugService.getDrugDTOsByDiseaseId(diseaseId);
        return new MultipleEntityResponse<>(requestId, dtoList);
    }

    private DiseaseDTO convertToDTO(Disease disease) {
        DiseaseDTO diseaseDTO = modelMapper.map(disease, DiseaseDTO.class);
        return diseaseDTO;
    }

    private List<DiseaseDTO> toDiseaseDTOList(List<Disease> from){
        return this.modelMapper.map(from, new TypeToken<List<DiseaseDTO>>(){}.getType());
    }

    private List<DrugDTO> toDrugDTOList(List<Drug> from){
        return this.modelMapper.map(from, new TypeToken<List<DrugDTO>>(){}.getType());
    }
}
