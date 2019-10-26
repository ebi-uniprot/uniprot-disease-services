package uk.ac.ebi.uniprot.ds.importer.processor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import uk.ac.ebi.uniprot.ds.common.dao.DiseaseDAO;
import uk.ac.ebi.uniprot.ds.common.dao.SynonymDAO;
import uk.ac.ebi.uniprot.ds.common.model.Disease;
import uk.ac.ebi.uniprot.ds.common.model.Synonym;
import uk.ac.ebi.uniprot.ds.importer.reader.diseaseontology.AdjacencyList;
import uk.ac.ebi.uniprot.ds.importer.reader.diseaseontology.Node;
import uk.ac.ebi.uniprot.ds.importer.reader.diseaseontology.OBOTerm;
import uk.ac.ebi.uniprot.ds.importer.util.Constants;

import java.util.*;

@Slf4j
public class MondoTermToDiseaseChildConverter implements ItemProcessor<OBOTerm, Disease> {
    private Map<String, Disease> diseaseNameToDiseaseMap;
    private Map<String, Node> adjList;

    @Override
    public Disease process(OBOTerm oboTerm) {
        // get the parent node by mondo id
        Node diseaseNode = this.adjList.get(oboTerm.getId());
        String monoDiseaseName = diseaseNode.getTerm().getName();
        // get the Disease object for parent node aka obo term from cache
        Disease disease = this.diseaseNameToDiseaseMap.get(monoDiseaseName.toLowerCase());
        List<Disease> existingChildren = disease.getChildren();
        if("Alzheimer disease".equalsIgnoreCase(monoDiseaseName)){
            System.out.println();
        }
        if(existingChildren.isEmpty()){ // hack FIXME fix the data?
            // get children nodes of term aka parent node
            List<Node> childDiseaseNodes = diseaseNode.getChildren();
            // get disease object for each child nodes
            Set<Disease> childDiseases = getChildDiseases(disease, childDiseaseNodes);
            disease.setChildren(new ArrayList<>(childDiseases));
        } // else it means this disease has children because the name in humdisease and name in mondo are same but point two different diseases

        return disease;
    }

    @BeforeStep
    public void init(final StepExecution stepExecution) {// get the cached data from previous step
        this.diseaseNameToDiseaseMap = (Map<String, Disease>) stepExecution.getJobExecution().getExecutionContext().get(Constants.DISEASE_NAME_OR_OMIM_DISEASE_MAP);
        List<OBOTerm> oboTerms = (List<OBOTerm>) stepExecution.getJobExecution().getExecutionContext().get(Constants.MONDO_OBO_TERMS_LIST);
        // create an adjacency list where each term id is the key and node is obo term with its children oboterms
        this.adjList = new AdjacencyList().buildAdjacencyList(oboTerms);
    }

    private Set<Disease> getChildDiseases(Disease parent, List<Node> childNodes) {
        Set<Disease> childDiseases = new HashSet<>();
        for (Node childNode : childNodes) {
            String monoDiseaseName = childNode.getTerm().getName();
            Disease childDisease = this.diseaseNameToDiseaseMap.get(monoDiseaseName.toLowerCase());
            if (childDisease == null) {
                //log.warn("Unable to find mapping for child term {} in disease service", doName);
            } else if(parent.getId() != childDisease.getId()){ // avoid loop because of bad data
                childDiseases.add(childDisease);
            }
        }
        return childDiseases;
    }
}