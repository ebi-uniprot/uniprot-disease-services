package uk.ac.ebi.uniprot.ds.importer.processor;

import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;
import uk.ac.ebi.uniprot.ds.common.dao.ProteinCrossRefDAO;
import uk.ac.ebi.uniprot.ds.common.model.Disease;
import uk.ac.ebi.uniprot.ds.common.model.Drug;
import uk.ac.ebi.uniprot.ds.common.model.DrugEvidence;
import uk.ac.ebi.uniprot.ds.common.model.ProteinCrossRef;
import uk.ac.ebi.uniprot.ds.importer.model.ChemblOpenTarget;
import uk.ac.ebi.uniprot.ds.importer.util.Constants;

@Slf4j
public class ChemblOpenTargetToDrugs implements ItemProcessor<ChemblOpenTarget, List<Drug>> {
    // a local cache to get a list of the cross refs by primary_id during the drug data load
    private Map<String, List<ProteinCrossRef>> targetChemblToXRefsMap;
    private Set<Drug> drugsStored;

    @Autowired
    private ProteinCrossRefDAO proteinCrossRefDAO;
    private Map<String, Disease> diseaseNameToDiseaseMap;
    // a map to keep efo to omim mapping.. an efo id can be mapped to multiple omim ids
    private Map<String, Set<String>> efo2OmimsMap;
    private String omim2EfoFile;
    public ChemblOpenTargetToDrugs(String omim2EfoFile){
        this.drugsStored = new HashSet<>();
        this.targetChemblToXRefsMap = new HashMap<>();
        this.diseaseNameToDiseaseMap = new HashMap<>();
        assert omim2EfoFile != null;
        this.omim2EfoFile = omim2EfoFile;
        this.efo2OmimsMap = new HashMap<>();
    }

    @BeforeStep
    public void init(final StepExecution stepExecution) { //get the cached data from previous step
        this.diseaseNameToDiseaseMap = (Map<String, Disease>) stepExecution.getJobExecution()
                .getExecutionContext().get(Constants.DISEASE_NAME_OR_OMIM_DISEASE_MAP);
    }

    @Override
    public List<Drug> process(ChemblOpenTarget item) throws Exception {
        // load the cache once to avoid hitting the db multiple times
        loadProteinCrossRefsCache();
        loadEfo2OmimsMap(this.omim2EfoFile);

        String targetChemblUrl = item.getChemblTargetUrl();
        String targetChemblId = targetChemblUrl.substring(targetChemblUrl.lastIndexOf(Constants.FORWARD_SLASH) + 1);

        List<ProteinCrossRef> xrefs = this.targetChemblToXRefsMap.get(targetChemblId.toUpperCase());

        Set<Drug> drugs = new HashSet<>();

        if (xrefs != null) {
            drugs = xrefs.stream()
                    .map(xref -> getDrug(xref, item))
                    .filter(drug -> !this.drugsStored.contains(drug))
                    .collect(Collectors.toSet());
            // add the drug in set to avoid duplicate insertion
            this.drugsStored.addAll(drugs);
        } else { // else add drug with or without disease
            Drug drug = getDrug(null, item);
            if(!this.drugsStored.contains(drug)){
                drugs.add(drug);
                this.drugsStored.add(drug);
            }
        }

        return new ArrayList<>(drugs);
    }

    private void loadProteinCrossRefsCache() {
        if(this.targetChemblToXRefsMap.isEmpty()){
            List<ProteinCrossRef> xrefs = this.proteinCrossRefDAO.findAllByDbType(Constants.ChEMBL_STR);

            xrefs.stream().forEach(xref -> {
                List<ProteinCrossRef> primaryIdXRefs = new ArrayList<>();
                if(this.targetChemblToXRefsMap.containsKey(xref.getPrimaryId())){
                    primaryIdXRefs = this.targetChemblToXRefsMap.get(xref.getPrimaryId());
                }

                primaryIdXRefs.add(xref);
                this.targetChemblToXRefsMap.put(xref.getPrimaryId(), primaryIdXRefs);
            });
        }
    }

    private Drug getDrug(ProteinCrossRef xref, ChemblOpenTarget item) {
        String srcChemblUrl = item.getChemblSourceUrl();
        String srcChemblId = srcChemblUrl.substring(srcChemblUrl.lastIndexOf(Constants.FORWARD_SLASH) + 1);
        Disease disease = Objects.nonNull(item.getDiseaseId()) ? getDiseaseByEFO(item.getDiseaseId()) : null;
        Drug.DrugBuilder drugBuilder = Drug.builder();
        drugBuilder.mechanismOfAction(item.getMechOfAction()).clinicalTrialLink(item.getClinicalTrialLink());
        drugBuilder.clinicalTrialPhase(item.getClinicalTrialPhase()).proteinCrossRef(xref);
        drugBuilder.moleculeType(item.getMoleculeType()).name(item.getMoleculeName()).sourceId(srcChemblId);
        drugBuilder.sourceType(Constants.ChEMBL_STR);
        drugBuilder.chemblDiseaseId(item.getDiseaseId());
        drugBuilder.disease(disease);
        Drug drug = drugBuilder.build();
        List<DrugEvidence> drugEvidences = getDrugEvidences(item.getDrugEvidences(), drug);
        drug.setDrugEvidences(drugEvidences);
        return drug;
    }



    private Disease getDiseaseByEFO(String diseaseId) {
        String efoId = extractEfoId(diseaseId);
        // get from the diseaseName to disease cache
        Disease disease = this.diseaseNameToDiseaseMap.get(efoId);
        if(Objects.isNull(disease)){
            // get the omims from efo 2 omim cache
            Set<String> omims = this.efo2OmimsMap.get(efoId);
            if(Objects.nonNull(omims)){ // exact one match to avoid mismatch
                if(omims.size() == 1) {
                    disease = this.diseaseNameToDiseaseMap.get(new ArrayList<>(omims).get(0));
                    if (disease != null) {
                        this.diseaseNameToDiseaseMap.put(efoId, disease);
                        log.info("Found disease in cache for efoId {}", efoId);
                    } else {
                        log.info("No disease found in cache for efoId {}", efoId);
                    }
                } else {
                    log.info("EFO {} has more than one OMIM mapping. Ignoring it..", efoId);
                }
            }
        }
        return disease;
    }

    private List<DrugEvidence> getDrugEvidences(List<String> drugEvidences, Drug drug) {

        if (drugEvidences != null) {
            return drugEvidences.stream()
                    .map(evidence -> new DrugEvidence(Constants.PUBMED_STR, evidence, drug))
                    .collect(Collectors.toList());
        }

        return Collections.emptyList();
    }

    private void loadEfo2OmimsMap(String omim2EfoFile) {
        if(this.efo2OmimsMap.isEmpty()) {
            Scanner scanner = null;
            try {
                scanner = new Scanner(this.getClass().getClassLoader().getResourceAsStream(omim2EfoFile));
                if(Objects.isNull(scanner)){
                    throw new IllegalArgumentException("Cannot read file " + omim2EfoFile);
                }

                while (scanner.hasNextLine()) {
                    String[] row = scanner.nextLine().split("\t");
                    assert row.length == 2;
                    String efoId = extractEfoId(row[1]);
                    if (this.efo2OmimsMap.containsKey(efoId)) {
                        this.efo2OmimsMap.get(efoId).add(row[0]);
                    } else {
                        Set<String> omims = new HashSet<>();
                        omims.add(row[0]);
                        this.efo2OmimsMap.put(efoId, omims);
                    }
                }
            } finally {
                if (scanner != null) {
                    scanner.close();
                }
            }
        }
    }

    private String extractEfoId(String efoUrl){
        // get in form EFO_1000890
        String efo_ = efoUrl.substring(efoUrl.lastIndexOf("/")+1);
        String[] efoId = efo_.split("_");
        assert efoId.length == 2;
        return efoId[0] + ":" + efoId[1];
    }
}
