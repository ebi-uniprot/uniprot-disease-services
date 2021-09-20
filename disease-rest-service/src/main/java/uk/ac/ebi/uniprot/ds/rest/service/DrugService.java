package uk.ac.ebi.uniprot.ds.rest.service;

import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;
import uk.ac.ebi.uniprot.ds.common.dao.DrugDAO;
import uk.ac.ebi.uniprot.ds.common.dao.ProteinDAO;
import uk.ac.ebi.uniprot.ds.common.model.Disease;
import uk.ac.ebi.uniprot.ds.common.model.Drug;
import uk.ac.ebi.uniprot.ds.common.model.Protein;
import uk.ac.ebi.uniprot.ds.rest.dto.DrugDTO;
import uk.ac.ebi.uniprot.ds.rest.dto.RowToDrugDTO;
import uk.ac.ebi.uniprot.ds.rest.exception.AssetNotFoundException;

import static java.util.stream.Collectors.toSet;

@Service
@Slf4j
public class DrugService {

    @Autowired
    private ProteinDAO proteinDAO;
    @Autowired
    private DrugDAO drugDAO;

    @Autowired
    private DiseaseService diseaseService;

    public List<Drug> getDrugsByDiseaseId(String diseaseId) {
        Optional<Disease> optDisease = this.diseaseService.findByDiseaseId(diseaseId);
        Set<Drug> drugs = new HashSet<>();

        if (optDisease.isPresent()) {
            List<Protein> proteins = optDisease.get().getDiseaseProteins()
                    .stream().map(dp -> dp.getProtein()).collect(Collectors.toList());

            if (proteins != null) {
                drugs = proteins
                        .stream()
                        .filter(pr -> pr.getProteinCrossRefs() != null && !pr.getProteinCrossRefs().isEmpty())
                        .map(pr -> pr.getProteinCrossRefs())
                        .flatMap(List::stream)
                        .filter(xref -> xref.getDrugs() != null && !xref.getDrugs().isEmpty())
                        .map(xref -> xref.getDrugs())
                        .flatMap(List::stream)
                        .collect(toSet());
            }
        }

        List<Drug> drugList = new ArrayList<>(drugs);

        // get the proteins for each drug and get the diseases for each drug
        populateProteins(drugList);

        return drugList;
    }

    public List<DrugDTO> getDrugsByAccession(String accession) {

        Optional<Protein> optProtein = this.proteinDAO.findProteinByAccession(accession);
        if (!optProtein.isPresent()) {
            throw new AssetNotFoundException("Unable to find the accession '" + accession + "'.");
        }

        List<Object[]> drugRows = this.drugDAO.getDrugsByProtein(accession);
        return convertRowsToDTOs(drugRows);
    }

    public List<DrugDTO> getDrugDTOsByDiseaseId(String diseaseId) {
        List<Object[]> drugRows = this.drugDAO.getDrugsByDiseaseId(diseaseId);
        return convertRowsToDTOs(drugRows);
    }

    private List<DrugDTO> convertRowsToDTOs(List<Object[]> drugRows) {
        List<DrugDTO> drugDTOs = new ArrayList<>();
        RowToDrugDTO rowToDrugConverter = new RowToDrugDTO();
        for(Object[] dr : drugRows){
            DrugDTO currDrugDTO = rowToDrugConverter.apply(dr);
            if(drugDTOs.contains(currDrugDTO)){
                DrugDTO prevDrugDTO = drugDTOs.stream().filter(dto -> Objects.equals(dto, currDrugDTO)).findFirst().orElse(null);
                //1. append source id
                prevDrugDTO.getSourceIds().addAll(currDrugDTO.getSourceIds());
                // 2. update phase and clinical trial link if greater value
                if(updateClinicalTrialPhase(prevDrugDTO.getMaxTrialPhase(), currDrugDTO.getMaxTrialPhase(),
                        prevDrugDTO.getClinicalTrialLink(), currDrugDTO.getClinicalTrialLink())){
                    prevDrugDTO.setMaxTrialPhase(currDrugDTO.getMaxTrialPhase());
                    prevDrugDTO.setClinicalTrialLink(currDrugDTO.getClinicalTrialLink());
                }
                // 3. append evidence
                prevDrugDTO.getEvidences().addAll(currDrugDTO.getEvidences());
            } else {
                drugDTOs.add(currDrugDTO);
            }
        }
        return drugDTOs;
    }

    private boolean updateClinicalTrialPhase(Integer oldTrialPhase, Integer newTrialPhase, String oldLink, String newLink){
       return  Objects.isNull(oldTrialPhase) || // case 1. set the phase if it is first time
               newTrialPhase > oldTrialPhase ||// case 2. update phase if it is greater than previous one
               (oldTrialPhase.equals(newTrialPhase) && // case 3. update link for the same phase if oldlink is null and new link is not null
                       Objects.isNull(oldLink) && Objects.nonNull(newLink));
    }

    private Drug createDrugWithoutCrossRefAndDisease(Drug drug){
        Drug.DrugBuilder builder = Drug.builder();
        builder.name(drug.getName()).sourceType(drug.getSourceType());
        builder.sourceId(drug.getSourceId()).moleculeType(drug.getMoleculeType());
        builder.clinicalTrialLink(drug.getClinicalTrialLink());
        builder.clinicalTrialPhase(drug.getClinicalTrialPhase());
        builder.mechanismOfAction(drug.getMechanismOfAction());
        builder.drugEvidences(drug.getDrugEvidences());
        return builder.build();
    }



    private void populateProteins(List<Drug> drugList) {
        if (!drugList.isEmpty()) {
            for (Drug drug : drugList) {
                List<Protein> proteins = this.proteinDAO.findAllByDrugName(drug.getName());
                // get and set the protein accessions
                Set<String> accessions = proteins.stream().map(protein -> protein.getAccession()).collect(toSet());
                drug.setProteins(accessions);

                // get and set the disease names
                Set<Pair<String, Integer>> diseaseNames = proteins
                        .stream()
                        .filter(protein -> protein.getDiseaseProteins() != null && !protein.getDiseaseProteins().isEmpty())
                        .map(p -> p.getDiseaseProteins())
                        .flatMap(Set::stream)
                        .map(dp -> dp.getDisease().getName())
                        .map(name -> Pair.of(name, 0))
                        .collect(toSet());
                drug.setDiseaseProteinCount(diseaseNames);
            }
        }
    }
}
