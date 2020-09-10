package uk.ac.ebi.uniprot.ds.importer.processor;

import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import uk.ac.ebi.uniprot.ds.common.dao.ProteinCrossRefDAO;
import uk.ac.ebi.uniprot.ds.common.model.Disease;
import uk.ac.ebi.uniprot.ds.common.model.Drug;
import uk.ac.ebi.uniprot.ds.common.model.DrugEvidence;
import uk.ac.ebi.uniprot.ds.common.model.ProteinCrossRef;
import uk.ac.ebi.uniprot.ds.importer.model.ChemblOpenTarget;
import uk.ac.ebi.uniprot.ds.importer.util.Constants;

import java.util.*;
import java.util.stream.Collectors;

public class ChemblOpenTargetToDrugs implements ItemProcessor<ChemblOpenTarget, List<Drug>> {
    // a local cache to get a list of the cross refs by primary_id during the drug data load
    private Map<String, List<ProteinCrossRef>> targetChemblToXRefsMap;
    private Set<Drug> drugsStored;

    @Autowired
    private ProteinCrossRefDAO proteinCrossRefDAO;
    private Map<String, Disease> diseaseNameToDiseaseMap;

    public ChemblOpenTargetToDrugs(){
        this.drugsStored = new HashSet<>();
        this.targetChemblToXRefsMap = new HashMap<>();
        this.diseaseNameToDiseaseMap = new HashMap<>();
    }

    @BeforeStep
    public void init(final StepExecution stepExecution) { //get the cached data from previous step
        this.diseaseNameToDiseaseMap = (Map<String, Disease>) stepExecution.getJobExecution()
                .getExecutionContext().get(Constants.DISEASE_NAME_OR_OMIM_DISEASE_MAP);
        if(Objects.nonNull(this.diseaseNameToDiseaseMap)) {
            System.out.println("******************** total diseaseNameToDiseaseMap cache size:" + this.diseaseNameToDiseaseMap.size());
        } else {
            System.out.println("******************** total diseaseNameToDiseaseMap cache size: null");
        }
    }

    @Override
    public List<Drug> process(ChemblOpenTarget item) throws Exception {
        // load the cache once to avoid hitting the db multiple times
        loadProteinCrossRefsCache();

        String targetChemblUrl = item.getChemblTargetUrl();
        String targetChemblId = targetChemblUrl.substring(targetChemblUrl.lastIndexOf(Constants.FORWARD_SLASH) + 1);

        List<ProteinCrossRef> xrefs = this.targetChemblToXRefsMap.get(targetChemblId.toUpperCase());

        List<Drug> drugs = new ArrayList<>();

        if (xrefs != null) {
            drugs = xrefs.stream()
                    .map(xref -> getDrug(xref, item))
                    .filter(drug -> !this.drugsStored.contains(drug))
                    .collect(Collectors.toList());
            // add the drug in set to avoid duplicate insertion
            this.drugsStored.addAll(drugs);
        }

        return drugs;
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

        Drug.DrugBuilder drugBuilder = Drug.builder();
        drugBuilder.mechanismOfAction(item.getMechOfAction()).clinicalTrialLink(item.getClinicalTrialLink());
        drugBuilder.clinicalTrialPhase(item.getClinicalTrialPhase()).proteinCrossRef(xref);
        drugBuilder.moleculeType(item.getMoleculeType()).name(item.getMoleculeName()).sourceId(srcChemblId);
        drugBuilder.sourceType(Constants.ChEMBL_STR);
        drugBuilder.chemblDiseaseId(item.getDiseaseId());
        Drug drug = drugBuilder.build();
        List<DrugEvidence> drugEvidences = getDrugEvidences(item.getDrugEvidences(), drug);
        drug.setDrugEvidences(drugEvidences);

        return drug;
    }

    private List<DrugEvidence> getDrugEvidences(List<String> drugEvidences, Drug drug) {

        if (drugEvidences != null) {
            return drugEvidences.stream()
                    .map(evidence -> new DrugEvidence(Constants.PUBMED_STR, evidence, drug))
                    .collect(Collectors.toList());
        }

        return null;

    }
}
