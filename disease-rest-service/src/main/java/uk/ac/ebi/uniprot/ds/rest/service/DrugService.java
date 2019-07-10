package uk.ac.ebi.uniprot.ds.rest.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.ac.ebi.uniprot.ds.common.dao.ProteinDAO;
import uk.ac.ebi.uniprot.ds.common.model.Disease;
import uk.ac.ebi.uniprot.ds.common.model.Drug;
import uk.ac.ebi.uniprot.ds.common.model.Protein;
import uk.ac.ebi.uniprot.ds.rest.exception.AssetNotFoundException;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class DrugService {

    @Autowired
    private ProteinDAO proteinDAO;

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
                        .map(dp -> dp.getDisease().getName()).collect(Collectors.toSet());
                drug.setDiseases(diseaseNames);
            }
        }
    }
}
