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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.Size;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import uk.ac.ebi.uniprot.ds.common.model.Drug;
import uk.ac.ebi.uniprot.ds.common.model.Protein;
import uk.ac.ebi.uniprot.ds.rest.dto.DrugDTO;
import uk.ac.ebi.uniprot.ds.rest.dto.ProteinDTO;
import uk.ac.ebi.uniprot.ds.rest.dto.ProteinDiseasesDTO;
import uk.ac.ebi.uniprot.ds.rest.filter.RequestCorrelation;
import uk.ac.ebi.uniprot.ds.rest.response.MultipleEntityResponse;
import uk.ac.ebi.uniprot.ds.rest.response.SingleEntityResponse;
import uk.ac.ebi.uniprot.ds.rest.service.DrugService;
import uk.ac.ebi.uniprot.ds.rest.service.ProteinService;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping
@Validated
@Tag(name = "Protein", description = "Protein related operations")
public class ProteinController {

    private final ProteinService proteinService;
    private final DrugService drugService;

    private final ModelMapper modelMapper;

    public ProteinController(ProteinService proteinService, DrugService drugService, ModelMapper modelMapper) {
        this.proteinService = proteinService;
        this.drugService = drugService;
        this.modelMapper = modelMapper;
    }

    @Operation(
            summary = "Get the protein by protein accessions",
            responses = {
                    @ApiResponse(
                            content = {
                                    @Content(
                                            mediaType = APPLICATION_JSON_VALUE,
                                            schema = @Schema(implementation = ProteinDTO.class))
                            })
            })
    @GetMapping(value = {"/proteins/{accession}"}, produces = MediaType.APPLICATION_JSON_VALUE)
    public SingleEntityResponse<ProteinDTO> getProtein(@PathVariable("accession") String accession){
        String requestId = RequestCorrelation.getCorrelationId();
        Optional<Protein> optProtein = this.proteinService.getProteinByAccession(accession);
        Protein protein = optProtein.orElse(new Protein());
        ProteinDTO proteinDTO = convertToDTO(protein);
        return new SingleEntityResponse<>(requestId, false, null, proteinDTO) ;
    }

    @Operation(hidden = true)
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

    @Operation(
            summary = "Get proteins by a disease id.",
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
    @GetMapping(value = {"/disease/{diseaseId}/proteins"}, produces = MediaType.APPLICATION_JSON_VALUE, name = "Get proteins by the diseaseId")
    public MultipleEntityResponse<ProteinDTO> getProteinsByDiseaseId(@PathVariable("diseaseId") String diseaseId){
        String requestId = RequestCorrelation.getCorrelationId();
        List<Protein> proteins = this.proteinService.getProteinsByDiseaseId(diseaseId);
        List<ProteinDTO> dtoList = toProteinDTOList(proteins);
        return new MultipleEntityResponse<>(requestId, dtoList) ;
    }

    @Operation(
            summary = "Get drugs by a protein accession.",
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
    @GetMapping(value={"/protein/{accession}/drugs"}, name = "Get the drugs for a given protein accession")
    public MultipleEntityResponse<DrugDTO> getDrugsByProteinAccession(@PathVariable(name = "accession") String accession) {
        String requestId = RequestCorrelation.getCorrelationId();

        List<Drug> drugs = this.drugService.getDrugsByAccession(accession);
        List<DrugDTO> dtoList = toDrugDTOList(drugs);

        return new MultipleEntityResponse<>(requestId, dtoList);
    }

    @Operation(hidden = true)
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
