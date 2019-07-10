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
import uk.ac.ebi.uniprot.ds.common.model.Drug;
import uk.ac.ebi.uniprot.ds.common.model.Protein;
import uk.ac.ebi.uniprot.ds.rest.dto.*;
import uk.ac.ebi.uniprot.ds.rest.filter.RequestCorrelation;
import uk.ac.ebi.uniprot.ds.rest.response.MultipleEntityResponse;
import uk.ac.ebi.uniprot.ds.rest.response.SingleEntityResponse;
import uk.ac.ebi.uniprot.ds.rest.service.DrugService;
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
    private DrugService drugService;

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

    @GetMapping(value = {"/disease/{diseaseId}/proteins"}, produces = MediaType.APPLICATION_JSON_VALUE, name = "Get proteins by the diseaseId")
    public MultipleEntityResponse<ProteinDTO> getProteinsByDiseaseId(@PathVariable("diseaseId") String diseaseId){
        String requestId = RequestCorrelation.getCorrelationId();
        List<Protein> proteins = this.proteinService.getProteinsByDiseaseId(diseaseId);
        List<ProteinDTO> dtoList = toProteinDTOList(proteins);
        return new MultipleEntityResponse<>(requestId, dtoList) ;
    }

    @GetMapping(value={"/protein/{accession}/drugs"}, name = "Get the drugs for a given protein accession")
    public MultipleEntityResponse<DrugDTO> getDrugsByProteinAccession(@PathVariable(name = "accession") String accession) {
        String requestId = RequestCorrelation.getCorrelationId();

        List<Drug> drugs = this.drugService.getDrugsByAccession(accession);
        List<DrugDTO> dtoList = toDrugDTOList(drugs);

        return new MultipleEntityResponse<>(requestId, dtoList);
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
