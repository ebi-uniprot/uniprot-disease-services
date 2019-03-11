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
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import uk.ac.ebi.uniprot.ds.common.model.Disease;
import uk.ac.ebi.uniprot.ds.rest.dto.DiseaseDTO;
import uk.ac.ebi.uniprot.ds.rest.filter.RequestCorrelation;
import uk.ac.ebi.uniprot.ds.rest.response.MultipleEntityResponse;
import uk.ac.ebi.uniprot.ds.rest.response.SingleEntityResponse;
import uk.ac.ebi.uniprot.ds.rest.service.DiseaseService;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/v1/ds")
@Validated
public class DiseaseController {

    @Autowired
    private DiseaseService diseaseService;

    @Autowired
    private ModelMapper modelMapper;

    @GetMapping(value = {"/diseases/{diseaseId}"})
    public SingleEntityResponse<DiseaseDTO> getDisease(@PathVariable("diseaseId") String diseaseId){
        String requestId = RequestCorrelation.getCorrelationId();
        Optional<Disease> optDisease = this.diseaseService.findByDiseaseId(diseaseId);
        Disease disease = optDisease.orElse(new Disease());
        DiseaseDTO diseaseDTO = convertToDTO(disease);
        return new SingleEntityResponse<>(requestId, false, null, diseaseDTO) ;
    }

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

    @GetMapping(value={"/protein/{accession}/diseases"}, name = "Get the diseases for a given accession")
    public MultipleEntityResponse<DiseaseDTO> getProteinDiseases(@PathVariable(name = "accession") String accession) {
        String requestId = RequestCorrelation.getCorrelationId();

        List<Disease> diseases = this.diseaseService.getDiseasesByProteinAccession(accession);
        List<DiseaseDTO> diseaseDTOList = toDiseaseDTOList(diseases);

        return new MultipleEntityResponse<>(requestId, diseaseDTOList, null, null);
    }

    private DiseaseDTO convertToDTO(Disease disease) {
        DiseaseDTO diseaseDTO = modelMapper.map(disease, DiseaseDTO.class);
        return diseaseDTO;
    }

    private List<DiseaseDTO> toDiseaseDTOList(List<Disease> from){
        return this.modelMapper.map(from, new TypeToken<List<DiseaseDTO>>(){}.getType());
    }
}
