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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.ac.ebi.uniprot.ds.common.model.Interaction;
import uk.ac.ebi.uniprot.ds.common.model.Protein;
import uk.ac.ebi.uniprot.ds.common.model.ProteinCrossRef;
import uk.ac.ebi.uniprot.ds.rest.dto.*;
import uk.ac.ebi.uniprot.ds.rest.filter.RequestCorrelation;
import uk.ac.ebi.uniprot.ds.rest.response.MultipleEntityResponse;
import uk.ac.ebi.uniprot.ds.rest.response.SingleEntityResponse;
import uk.ac.ebi.uniprot.ds.rest.service.ProteinService;

import javax.validation.constraints.Size;
import java.util.List;
import java.util.Optional;
@RestController
@RequestMapping("/v1/ds")
@Validated
public class ProteinController {

    @Autowired
    private ProteinService proteinService;

    @Autowired
    private ModelMapper modelMapper;

    @GetMapping(value = {"/proteins/{accession}"}, produces = MediaType.APPLICATION_JSON_VALUE)
    public SingleEntityResponse<ProteinDTO> getProtein(@PathVariable("accession") String accession){
        String requestId = RequestCorrelation.getCorrelationId();
        Optional<Protein> optProtein = this.proteinService.getProteinByAccession(accession);
        Protein protein = optProtein.orElse(new Protein());
        ProteinDTO proteinDTO = convertToDTO(protein);
        return new SingleEntityResponse<>(requestId, false, null, proteinDTO) ;
    }

    @GetMapping(value={"/proteins/{accessions}/xrefs"}, name = "Get the cross refs for the given list of accession")
    public MultipleEntityResponse<ProteinWithCrossRefsDTO> getProteinsXRefs(
            @Size(min = 1, max = 200, message = "The total count of accessions passed must be between 1 and 200 both inclusive.")
            @PathVariable(name = "accessions")
                    List<String> accessions)
    {
        String requestId = RequestCorrelation.getCorrelationId();
        List<Protein> proteins = this.proteinService.getAllProteinsByAccessions(accessions);
        List<ProteinWithCrossRefsDTO> dtos = toProteinCrossRefsDTOList(proteins);
        MultipleEntityResponse<ProteinWithCrossRefsDTO> resp = new MultipleEntityResponse<>(requestId, dtos);
        return resp;
    }

    @GetMapping(value={"/protein/{accession}/xrefs"}, name = "Get the protein cross refs for a given accession")
    public MultipleEntityResponse<ProteinCrossRefDTO> getProteinXRefs(@PathVariable(name = "accession") String accession) {
        String requestId = RequestCorrelation.getCorrelationId();
        List<ProteinCrossRef> xrefs = this.proteinService.getProteinCrossRefsByAccession(accession);
        List<ProteinCrossRefDTO> dtoList = toProteinCrossRefDTOList(xrefs);
        return new MultipleEntityResponse<>(requestId, dtoList);
    }

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

    @GetMapping(value={"/protein/{accession}/interactions"}, name = "Get the protein interactions for a given accession")
    public MultipleEntityResponse<InteractionDTO> getProteinInteractions(@PathVariable(name = "accession") String accession) {
        String requestId = RequestCorrelation.getCorrelationId();
        List<Interaction> interactions = this.proteinService.getProteinInteractions(accession);
        List<InteractionDTO> dtoList = toInteractionDTOList(interactions);
        return new MultipleEntityResponse<>(requestId, dtoList);
    }

    @GetMapping(value = {"/disease/{diseaseId}/proteins"}, produces = MediaType.APPLICATION_JSON_VALUE)
    public MultipleEntityResponse<ProteinDTO> getProteinsByDiseaseId(@PathVariable("diseaseId") String diseaseId){
        String requestId = RequestCorrelation.getCorrelationId();
        List<Protein> proteins = this.proteinService.getProteinsByDiseaseId(diseaseId);
        List<ProteinDTO> dtoList = toProteinDTOList(proteins);
        return new MultipleEntityResponse<>(requestId, dtoList) ;
    }

    private ProteinDTO convertToDTO(Protein protein) {
        ProteinDTO proteinDTO = modelMapper.map(protein, ProteinDTO.class);
        return proteinDTO;
    }

    private List<ProteinWithCrossRefsDTO> toProteinCrossRefsDTOList(List<Protein> from){
        return this.modelMapper.map(from, new TypeToken<List<ProteinWithCrossRefsDTO>>(){}.getType());
    }

    private List<ProteinDiseasesDTO> toProteinDiseasesDTOList(List<Protein> from){
        return this.modelMapper.map(from, new TypeToken<List<ProteinDiseasesDTO>>(){}.getType());
    }

    private List<ProteinCrossRefDTO> toProteinCrossRefDTOList(List<ProteinCrossRef> xrefs){
        List<ProteinCrossRefDTO> dtoList = modelMapper.map(xrefs,
                new TypeToken<List<ProteinCrossRefDTO>>(){}.getType());
        return dtoList;
    }

    private List<InteractionDTO> toInteractionDTOList(List<Interaction> intrxn){
        List<InteractionDTO> dtoList = modelMapper.map(intrxn,
                new TypeToken<List<InteractionDTO>>(){}.getType());
        return dtoList;
    }

    private List<ProteinDTO> toProteinDTOList(List<Protein> proteins){
        List<ProteinDTO> dtoList = modelMapper.map(proteins,
                new TypeToken<List<ProteinDTO>>(){}.getType());
        return dtoList;
    }
}
