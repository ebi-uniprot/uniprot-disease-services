package uk.ac.ebi.uniprot.ds.importer.processor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import uk.ac.ebi.uniprot.ds.common.dao.CrossRefDAO;
import uk.ac.ebi.uniprot.ds.common.dao.DiseaseDAO;
import uk.ac.ebi.uniprot.ds.common.dao.SynonymDAO;
import uk.ac.ebi.uniprot.ds.common.model.CrossRef;
import uk.ac.ebi.uniprot.ds.common.model.Disease;
import uk.ac.ebi.uniprot.ds.common.model.Synonym;
import uk.ac.ebi.uniprot.ds.importer.reader.diseaseontology.OBOTerm;
import uk.ac.ebi.uniprot.ds.importer.util.Constants;

import java.io.FileNotFoundException;
import java.util.*;

@Slf4j
public class MondoTermToDiseaseConverter implements ItemProcessor<OBOTerm, Disease> {
    @Autowired
    private DiseaseDAO diseaseDAO;
    @Autowired
    private SynonymDAO synonymDAO;
    @Autowired
    private CrossRefDAO crossRefDAO;

    private Map<String, Disease> diseaseNameToDiseaseMap; // cache to quick look up for disease by name, synonym or omim id
    private List<OBOTerm> mondoOboTerms; // list of oboterms. will be used in next step to create parent-child relationship
    private StepExecution stepExecution;

    @BeforeStep //initialisation like load cache and set cache in step context to be used in next step
    public void init(final StepExecution stepExecution) throws FileNotFoundException {
        this.stepExecution = stepExecution;
        this.mondoOboTerms = new ArrayList<>();
        this.diseaseNameToDiseaseMap = new HashMap<>();
        loadCache();
        setCacheInStepContext();
    }

    @Override
    public Disease process(OBOTerm oboTerm) {
        this.mondoOboTerms.add(oboTerm); // add the term in list to be passed to next step.
        Disease disease;
        // for each Mondo OBOTerm,
        //case 1 if mondo disease(oboterm) is there in humdisease and name matches then do nothing
        //case 2 if mondo disease(oboterm) is there in humdisease and name doesn't match then update synonym (if not already there) and cache
        //case 3 if mondo disease(oboterm) is not there in humdisease then create a new disease and add in cache
        // try to get the humdisease by disease name or omim id
        Disease cachedDisease = this.diseaseNameToDiseaseMap.get(oboTerm.getName().toLowerCase());
        // if cache doesn't have disease name, try to find by omim id
        String mondoOmim = getOMIMId(oboTerm);
        if (cachedDisease == null && mondoOmim != null) {
            cachedDisease = this.diseaseNameToDiseaseMap.get(mondoOmim.toLowerCase());
        }
        if (cachedDisease == null) { // case 3 from above, disease or group name from Mondo
            disease = createDiseaseObject(oboTerm);
            this.diseaseNameToDiseaseMap.put(oboTerm.getName().toLowerCase(), disease);
        } else {
            disease = cachedDisease; // case 1
            if (!cachedDisease.getName().equalsIgnoreCase(oboTerm.getName())
                    && !this.diseaseNameToDiseaseMap.containsKey(oboTerm.getName().toLowerCase())) { // case 2 in above
                Synonym synonym = createSynonymObject(disease, oboTerm.getName());
                disease.addSynonym(synonym);
                // put this synonym in the map
                this.diseaseNameToDiseaseMap.put(synonym.getName().toLowerCase(), disease);
            }
        }
        return disease;
    }

    private void loadCache() throws FileNotFoundException {
        loadDiseaseNameDiseaseMapFromDisease();
        loadDiseaseNameDiseaseMapFromSynonym();
        loadDiseaseNameDiseaseMapFromCrossRef();
    }

    private void setCacheInStepContext() {
        ExecutionContext stepContext = this.stepExecution.getExecutionContext();
        stepContext.put(Constants.DISEASE_NAME_OR_OMIM_DISEASE_MAP, this.diseaseNameToDiseaseMap);
        stepContext.put(Constants.MONDO_OBO_TERMS_LIST, this.mondoOboTerms);
    }

    private void loadDiseaseNameDiseaseMapFromDisease() throws FileNotFoundException {
        List<Disease> allDiseases = this.diseaseDAO.findAll();
        for (Disease d : allDiseases) {
            this.diseaseNameToDiseaseMap.put(d.getName().toLowerCase(), d);
        }
    }

    private void loadDiseaseNameDiseaseMapFromSynonym() {
        List<Synonym> allSyns = this.synonymDAO.findAll();
        for (Synonym s : allSyns) {
            if (!this.diseaseNameToDiseaseMap.containsKey(s.getName().toLowerCase())) {
                this.diseaseNameToDiseaseMap.put(s.getName().toLowerCase(), s.getDisease());
            }
        }
    }

    private void loadDiseaseNameDiseaseMapFromCrossRef() {
        List<CrossRef> xrefs = this.crossRefDAO.findAllByRefType("MIM"); //TODO
        for (CrossRef xref : xrefs) {
            String omim = "OMIM:" + xref.getRefId();// construct OMIM:<omimid> //TODO
            if (!this.diseaseNameToDiseaseMap.containsKey(omim)) {
                this.diseaseNameToDiseaseMap.put(omim.toLowerCase(), xref.getDisease());
            }
        }
    }

    private Disease createDiseaseObject(OBOTerm mondoDisease) { // not updating the synonym and xref intentionally.
        // This is being created just to create disease hierarchy
        Disease.DiseaseBuilder builder = Disease.builder();
        builder.diseaseId(mondoDisease.getId());
        builder.name(mondoDisease.getName());
        builder.desc(mondoDisease.getDefinition());
        builder.source(Constants.MONDO_STR);
        return builder.build();
    }

    private Synonym createSynonymObject(Disease disease, String mondoName) {
        Synonym.SynonymBuilder bldr = Synonym.builder();
        bldr.name(mondoName).source(Constants.MONDO_STR);
        return bldr.build();
    }

    private String getOMIMId(OBOTerm mondoTerm) {
        List<String> xrefs = mondoTerm.getXrefs();
        for (String xref : xrefs) {
            if (xref.startsWith("OMIM:")) {//TODO
                return xref;
            }
        }
        return null;
    }
}