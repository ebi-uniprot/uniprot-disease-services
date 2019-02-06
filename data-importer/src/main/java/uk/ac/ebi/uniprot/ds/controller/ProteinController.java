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
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import uk.ac.ebi.uniprot.ds.controller.dto.ProteinDTO;
import uk.ac.ebi.uniprot.ds.controller.dto.ProteinPathwaysDTO;
import uk.ac.ebi.uniprot.ds.controller.filter.RequestCorrelation;
import uk.ac.ebi.uniprot.ds.controller.response.MultipleEntityResponse;
import uk.ac.ebi.uniprot.ds.controller.response.SingleEntityResponse;
import uk.ac.ebi.uniprot.ds.model.Protein;
import uk.ac.ebi.uniprot.ds.service.ProteinService;

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

    @GetMapping(value = {"/proteins/{accession}"})
    public SingleEntityResponse<ProteinDTO> getDisease(@PathVariable("accession") String accession){
        String requestId = RequestCorrelation.getCorrelationId();
        Optional<Protein> optProtein = this.proteinService.getProteinByAccession(accession);
        Protein protein = optProtein.get();
        ProteinDTO proteinDTO = convertToDTO(protein);
        return new SingleEntityResponse<>(requestId, false, null, proteinDTO) ;
    }

    @GetMapping(value={"/{accessions}/pathways"}, name = "Get the pathways for the given list of accession")
    public MultipleEntityResponse<ProteinPathwaysDTO> getProteinsPathway(
            @Size(min = 1, max = 20, message = "The total count of accessions passed must be between 1 and 20 both inclusive.")
            @PathVariable(name = "accessions")
                    List<String> accessions)
    {
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
