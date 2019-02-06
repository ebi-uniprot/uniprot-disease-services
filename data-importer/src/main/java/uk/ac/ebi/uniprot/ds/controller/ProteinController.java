/*
 * Created by sahmad on 05/02/19 11:59
 * UniProt Consortium.
 * Copyright (c) 2002-2019.
 *
 */

package uk.ac.ebi.uniprot.ds.controller;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.ac.ebi.uniprot.ds.controller.dto.ProteinDTO;
import uk.ac.ebi.uniprot.ds.controller.dto.ProteinPathwaysDTO;
import uk.ac.ebi.uniprot.ds.controller.filter.RequestCorrelation;
import uk.ac.ebi.uniprot.ds.controller.response.MultipleEntityResponse;
import uk.ac.ebi.uniprot.ds.controller.response.SingleEntityResponse;
import uk.ac.ebi.uniprot.ds.model.Protein;
import uk.ac.ebi.uniprot.ds.service.ProteinService;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/v1/ds")
public class ProteinController {

    @Autowired
    private ProteinService proteinService;

    @Autowired
    private ModelMapper modelMapper;

    @GetMapping(value = {"/proteins/{accession}"})
    public SingleEntityResponse<ProteinDTO> getDisease(@PathVariable("accession") String accession){
        String requestId = RequestCorrelation.getCorrelationId();
        Optional<Protein> optProtein = this.proteinService.getProteinByAccession(accession);
        Protein protein = optProtein.get();
        ProteinDTO proteinDTO = convertToDTO(protein);
        return new SingleEntityResponse<>(requestId, false, null, proteinDTO) ;
    }

    @GetMapping(value={"/{accessions}/pathways"}, name = "Get the pathways for the given list of accession")
    public MultipleEntityResponse<ProteinPathwaysDTO> getProteinsPathway(@PathVariable(name = "accessions") List<String> accessions){
        String requestId = RequestCorrelation.getCorrelationId();
        List<Protein> proteins = this.proteinService.getAllProteinsByAccessions(accessions);
        List<ProteinPathwaysDTO> dtos = toProteinPathwaysDTOList(proteins);
        MultipleEntityResponse<ProteinPathwaysDTO> resp = new MultipleEntityResponse<>(requestId, dtos);
        return resp;
    }

    private ProteinDTO convertToDTO(Protein protein) {
        ProteinDTO proteinDTO = modelMapper.map(protein, ProteinDTO.class);
        return proteinDTO;
    }

    public List<ProteinPathwaysDTO> toProteinPathwaysDTOList(List<Protein> from){
        return this.modelMapper.map(from, new TypeToken<List<ProteinPathwaysDTO>>(){}.getType());
    }
}
