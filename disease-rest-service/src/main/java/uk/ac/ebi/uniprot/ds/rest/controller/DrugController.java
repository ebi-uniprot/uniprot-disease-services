package uk.ac.ebi.uniprot.ds.rest.controller;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
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
@RestController
@RequestMapping("/v1/ds")
@Validated
public class DrugController {
    @Autowired
    private ProteinService proteinService;

    @Autowired
    private DiseaseService diseaseService;

    @Autowired
    private ModelMapper modelMapper;

    @GetMapping(value={"/drug/{drugName}/proteins"}, name = "Get the proteins for a given drug name")
    public MultipleEntityResponse<ProteinDTO> getProteinsByDrug(@PathVariable String drugName){
        String requestId = RequestCorrelation.getCorrelationId();
        List<Protein> proteins = this.proteinService.getProteinsByDrugName(drugName);
        List<ProteinDTO> dtoList = ProteinDTO.toProteinDTOList(proteins, this.modelMapper);
        return new MultipleEntityResponse<>(requestId, dtoList);
    }

    @GetMapping(value={"/drug/{drugName}/diseases"}, name = "Get the diseases for a given drug name")
    public MultipleEntityResponse<DiseaseDTO> getDiseasesByDrug(@PathVariable String drugName){
        String requestId = RequestCorrelation.getCorrelationId();
        List<Disease> diseases = this.diseaseService.getDiseasesByDrugName(drugName);
        List<DiseaseDTO> diseaseDTOList = DiseaseDTO.toDiseaseDTOList(diseases, this.modelMapper);

        return new MultipleEntityResponse<>(requestId, diseaseDTOList);
    }
}
