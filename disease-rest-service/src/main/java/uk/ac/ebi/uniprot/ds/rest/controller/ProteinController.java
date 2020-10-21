/*
 * Created by sahmad on 07/02/19 12:18
 * UniProt Consortium.
 * Copyright (c) 2002-2019.
 *
 */

package uk.ac.ebi.uniprot.ds.rest.controller;

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
import uk.ac.ebi.uniprot.ds.common.model.Drug;
import uk.ac.ebi.uniprot.ds.common.model.Protein;
import uk.ac.ebi.uniprot.ds.rest.dto.*;
import uk.ac.ebi.uniprot.ds.rest.filter.RequestCorrelation;
import uk.ac.ebi.uniprot.ds.rest.response.MultipleEntityResponse;
import uk.ac.ebi.uniprot.ds.rest.response.SingleEntityResponse;
import uk.ac.ebi.uniprot.ds.rest.service.DrugService;
import uk.ac.ebi.uniprot.ds.rest.service.ProteinService;

import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.Size;
import java.io.IOException;
import java.util.*;

@Api(tags = {"proteins"})
@RestController
@RequestMapping("/v1/ds")
@Validated
public class ProteinController {

    private final ProteinService proteinService;
    private final DrugService drugService;

    private final ModelMapper modelMapper;

    public ProteinController(ProteinService proteinService, DrugService drugService, ModelMapper modelMapper) {
        this.proteinService = proteinService;
        this.drugService = drugService;
        this.modelMapper = modelMapper;
    }

    @ApiResponse(code = 200, message = "The protein retrieved", response = ProteinDTO.class)
    @ApiOperation(value = "Get the protein by accession.")
    @GetMapping(value = {"/proteins/{accession}"}, produces = MediaType.APPLICATION_JSON_VALUE)
    public SingleEntityResponse<ProteinDTO> getProtein(@ApiParam(value = "Protein accession", required = true)
                                                           @PathVariable("accession") String accession){
        String requestId = RequestCorrelation.getCorrelationId();
        Optional<Protein> optProtein = this.proteinService.getProteinByAccession(accession);
        Protein protein = optProtein.orElse(new Protein());
        ProteinDTO proteinDTO = convertToDTO(protein);
        return new SingleEntityResponse<>(requestId, false, null, proteinDTO) ;
    }

    @ApiOperation(hidden = true, value = "")
    @GetMapping(value={"/proteins/{accessions}/diseases"}, name = "Get the diseases for the given list of accession")
    public MultipleEntityResponse<ProteinDiseasesDTO> getProteinsDiseases(
            @Size(min = 1, max = 200, message = "The total count of accessions passed must be between 1 and 200 both inclusive.")
            @PathVariable(name = "accessions")
                    List<String> accessions)
    {
        String requestId = RequestCorrelation.getCorrelationId();
        List<Protein> proteins = this.proteinService.getAllProteinsByAccessions(accessions);
        List<ProteinDiseasesDTO> dtos = toProteinDiseasesDTOList(proteins);
        MultipleEntityResponse<ProteinDiseasesDTO> resp = new MultipleEntityResponse<>(requestId, dtos);
        return resp;
    }

    @ApiResponse(code = 200, message = "The proteins retrieved", response = ProteinDTO.class, responseContainer = "List")
    @ApiOperation(value = "Get the proteins for a given disease name.")
    @GetMapping(value = {"/disease/{diseaseId}/proteins"}, produces = MediaType.APPLICATION_JSON_VALUE,
            name = "Get proteins by the diseaseId")
    public MultipleEntityResponse<ProteinDTO> getProteinsByDiseaseId(@ApiParam(value = "Disease name", required = true)
                                                                         @PathVariable("diseaseId") String diseaseId){
        String requestId = RequestCorrelation.getCorrelationId();
        List<Protein> proteins = this.proteinService.getProteinsByDiseaseId(diseaseId);
        List<ProteinDTO> dtoList = toProteinDTOList(proteins);
        return new MultipleEntityResponse<>(requestId, dtoList) ;
    }

    @ApiResponse(code = 200, message = "The proteins retrieved", response = DrugDTO.class, responseContainer = "List")
    @ApiOperation(value = "Get the drugs for a given protein accession.")
    @GetMapping(value={"/protein/{accession}/drugs"}, name = "Get the drugs for a given protein accession",
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public MultipleEntityResponse<DrugDTO> getDrugsByProteinAccession(@ApiParam(value = "Protein accession", required = true)
                                                                          @PathVariable(name = "accession") String accession) {
        String requestId = RequestCorrelation.getCorrelationId();
        List<Drug> drugs = this.drugService.getDrugsByAccession(accession);
        List<DrugDTO> dtoList = toDrugDTOList(drugs);

        return new MultipleEntityResponse<>(requestId, dtoList);
    }

    @ApiOperation(hidden = true, value = "")
    @GetMapping(value = {"/proteins/{accessions}/download"}, name = "Download proteins by given list of accessions", produces = "text/tsv")
    public void downloadProteins(
            @Size(min = 1, max = 300, message = "The total count of accessions passed must be between 1 and 300 both inclusive.")
            @PathVariable(name = "accessions") List<String> accessions,
            @RequestParam(value = "fields", required = false) String fields,
            HttpServletResponse response) throws IOException {

        List<String> headers = getHeaders(fields);

        List<Protein> proteins = this.proteinService.getAllProteinsByAccessions(accessions);
        response.setContentType("text/tsv; charset=utf-8");
        response.setHeader("Content-disposition", "attachment; filename=proteins.tsv");
        // print the header
        response.getWriter().print(ProteinDownloadHelper.getTabSeparatedStr(headers));
        // print each protein
        for (Protein protein : proteins) {
            Map<String, String> map = ProteinDownloadHelper.getProteinMap(protein);
            List<String> result = ProteinDownloadHelper.getFieldsValues(headers, map);
            response.getWriter().print(ProteinDownloadHelper.getTabSeparatedStr(result));
        }
    }

    private List<String> getHeaders(String fields) {
        if(fields == null || fields.isEmpty()){
            return Arrays.asList(ProteinDownloadHelper.DEFAULT_FIELDS.split(","));
        } else {
            return Arrays.asList(fields.split(","));
        }
    }


    private ProteinDTO convertToDTO(Protein protein) {
        ProteinDTO proteinDTO = modelMapper.map(protein, ProteinDTO.class);
        return proteinDTO;
    }

    private List<ProteinDiseasesDTO> toProteinDiseasesDTOList(List<Protein> from){
        return this.modelMapper.map(from, new TypeToken<List<ProteinDiseasesDTO>>(){}.getType());
    }

    private List<ProteinDTO> toProteinDTOList(List<Protein> proteins){
        List<ProteinDTO> dtoList = modelMapper.map(proteins,
                new TypeToken<List<ProteinDTO>>(){}.getType());
        return dtoList;
    }

    private List<DrugDTO> toDrugDTOList(List<Drug> from){
        return this.modelMapper.map(from, new TypeToken<List<DrugDTO>>(){}.getType());
    }
}
