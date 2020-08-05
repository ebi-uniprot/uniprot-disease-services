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
    // one synonym can belong to more than 1 disease
    private Map<String, Set<Disease>> synonymToDiseasesMap; // cache to quick look up for diseases by synonym
    // one omim can belong to more than 1 disease
    private Map<String, Set<Disease>> omimToDiseasesMap; // cache to quick look up for diseases by omim id
    private List<OBOTerm> mondoOboTerms; // list of oboterms. will be used in next step to create parent-child relationship
    // Obo terms which have more than one corresponding disease in hum disease
    // either because the omim or synonym belongs to more than one disease
    private Set<String> ambiguousOboTerms;
    private StepExecution stepExecution;

    @BeforeStep //initialisation like load cache and set cache in step context to be used in next step
    public void init(final StepExecution stepExecution) throws FileNotFoundException {
        this.stepExecution = stepExecution;
        this.mondoOboTerms = new ArrayList<>();
        this.diseaseNameToDiseaseMap = new HashMap<>();
        this.synonymToDiseasesMap = new HashMap<>();
        this.omimToDiseasesMap = new HashMap<>();
        this.ambiguousOboTerms = new HashSet<>();
        loadCache();
        setCacheInStepContext();
    }

    @Override
    public Disease process(OBOTerm oboTerm) {
        this.mondoOboTerms.add(oboTerm); // add the term in list to be passed to next step.
        Disease disease = null;
        // for each Mondo OBOTerm,
        //case 1 if mondo disease(oboterm) is there in humdisease and name matches then do nothing
        //case 2 if mondo disease(oboterm) is there in humdisease and name doesn't match then update synonym
        // (if not already there) and cache
        //case 3 if mondo disease(oboterm) is not there in humdisease and no ambiguouity about mapping with hum disease
        // then create a new disease and add in cache

        // try to get the humdisease by disease name
        String mondoName = oboTerm.getName().toLowerCase();
        Disease cachedDisease = this.diseaseNameToDiseaseMap.get(mondoName);

        // if cache doesn't have disease name, try to find by omim id or synonym
        String mondoOmim = getOMIMId(oboTerm);
        if (Objects.isNull(cachedDisease)) {
           cachedDisease = getByOmimOrSynonym(mondoName, mondoOmim);
        }
        // case 3 from above, disease or group name from Mondo
        if (cachedDisease == null && !this.ambiguousOboTerms.contains(mondoName)) {
            disease = createDiseaseObject(oboTerm);
            this.diseaseNameToDiseaseMap.put(mondoName, disease);
        } else if(Objects.nonNull(cachedDisease)){
            disease = cachedDisease; // case 1
            if (!cachedDisease.getName().equalsIgnoreCase(oboTerm.getName())
                    && !this.diseaseNameToDiseaseMap.containsKey(oboTerm.getName().toLowerCase())) { // case 2 in above
                Synonym synonym = createSynonymObject(oboTerm.getName());
                disease.addSynonym(synonym);
                // put this synonym in the map
                this.diseaseNameToDiseaseMap.put(synonym.getName().toLowerCase(), disease);
            }
        }
        return disease;
    }

    private void loadCache() {
        loadDiseaseNameDiseaseMapFromDisease();
        loadDiseaseNameDiseaseMapFromSynonym();
        loadDiseaseNameDiseaseMapFromCrossRef();
    }

    private void setCacheInStepContext() {
        ExecutionContext stepContext = this.stepExecution.getExecutionContext();
        stepContext.put(Constants.DISEASE_NAME_OR_OMIM_DISEASE_MAP, this.diseaseNameToDiseaseMap);
        stepContext.put(Constants.MONDO_OBO_TERMS_LIST, this.mondoOboTerms);
    }

    private void loadDiseaseNameDiseaseMapFromDisease() {
        List<Disease> allDiseases = this.diseaseDAO.findAll();
        for (Disease d : allDiseases) {
            if(this.diseaseNameToDiseaseMap.containsKey(d.getName().toLowerCase())){
                throw new IllegalArgumentException("Disease name" + d.getName().toLowerCase() + "already exist for disease id"
                        + this.diseaseNameToDiseaseMap.get(d.getName().toLowerCase()).getId());
            }

            this.diseaseNameToDiseaseMap.put(d.getName().toLowerCase(), d);
        }
    }

    private void loadDiseaseNameDiseaseMapFromSynonym() {
        List<Synonym> allSyns = this.synonymDAO.findAll();
        for (Synonym s : allSyns) {
            if (!this.synonymToDiseasesMap.containsKey(s.getName().toLowerCase())) {
                Set<Disease> diseases = new HashSet<>();
                diseases.add(s.getDisease());
                this.synonymToDiseasesMap.put(s.getName().toLowerCase(), diseases);
            } else {
                Set<Disease> diseases = this.synonymToDiseasesMap.get(s.getName().toLowerCase());
                diseases.add(s.getDisease());
                log.warn("Synonym {} already exist", s.getName().toLowerCase());
            }
        }
    }

    private void loadDiseaseNameDiseaseMapFromCrossRef() {
        List<CrossRef> xrefs = this.crossRefDAO.findAllByRefType(Constants.MIM_STR);
        for (CrossRef xref : xrefs) {
            String omim = Constants.OMIM_COLON_STR + xref.getRefId();// construct OMIM:<omimid>
            if (!this.omimToDiseasesMap.containsKey(omim)) {
                Set<Disease> diseases = new HashSet<>();
                diseases.add(xref.getDisease());
                this.omimToDiseasesMap.put(omim, diseases);
            } else {
                Set<Disease> diseases = this.omimToDiseasesMap.get(omim);
                diseases.add(xref.getDisease());
                log.warn("The Omim id {} is already associated with disease", omim);
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

    private Synonym createSynonymObject(String mondoName) {
        Synonym.SynonymBuilder bldr = Synonym.builder();
        bldr.name(mondoName).source(Constants.MONDO_STR);
        return bldr.build();
    }

    private String getOMIMId(OBOTerm mondoTerm) {
        List<String> xrefs = mondoTerm.getXrefs();
        for (String xref : xrefs) {
            if (xref.startsWith(Constants.OMIM_COLON_STR)) {
                return xref;
            }
        }
        return null;
    }

    private Disease getByOmimOrSynonym(String mondoName, String mondoOmim) {
        Disease cachedDisease;
        resolveOboTerm(mondoName, mondoOmim);
        if (Objects.nonNull(mondoOmim)) {
            cachedDisease = this.diseaseNameToDiseaseMap.get(mondoOmim);
        } else {
            cachedDisease = this.diseaseNameToDiseaseMap.get(mondoName);
        }
        return cachedDisease;
    }

    private void  resolveOboTerm(String mondoName, String mondoOmim){
        resolveOboTermByOmimId(mondoName, mondoOmim);
        resolveOboTermBySynonym(mondoName);
    }

    private void resolveOboTermByOmimId(String mondoName, String mondoOmim){
        Set<Disease> diseases = this.omimToDiseasesMap.get(mondoOmim);
        if(Objects.nonNull(diseases)){
            if(diseases.size() > 1){
                this.ambiguousOboTerms.add(mondoName);// add to the ambiguous list to ignore
                log.warn("Omim {} has more than one disease associated with it. Ignoring it...", mondoOmim);
            } else if(diseases.size() == 1){
                Disease cachedDisease = new ArrayList<>(diseases).get(0);
                this.diseaseNameToDiseaseMap.put(mondoOmim, cachedDisease);
            }
        }
    }

    private void resolveOboTermBySynonym(String mondoName){
        Set<Disease> diseases = this.synonymToDiseasesMap.get(mondoName);
        if(Objects.nonNull(diseases)){
            if(diseases.size() > 1){
                log.warn("Synonym {} has more than one disease associated with it. Ignoring it...", mondoName);
                this.ambiguousOboTerms.add(mondoName);// add to the ambiguous list to ignore
            } else if(diseases.size() == 1){
                Disease cachedDisease = new ArrayList<>(diseases).get(0);
                this.diseaseNameToDiseaseMap.put(mondoName, cachedDisease);
            }
        }
    }
}