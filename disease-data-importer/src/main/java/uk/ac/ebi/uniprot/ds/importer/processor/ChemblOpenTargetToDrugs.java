package uk.ac.ebi.uniprot.ds.importer.processor;

import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
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
import uk.ac.ebi.uniprot.ds.common.common.SourceType;
import uk.ac.ebi.uniprot.ds.common.dao.ProteinCrossRefDAO;
import uk.ac.ebi.uniprot.ds.common.model.Disease;
import uk.ac.ebi.uniprot.ds.common.model.Drug;
import uk.ac.ebi.uniprot.ds.common.model.DrugEvidence;
import uk.ac.ebi.uniprot.ds.common.model.ProteinCrossRef;
import uk.ac.ebi.uniprot.ds.importer.model.ChemblEntry;
import uk.ac.ebi.uniprot.ds.importer.model.DrugIndication;
import uk.ac.ebi.uniprot.ds.importer.model.Mechanism;
import uk.ac.ebi.uniprot.ds.importer.model.Molecule;
import uk.ac.ebi.uniprot.ds.importer.util.Constants;

@Slf4j
public class ChemblOpenTargetToDrugs implements ItemProcessor<ChemblEntry, List<Drug>> {
    // a local cache to get a list of the cross refs by primary_id during the drug data load
    private Map<String, List<ProteinCrossRef>> targetChemblToXRefsMap;
    private Set<Drug> drugsStored;
    private ChemblService chemblService;

    @Autowired
    private ProteinCrossRefDAO proteinCrossRefDAO;
    private Map<String, Disease> diseaseNameToDiseaseMap;
    // a map to keep efo to omim mapping.. an efo id can be mapped to multiple omim ids
    private Map<String, Set<String>> efo2OmimsMap;
    private String omim2EfoFile;
    private Map<String, List<Mechanism>> chemblMechanisms;
    private Map<String, List<Mechanism>> targetChemblMechanisms;
    private Map<String, Molecule> chemblMolecule;
    private Map<String, List<DrugIndication>> chemblDrugIndications;

    public ChemblOpenTargetToDrugs(String omim2EfoFile, RestTemplate restTemplate) {
        this.drugsStored = new HashSet<>();
        this.targetChemblToXRefsMap = new HashMap<>();
        this.diseaseNameToDiseaseMap = new HashMap<>();
        assert omim2EfoFile != null;
        this.omim2EfoFile = omim2EfoFile;
        this.efo2OmimsMap = new HashMap<>();
        this.chemblMechanisms = new HashMap<>();
        this.targetChemblMechanisms = new HashMap<>();
        this.chemblMolecule = new HashMap<>();
        this.chemblDrugIndications = new HashMap<>();
        this.chemblService = new ChemblService(restTemplate);
    }

    @BeforeStep
    public void init(final StepExecution stepExecution) { //get the cached data from previous step
        this.diseaseNameToDiseaseMap = (Map<String, Disease>) stepExecution.getJobExecution()
                .getExecutionContext().get(Constants.DISEASE_NAME_OR_OMIM_DISEASE_MAP);
    }

    @Override
    public List<Drug> process(ChemblEntry chemblEntry) throws Exception {
        String chemblId = chemblEntry.getChemblId();
        // load the cache once to avoid hitting the db multiple times
        loadProteinCrossRefsCache();
        loadEfo2OmimsMap(this.omim2EfoFile);
        // construct drugs from chembl entry
        Molecule molecule = getMolecule(chemblId);
        Set<Drug> drugs = getDrugsForChemblId(chemblEntry, molecule);
        // compute drugs by targetChemblId of chemblEntry
        Set<Drug> drugsForTargetChemblId = getDrugsForTargetChemblId(chemblEntry, molecule);
        drugs.addAll(drugsForTargetChemblId);
        this.drugsStored.addAll(drugs);
        return new ArrayList<>(drugs);
    }

    private Molecule getMolecule(String chemblId) throws IOException {
        if (!this.chemblMolecule.containsKey(chemblId)) {
            Molecule molecule = this.chemblService.getMolecule(chemblId);
            this.chemblMolecule.put(chemblId, molecule);
        }
        return this.chemblMolecule.get(chemblId);
    }

    private Set<Drug> getDrugsForChemblId(ChemblEntry chemblEntry, Molecule molecule) throws IOException {
        String chemblId = chemblEntry.getChemblId();
        Set<Drug> drugs = new HashSet<>();
        List<DrugIndication> drugIndications = getDrugIndications(chemblId);
        List<Mechanism> mechanisms = getMechanisms(chemblId);
        // flattened the drugs for source chemblId
        for (DrugIndication di : drugIndications) {
            Drug.DrugBuilder drugBuilder = Drug.builder();
            drugBuilder.name(molecule.getName()).sourceType(molecule.getSourceType());
            drugBuilder.sourceId(molecule.getChemblId()).moleculeType(molecule.getMoleculeType());
            String diseaseUrl = Objects.nonNull(di.getEfoId()) ? ChemblEntry.convertToUrl(di.getEfoId()) : null;
            drugBuilder.chemblDiseaseId(diseaseUrl).clinicalTrialPhase(di.getMaxPhase());
            Disease disease = Objects.nonNull(diseaseUrl) ? getDiseaseByEFO(diseaseUrl) : null;
            drugBuilder.disease(disease);
            if (Objects.nonNull(di.getClinicalTrialLinks()) && !di.getClinicalTrialLinks().isEmpty()) {
                Set<Drug> drugsWithLink = flattenedDrugsWithClinicalTrials(drugBuilder, di, mechanisms);
                drugs.addAll(drugsWithLink);
            } else if(Objects.nonNull(mechanisms) && !mechanisms.isEmpty()) {
                Set<Drug> drugsWithMechanism = flattenedDrugsWithMechanisms(drugBuilder, mechanisms);
                drugs.addAll(drugsWithMechanism);
            } else {
                addDrug(drugs, drugBuilder.build());
            }
        }

        return drugs;
    }

    private Set<Drug> getDrugsForTargetChemblId(ChemblEntry chemblEntry, Molecule molecule) throws IOException {
        List<Mechanism> mechanisms = getMechanismsByTargetChemblId(chemblEntry.getTargetChemblId());
        List<Mechanism> filteredMechanisms = mechanisms.stream()
                .filter(m -> chemblEntry.getChemblId().equalsIgnoreCase(m.getMoleculeChemblId())
                        || chemblEntry.getChemblId().equalsIgnoreCase(m.getParentMoleculeChemblId()))
                .collect(Collectors.toList());
        Drug.DrugBuilder drugBuilder = Drug.builder();
        drugBuilder.name(molecule.getName()).sourceType(molecule.getSourceType());
        drugBuilder.sourceId(molecule.getChemblId()).moleculeType(molecule.getMoleculeType());
        drugBuilder.chemblDiseaseId(chemblEntry.getDiseaseUrl()).clinicalTrialPhase(chemblEntry.getPhase());
        drugBuilder.clinicalTrialLink(chemblEntry.getClinicalTrialLink());
        Disease disease = Objects.nonNull(chemblEntry.getDiseaseUrl()) ? getDiseaseByEFO(chemblEntry.getDiseaseUrl()) : null;
        drugBuilder.disease(disease);
        if(filteredMechanisms.isEmpty()){ // without mechanism of action
            Set<Drug> drugs = new HashSet<>();
            addDrug(drugs, drugBuilder.build());
            return drugs;
        } else {
            return flattenedDrugsWithMechanisms(drugBuilder, filteredMechanisms);
        }
    }

    private Set<Drug> flattenedDrugsWithClinicalTrials(Drug.DrugBuilder drugBuilder, DrugIndication di, List<Mechanism> mechanisms){
        Set<Drug> drugs = new HashSet<>();
        for (String tl : di.getClinicalTrialLinks()) {
            drugBuilder.clinicalTrialLink(tl);
            if(Objects.nonNull(mechanisms) && !mechanisms.isEmpty()) {
                drugs = flattenedDrugsWithMechanisms(drugBuilder, mechanisms);
            } else {
                addDrug(drugs, drugBuilder.build());
            }
        }
        return drugs;
    }

    private void addDrug(Set<Drug> drugs, Drug drug){
        if(!this.drugsStored.contains(drug)){
            drugs.add(drug);
        }
    }
    Set<Drug> flattenedDrugsWithMechanisms(Drug.DrugBuilder drugBuilder, List<Mechanism> mechanisms){
        Set<Drug> drugs = new HashSet<>();
        for (Mechanism m : mechanisms) {
            drugBuilder.mechanismOfAction(m.getMechanismOfAction());
            List<ProteinCrossRef> xrefs = this.targetChemblToXRefsMap.get(m.getTargetChemblId().toUpperCase());
            if (Objects.nonNull(xrefs) && !xrefs.isEmpty()) {
                for (ProteinCrossRef xref : xrefs) {
                    drugBuilder.proteinCrossRef(xref);
                    Drug drug = drugBuilder.build();
                    List<DrugEvidence> drugEvidences = getDrugEvidences(m.getEvidences(), drug);
                    drug.setDrugEvidences(drugEvidences);
                    addDrug(drugs, drug);
                }
            } else {
                Drug drug = drugBuilder.build();
                List<DrugEvidence> drugEvidences = getDrugEvidences(m.getEvidences(), drug);
                drug.setDrugEvidences(drugEvidences);
                addDrug(drugs, drug);
            }
        }
        return drugs;
    }

    private List<DrugIndication> getDrugIndications(String chemblId) throws IOException {
        if(!this.chemblDrugIndications.containsKey(chemblId)) {
            List<DrugIndication> drugIndications = this.chemblService.getDrugIndications(chemblId);
            this.chemblDrugIndications.put(chemblId, drugIndications);
        }
        return this.chemblDrugIndications.get(chemblId);
    }

    List<Mechanism> getMechanisms(String chemblId) throws IOException {
        if(!this.chemblMechanisms.containsKey(chemblId)){
            List<Mechanism> mechs = this.chemblService.getMechanisms(chemblId, "molecule_chembl_id");
            this.chemblMechanisms.put(chemblId, mechs);
        }
        return this.chemblMechanisms.get(chemblId);
    }

    List<Mechanism> getMechanismsByTargetChemblId(String chemblId) throws IOException {
        if(!this.targetChemblMechanisms.containsKey(chemblId)){
            List<Mechanism> mechs = this.chemblService.getMechanisms(chemblId, "target_chembl_id");
            this.targetChemblMechanisms.put(chemblId, mechs);
        }
        return this.targetChemblMechanisms.get(chemblId);
    }

    private void loadProteinCrossRefsCache() {
        if (this.targetChemblToXRefsMap.isEmpty()) {
            List<ProteinCrossRef> xrefs = this.proteinCrossRefDAO.findAllByDbType(SourceType.ChEMBL.name());

            xrefs.stream().forEach(xref -> {
                List<ProteinCrossRef> primaryIdXRefs = new ArrayList<>();
                if (this.targetChemblToXRefsMap.containsKey(xref.getPrimaryId())) {
                    primaryIdXRefs = this.targetChemblToXRefsMap.get(xref.getPrimaryId());
                }

                primaryIdXRefs.add(xref);
                this.targetChemblToXRefsMap.put(xref.getPrimaryId(), primaryIdXRefs);
            });
        }
    }

    private Disease getDiseaseByEFO(String diseaseUrl) {
        // get from the diseaseName to disease cache
        String diseaseId = extractDiseaseId(diseaseUrl);
        Disease disease = this.diseaseNameToDiseaseMap.get(diseaseId);
        if (Objects.isNull(disease)) {
            // get the omims from efo 2 omim cache
            Set<String> omims = this.efo2OmimsMap.get(diseaseId);
            if (Objects.nonNull(omims)) { // exact one match to avoid mismatch
                if (omims.size() == 1) {
                    disease = this.diseaseNameToDiseaseMap.get(new ArrayList<>(omims).get(0));
                    if (disease != null) {
                        this.diseaseNameToDiseaseMap.put(diseaseId, disease);
                        log.info("Found disease in cache for efoId {}", diseaseId);
                    } else {
                        log.info("No disease found in cache for efoId {}", diseaseId);
                    }
                } else {
                    log.info("EFO {} has more than one OMIM mapping. Ignoring it..", diseaseId);
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
        if (this.efo2OmimsMap.isEmpty()) {
            Scanner scanner = null;
            try {
                scanner = new Scanner(this.getClass().getClassLoader().getResourceAsStream(omim2EfoFile));
                if (Objects.isNull(scanner)) {
                    throw new IllegalArgumentException("Cannot read file " + omim2EfoFile);
                }

                while (scanner.hasNextLine()) {
                    String[] row = scanner.nextLine().split("\t");
                    assert row.length == 2;
                    String efoId = extractDiseaseId(row[1]);
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

    private String extractDiseaseId(String url){
        // get in form EFO:1000890 from http://www.ebi.ac.uk/efo/EFO_0004243
        String diseaseId = url.substring(url.lastIndexOf("/")+1);
        String[] tokens = diseaseId.split("_");
        return tokens.length == 2 ? tokens[0] + ":" + tokens[1] : "";
    }

    // for tests
    void setProteinCrossRefDAO(ProteinCrossRefDAO proteinCrossRefDAO) {
        this.proteinCrossRefDAO = proteinCrossRefDAO;
    }

    void setDiseaseNameToDiseaseMap(Map<String, Disease> diseaseNameToDiseaseMap) {
        this.diseaseNameToDiseaseMap = diseaseNameToDiseaseMap;
    }
}
