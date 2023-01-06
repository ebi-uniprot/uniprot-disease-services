package uk.ac.ebi.uniprot.ds.importer.processor;

import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemProcessor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import lombok.extern.slf4j.Slf4j;
import uk.ac.ebi.uniprot.ds.common.common.SourceType;
import uk.ac.ebi.uniprot.ds.common.dao.CrossRefDAO;
import uk.ac.ebi.uniprot.ds.common.dao.DiseaseDAO;
import uk.ac.ebi.uniprot.ds.common.dao.SynonymDAO;
import uk.ac.ebi.uniprot.ds.common.model.CrossRef;
import uk.ac.ebi.uniprot.ds.common.model.Disease;
import uk.ac.ebi.uniprot.ds.common.model.Synonym;
import uk.ac.ebi.uniprot.ds.importer.reader.graph.OBOTerm;
import uk.ac.ebi.uniprot.ds.importer.util.Constants;

@Slf4j
public class MondoTermToDiseaseConverter implements ItemProcessor<OBOTerm, Disease> {
    private DiseaseDAO diseaseDAO;
    private SynonymDAO synonymDAO;
    private CrossRefDAO crossRefDAO;
    // key can be name, synonym(mondo name), omim, efo id, mondo id
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

    public MondoTermToDiseaseConverter(DiseaseDAO diseaseDAO, SynonymDAO synonymDAO, CrossRefDAO crossRefDAO){
        this.diseaseDAO = diseaseDAO;
        this.synonymDAO = synonymDAO;
        this.crossRefDAO = crossRefDAO;
    }

    @BeforeStep //initialisation like load cache and set cache in step context to be used in next step
    public void init(final StepExecution stepExecution) {
        this.stepExecution = stepExecution;
        initialize();
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
        //case 3 if mondo disease(oboterm) is not there in humdisease and no ambiguity about mapping with hum disease
        // then create a new disease and add in cache

        // try to get the humdisease by disease name
        String mondoName = oboTerm.getName().toLowerCase();
        Disease cachedDisease = this.diseaseNameToDiseaseMap.get(mondoName);

        // Hard Coded - FIXME when Mondo and Hum disease have strong mapping
        if("MONDO:0007088".equalsIgnoreCase(oboTerm.getId())){
            cachedDisease = this.diseaseDAO.findDiseaseByNameIgnoreCase("Alzheimer disease 1").get();
        } else if("MONDO:0012153".equalsIgnoreCase(oboTerm.getId())){
            cachedDisease = this.diseaseDAO.findDiseaseByNameIgnoreCase("Alzheimer disease 9").get();
        } // Hard code ends

        // if cache doesn't have disease name, try to find by omim id or synonym
        String mondoOmim = getCrossRefByType(oboTerm, Constants.OMIM_COLON_STR);
        if (Objects.isNull(cachedDisease)) {
           cachedDisease = getByOmimOrSynonym(mondoName, mondoOmim);
        }
        // case 3 from above, disease or group name from Mondo and no ambiguity
        if (cachedDisease == null && !this.ambiguousOboTerms.contains(mondoName)) {
            disease = createDiseaseObject(oboTerm);
            updateDiseaseNameToDiseaseMap(oboTerm, disease);
        } else if(Objects.nonNull(cachedDisease)){
            disease = cachedDisease; // case 1
            if (!cachedDisease.getName().equalsIgnoreCase(oboTerm.getName())
                    && !this.diseaseNameToDiseaseMap.containsKey(oboTerm.getName().toLowerCase())) { // case 2 in above
                Synonym synonym = createSynonymObject(oboTerm.getName());
                disease.addSynonym(synonym);
                // put this synonym in the map
                this.diseaseNameToDiseaseMap.put(oboTerm.getName().toLowerCase(), disease);
            }
            //update cache for look-up during drug to disease mapping
            this.diseaseNameToDiseaseMap.put(oboTerm.getId().trim(), disease);// mondo id
            String mondoEFO = getCrossRefByType(oboTerm, Constants.EFO_COLON_STR);
            if (Objects.nonNull(mondoEFO)) {// [key --> value] = [EFO:1001434 --> disease]
                this.diseaseNameToDiseaseMap.put(mondoEFO, disease);// efo id if there
            }
        }
        return disease;
    }

    private void initialize(){
        this.mondoOboTerms = new ArrayList<>();
        this.diseaseNameToDiseaseMap = new HashMap<>();
        this.synonymToDiseasesMap = new HashMap<>();
        this.omimToDiseasesMap = new HashMap<>();
        this.ambiguousOboTerms = new HashSet<>();
        this.ambiguousOboTerms.add("alopecia areata 1");
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
            // Hard Code to skip to avoid loop.
            // As per hum disease via omim, Frontotemporal dementia and semantic dementia are same disease
            // but as per mondo semantic dementia is grand child of Frontotemporal dementia, hence loop is being created
            // Frontotemporal dementia is parent of behvaioural variant  which is parent of semantic dementia.
            // but semantic dementia is same as Frontotemporal dementia, hence a loop.
            if(!"Frontotemporal dementia".equalsIgnoreCase(xref.getDisease().getName())) {
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
    }

    private Disease createDiseaseObject(OBOTerm mondoDisease) { // not updating the synonym and xref intentionally.
        // This is being created just to create disease hierarchy
        Disease.DiseaseBuilder builder = Disease.builder();
        String diseaseId = computeDiseaseId(mondoDisease.getId());
        builder.diseaseId(diseaseId);
        builder.name(mondoDisease.getName());
        builder.desc(mondoDisease.getDefinition());
        builder.source(SourceType.MONDO.name());
        return builder.build();
    }

    private Synonym createSynonymObject(String mondoName) {
        Synonym.SynonymBuilder bldr = Synonym.builder();
        bldr.name(mondoName).source(SourceType.MONDO.name());
        return bldr.build();
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

    private void updateDiseaseNameToDiseaseMap(OBOTerm oboTerm, Disease disease) {
        this.diseaseNameToDiseaseMap.put(oboTerm.getName().trim().toLowerCase(), disease);// mondo name
        this.diseaseNameToDiseaseMap.put(oboTerm.getId().trim(), disease);// mondo id
        String mondoOmim = getCrossRefByType(oboTerm, Constants.OMIM_COLON_STR);
        if(Objects.nonNull(mondoOmim)) {
            this.diseaseNameToDiseaseMap.put(mondoOmim, disease);// omim id if there
        }
        String mondoEFO = getCrossRefByType(oboTerm, Constants.EFO_COLON_STR);
        if(Objects.nonNull(mondoEFO)) {
            this.diseaseNameToDiseaseMap.put(mondoEFO, disease);// efo id if there
        }
    }

    private String getCrossRefByType(OBOTerm mondoTerm, String xrefType) {
        List<String> xrefs = mondoTerm.getXrefs();
        for (String xref : xrefs) {
            if (xref.startsWith(xrefType)) {
                return xref;
            }
        }
        return null;
    }

    /**
     * Construct disease id from mondo id
     * MONDO:0000949 ==> DI-M0000949
     */
    private String computeDiseaseId(String mondoId) {
        String[] idParts = mondoId.split(":");
        StringBuilder builder = new StringBuilder("DI-M");
        builder.append(idParts[1]);
        return builder.toString();
    }
}