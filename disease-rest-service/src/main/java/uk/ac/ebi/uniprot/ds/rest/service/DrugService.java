package uk.ac.ebi.uniprot.ds.rest.service;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.ac.ebi.uniprot.ds.common.dao.DrugDAO;
import uk.ac.ebi.uniprot.ds.common.dao.ProteinDAO;
import uk.ac.ebi.uniprot.ds.common.model.Disease;
import uk.ac.ebi.uniprot.ds.common.model.DiseaseProtein;
import uk.ac.ebi.uniprot.ds.common.model.Drug;
import uk.ac.ebi.uniprot.ds.common.model.Protein;
import uk.ac.ebi.uniprot.ds.rest.dto.DrugDTO;
import uk.ac.ebi.uniprot.ds.rest.exception.AssetNotFoundException;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.mapping;
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

    public List<Drug> getDrugsByAccession(String accession) {

        Optional<Protein> optProtein = this.proteinDAO.findProteinByAccession(accession);
        if (!optProtein.isPresent()) {
            throw new AssetNotFoundException("Unable to find the accession '" + accession + "'.");
        }

        List<Drug> drugs = this.drugDAO.getDrugsByProtein(accession);
        // [disease1, disease2]
        Set<Disease> diseases = drugs.stream().filter(drug -> Objects.nonNull(drug.getDisease())).map(Drug::getDisease)
                .collect(Collectors.toSet());
        Map<String, Disease> nameToDisease = diseases.stream().collect(Collectors.toMap(Disease::getName, Function.identity()));

        // [drugName --> [diseaseName1, diseaseName2]]
        Map<String, Set<String>> drugToDiseases = drugs.stream()
                .collect(Collectors.groupingBy(drug -> drug.getName(), mapping(drug -> extractDiseaseName(drug), toSet())));

        // [drugName --> [proteinAccession1, proteinAccession2]]
        Map<String, Set<String>> drugToProteins = drugs.stream()
                .filter(drug -> Objects.nonNull(drug.getProteinCrossRef()))
                .collect(Collectors.groupingBy(drug -> drug.getName(),
                        mapping(drug -> drug.getProteinCrossRef().getProtein().getAccession(), toSet())));

        Set<Drug> resultDrugs = new HashSet<>();

        for(Drug drug : drugs){
            // drug without disease id and cross ref id
            Drug slimDrug = createDrugWithoutCrossRefAndDisease(drug);
            Set<Pair<String, Integer>> diseaseProteinCount = getDiseaseProteinCount(drug.getName(), drugToDiseases, nameToDisease);
            // fill the disease names with protein counts
            slimDrug.setDiseaseProteinCount(diseaseProteinCount);
            // fill the protein accessions
            slimDrug.setProteins(drugToProteins.get(slimDrug.getName()));
            resultDrugs.add(slimDrug);
        }

        return new ArrayList<>(resultDrugs);
    }

    public List<DrugDTO> getDrugDTOsByDiseaseId(String diseaseId) {
        List<Object[]> drugs = this.drugDAO.getDrugsByDiseaseId(diseaseId);
        Map<Long, Integer> diseaseIdProteinCount = new HashMap<>();
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
            Set<DrugDTO.BasicDiseaseDTO> diseases = builder.build().getDiseases() != null ? builder.build().getDiseases() : new HashSet<>();
            if(drug[9] != null && drug[10] != null) {
                Long did = (Long) drug[11];
                Integer proteinCount = did == null ? 0 : getProteinCount((Long) drug[11], diseaseIdProteinCount);
                DrugDTO.BasicDiseaseDTO disease = DrugDTO.BasicDiseaseDTO.builder().diseaseName((String) drug[9])
                        .diseaseId((String) drug[10]).proteinCount(proteinCount).build();
                diseases.add(disease);
            }
            builder.diseases(diseases);
            // put in the map
            drugNameBuilder.put((String) drug[0], builder);
        }

        populateProteins(drugNameBuilder);

        List<DrugDTO> drugDTOs = drugNameBuilder
                .values()
                .stream()
                .map(DrugDTO.DrugDTOBuilder::build)
                .collect(Collectors.toList());

        return drugDTOs;
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
    private void populateProteins(Map<String, DrugDTO.DrugDTOBuilder> drugNameBuilder) {
        for(Map.Entry<String, DrugDTO.DrugDTOBuilder> entry : drugNameBuilder.entrySet()){
            // get all proteins for a given drug
            List<Protein> proteins = this.proteinDAO.findAllByDrugName(entry.getKey());
            Set<String> accessions = proteins.stream().map(protein -> protein.getAccession()).collect(toSet());
            Set<String> allAccessions = entry.getValue().build().getProteins() != null ? entry.getValue().build().getProteins() : new HashSet<>();
            allAccessions.addAll(accessions);
            entry.getValue().proteins(allAccessions);
        }
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

    private String extractDiseaseName(Drug drug) {
        if(Objects.nonNull(drug.getDisease())) {
            return  drug.getDisease().getName();
        } else {
            return drug.getChemblDiseaseId();
        }
    }

    private Set<Pair<String, Integer>> getDiseaseProteinCount(String drugName, Map<String, Set<String>> drugToDiseases,
                                                              Map<String, Disease> nameToDisease) {
        Set<Pair<String, Integer>> dpPair = new HashSet<>();
        for(String diseaseName : drugToDiseases.get(drugName)){
            if(nameToDisease.containsKey(diseaseName)){
                Disease disease = nameToDisease.get(diseaseName);
                dpPair.add(Pair.of(diseaseName, disease.getDiseaseProteins().size()));
            } else {
                dpPair.add(Pair.of(diseaseName, 0));
            }
        }
        return dpPair;
    }

    private Integer getProteinCount(Long diseaseId, Map<Long, Integer> map){
        if(!map.containsKey(diseaseId)) {
            Optional<Disease> optDisease = this.diseaseService.findById(diseaseId);
            if(!optDisease.isPresent()){
                throw new AssetNotFoundException("Disease id " + diseaseId + " not found");
            }
            map.put(diseaseId, optDisease.get().getDiseaseProteins().size());
        }
        return map.get(diseaseId);
    }
}
