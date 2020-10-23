package uk.ac.ebi.uniprot.ds.rest.controller;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
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
import uk.ac.ebi.uniprot.ds.common.model.SiteMapping;
import uk.ac.ebi.uniprot.ds.rest.dto.UniProtSiteMapDTO;
import uk.ac.ebi.uniprot.ds.rest.filter.RequestCorrelation;
import uk.ac.ebi.uniprot.ds.rest.response.MultipleEntityResponse;
import uk.ac.ebi.uniprot.ds.rest.service.UniProtSiteMapService;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * @author sahmad
 * @created 07/09/2020
 */
@RestController
@RequestMapping
@Validated
@Tag(name = "Ortholog-Mapping", description = "Ortholog mapping related operations")
public class UniProtSiteMapController {
    private final UniProtSiteMapService siteMapService;
    private final ModelMapper modelMapper;

    public UniProtSiteMapController(UniProtSiteMapService siteMapService, ModelMapper modelMapper) {
        this.siteMapService = siteMapService;
        this.modelMapper = modelMapper;
    }

    @Operation(
            summary = "Get ortholog mappings by a protein accession.",
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
                                                                    UniProtSiteMapDTO.class)))
                            })
            })
    @GetMapping(value = {"/ortholog-mappings/{accession}"}, name = "Get the UniProt site mappings for a given accession")
    public MultipleEntityResponse<UniProtSiteMapDTO> getSiteMappings(@PathVariable(name = "accession") String accession) {
        String requestId = RequestCorrelation.getCorrelationId();
        List<SiteMapping> siteMaps = this.siteMapService.getSiteMappings(accession);
        List<UniProtSiteMapDTO> dtos = toUniProtSiteMapDTOs(siteMaps);
        MultipleEntityResponse<UniProtSiteMapDTO> resp = new MultipleEntityResponse<>(requestId, dtos);
        return resp;
    }

    private List<UniProtSiteMapDTO> toUniProtSiteMapDTOs(List<SiteMapping> from) {
        return this.modelMapper.map(from, new TypeToken<List<UniProtSiteMapDTO>>() {
        }.getType());
    }
}
