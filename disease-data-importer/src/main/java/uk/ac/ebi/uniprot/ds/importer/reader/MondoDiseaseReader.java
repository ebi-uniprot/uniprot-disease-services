package uk.ac.ebi.uniprot.ds.importer.reader;

import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import uk.ac.ebi.uniprot.ds.common.dao.CrossRefDAO;
import uk.ac.ebi.uniprot.ds.common.dao.DiseaseDAO;
import uk.ac.ebi.uniprot.ds.common.dao.SynonymDAO;
import uk.ac.ebi.uniprot.ds.common.model.CrossRef;
import uk.ac.ebi.uniprot.ds.common.model.Disease;
import uk.ac.ebi.uniprot.ds.common.model.Synonym;
import uk.ac.ebi.uniprot.ds.importer.reader.diseaseontology.DiseaseOntologyReader;
import uk.ac.ebi.uniprot.ds.importer.reader.diseaseontology.OBOTerm;
import uk.ac.ebi.uniprot.ds.importer.util.Constants;

import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class MondoDiseaseReader implements ItemReader<Disease> {
    @Autowired
    private DiseaseDAO diseaseDAO;
    @Autowired
    private SynonymDAO synonymDAO;
    @Autowired
    private CrossRefDAO crossRefDAO;
    private Map<String, Disease> diseaseNameToDiseaseMap; // TODO pass it around
    private Iterator<OBOTerm> iterator;
    private List<OBOTerm> mondoDiseases; // TODO pass this around
    private String file;
    private StepExecution stepExecution;

    public MondoDiseaseReader(String file) throws FileNotFoundException {
        this.diseaseNameToDiseaseMap = new HashMap<>();
        this.file = file;
    }

    @BeforeStep
    // set the stepExecution to pass data from this step to another step. See above executionContext.put() call
    public void init(final StepExecution stepExecution) throws FileNotFoundException {
        this.stepExecution = stepExecution;
        this.mondoDiseases = new DiseaseOntologyReader(this.file).read();
        this.iterator = this.mondoDiseases.iterator();
        loadCache();
        setCacheInStepContext();
    }

    @Override
    public Disease read() throws FileNotFoundException {
        //FIXME better way
        Disease disease = null;
        // for each Mondo OBOTerm,
        //case 1 if mondo disease(oboterm) is there in humdisease and name matches then do nothing
        //case 2 if mondo disease(oboterm) is there in humdisease and name doesn't match then update synonym (if not already there) and cache
        //case 3 if mondo disease(oboterm) is not there in humdisease then create a new disease and add in cache
        if (this.iterator.hasNext()) {
            OBOTerm mondoDisease = this.iterator.next();
            // try to get the humdisease by disease name or omim id
            Disease cachedDisease = this.diseaseNameToDiseaseMap.get(mondoDisease.getName().toLowerCase());
            // if cache doesn't have disease name, try to find by omim id
            String mondoOmim = getOMIMId(mondoDisease);
            if (cachedDisease == null && mondoOmim != null) {
                cachedDisease = this.diseaseNameToDiseaseMap.get(mondoOmim.toLowerCase());
            }
            if (cachedDisease == null) { // case 3 from above, disease or group name from Mondo
                disease = createDiseaseObject(mondoDisease);
                this.diseaseNameToDiseaseMap.put(mondoDisease.getName().toLowerCase(), disease);
            } else {
                disease = cachedDisease; // case 1
                if (!cachedDisease.getName().equalsIgnoreCase(mondoDisease.getName())
                        && !this.diseaseNameToDiseaseMap.containsKey(mondoDisease.getName().toLowerCase())) { // case 2 in above
                    Synonym synonym = createSynonymObject(disease, mondoDisease.getName());
                    disease.addSynonym(synonym);
                    // put this synonym in the map
                    this.diseaseNameToDiseaseMap.put(synonym.getName().toLowerCase(), disease);
                }
            }
        }
        return disease;
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
            if (xref.startsWith("OMIM:")) {
                return xref;
            }
        }
        return null;
    }

    private void loadCache() throws FileNotFoundException {
        loadDiseaseNameDiseaseMapFromDisease();
        loadDiseaseNameDiseaseMapFromSynonym();
        loadDiseaseNameDiseaseMapFromCrossRef();
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
        List<CrossRef> xrefs = this.crossRefDAO.findAllByRefType("MIM");
        for (CrossRef xref : xrefs) {
            String omim = "OMIM:" + xref.getRefId();// construct OMIM:<omimid>
            if (!this.diseaseNameToDiseaseMap.containsKey(omim)) {
                this.diseaseNameToDiseaseMap.put(omim.toLowerCase(), xref.getDisease());
            }
        }
    }

    private void setCacheInStepContext() {
        ExecutionContext stepContext = this.stepExecution.getExecutionContext();
        stepContext.put("diseasemap", this.diseaseNameToDiseaseMap);
        stepContext.put("oboterms", this.mondoDiseases);
    }
}
