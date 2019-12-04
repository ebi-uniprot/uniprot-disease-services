package uk.ac.ebi.uniprot.ds.rest.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.ac.ebi.uniprot.ds.common.dao.DrugDAO;
import uk.ac.ebi.uniprot.ds.common.dao.ProteinDAO;
import uk.ac.ebi.uniprot.ds.common.model.Disease;
import uk.ac.ebi.uniprot.ds.common.model.Drug;
import uk.ac.ebi.uniprot.ds.common.model.Protein;
import uk.ac.ebi.uniprot.ds.rest.dto.DrugDTO;
import uk.ac.ebi.uniprot.ds.rest.exception.AssetNotFoundException;

import java.util.*;
import java.util.stream.Collectors;

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
                        .collect(Collectors.toSet());
            }
        }

        List<Drug> drugList = new ArrayList<>(drugs);

        // get the proteins for each drug and get the diseases for each drug
        populateProteinsAndDiseases(drugList);

        return drugList;
    }

    public List<Drug> getDrugsByAccession(String accession) {

        Optional<Protein> optProtein = this.proteinDAO.findProteinByAccession(accession);
        if (!optProtein.isPresent()) {
            throw new AssetNotFoundException("Unable to find the accession '" + accession + "'.");
        }

        Set<Drug> drugs = new HashSet<>();

        if (optProtein.get().getProteinCrossRefs() != null && !optProtein.get().getProteinCrossRefs().isEmpty()) {
            drugs = optProtein.get().getProteinCrossRefs()
                    .stream().filter(xref -> xref.getDrugs() != null && !xref.getDrugs().isEmpty())
                    .map(xref -> xref.getDrugs())
                    .flatMap(List::stream)
                    .collect(Collectors.toSet());
        }

        List<Drug> drugList = new ArrayList<>(drugs);

        // get the proteins for each drug and get the diseases for each drug
        populateProteinsAndDiseases(drugList);

        return drugList;
    }

    public List<DrugDTO> getDrugDTOsByDiseaseId(String diseaseId) {
        List<Object[]> drugs = this.drugDAO.getDrugsByDiseaseId(diseaseId);
        // drug name is unique so using as key
        Map<String, DrugDTO.DrugDTOBuilder> drugNameBuilder = new HashMap<>();
        for(Object[] drug : drugs){
            DrugDTO.DrugDTOBuilder builder = drugNameBuilder.getOrDefault((String)drug[0], DrugDTO.builder());
            builder.name((String) drug[0]);
            builder.sourceType((String) drug[1]);
            builder.sourceId((String) drug[2]);
            builder.moleculeType((String) drug[3]);
            builder.clinicalTrialPhase((Integer) drug[4]);
            builder.mechanismOfAction((String) drug[5]);
            builder.clinicalTrialLink((String) drug[6]);
            // evidences
            Set<String> evidences = builder.build().getEvidences() != null ? builder.build().getEvidences() : new HashSet<>();
            if(drug[7] != null) {
                evidences.add((String) drug[7]);
            }
            builder.evidences(evidences);

            // proteins
            Set<String> proteins = builder.build().getProteins() != null ? builder.build().getProteins() : new HashSet<>();
            if(drug[8] != null) {
                proteins.add((String) drug[8]);
            }
            builder.proteins(proteins);

            // diseases
            Set<String> diseases = builder.build().getDiseases() != null ? builder.build().getDiseases() : new HashSet<>();
            if(drug[9] != null) {
                diseases.add((String) drug[9]);
            }
            builder.diseases(diseases);
            // put in the map
            drugNameBuilder.put((String) drug[0], builder);
        }

        populateProteinsAndDiseases(drugNameBuilder);

        List<DrugDTO> drugDTOs = drugNameBuilder
                .values()
                .stream()
                .map(DrugDTO.DrugDTOBuilder::build)
                .collect(Collectors.toList());

        return drugDTOs;
    }

    private void populateProteinsAndDiseases(Map<String, DrugDTO.DrugDTOBuilder> drugNameBuilder) {
        for(Map.Entry<String, DrugDTO.DrugDTOBuilder> entry : drugNameBuilder.entrySet()){
            // get all proteins for a given drug
            List<Protein> proteins = this.proteinDAO.findAllByDrugName(entry.getKey());
            Set<String> accessions = proteins.stream().map(protein -> protein.getAccession()).collect(Collectors.toSet());
            Set<String> allAccessions = entry.getValue().build().getProteins() != null ? entry.getValue().build().getProteins() : new HashSet<>();
            allAccessions.addAll(accessions);
            entry.getValue().proteins(allAccessions);

            // get all diseases for a given drug
            Set<String> diseaseNames = proteins
                    .stream()
                    .filter(protein -> protein.getDiseaseProteins() != null && !protein.getDiseaseProteins().isEmpty())
                    .map(p -> p.getDiseaseProteins())
                    .flatMap(Set::stream)
                    .map(dp -> dp.getDisease().getName())
                    .collect(Collectors.toSet());

            Set<String> allDiseases = entry.getValue().build().getDiseases() != null ? entry.getValue().build().getDiseases() : new HashSet<>();
            allDiseases.addAll(diseaseNames);
            entry.getValue().diseases(allDiseases);
        }
    }

    private void populateProteinsAndDiseases(List<Drug> drugList) {
        if (!drugList.isEmpty()) {
            for (Drug drug : drugList) {
                List<Protein> proteins = this.proteinDAO.findAllByDrugName(drug.getName());
                // get and set the protein accessions
                Set<String> accessions = proteins.stream().map(protein -> protein.getAccession()).collect(Collectors.toSet());
                drug.setProteins(accessions);

                // get and set the disease names
                Set<String> diseaseNames = proteins
                        .stream()
                        .filter(protein -> protein.getDiseaseProteins() != null && !protein.getDiseaseProteins().isEmpty())
                        .map(p -> p.getDiseaseProteins())
                        .flatMap(Set::stream)
                        .map(dp -> dp.getDisease().getName())
                        .collect(Collectors.toSet());
                drug.setDiseases(diseaseNames);
            }
        }
    }
}
