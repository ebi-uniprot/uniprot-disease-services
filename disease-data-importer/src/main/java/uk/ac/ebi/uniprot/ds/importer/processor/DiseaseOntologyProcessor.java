package uk.ac.ebi.uniprot.ds.importer.processor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import uk.ac.ebi.uniprot.ds.common.dao.DiseaseDAO;
import uk.ac.ebi.uniprot.ds.common.dao.SynonymDAO;
import uk.ac.ebi.uniprot.ds.common.model.Disease;
import uk.ac.ebi.uniprot.ds.common.model.Synonym;
import uk.ac.ebi.uniprot.ds.importer.reader.diseaseontology.AdjacencyList;
import uk.ac.ebi.uniprot.ds.importer.reader.diseaseontology.Node;
import uk.ac.ebi.uniprot.ds.importer.reader.diseaseontology.OBOTerm;

import java.util.*;

@Slf4j
public class DiseaseOntologyProcessor implements ItemProcessor<List<OBOTerm>, List<Disease>> {
    @Autowired
    private DiseaseDAO diseaseDAO;
    @Autowired
    private SynonymDAO synonymDAO;
    private Map<String, Disease> diseaseNameToDiseaseMap;

    @Override
    public List<Disease> process(List<OBOTerm> oboTerms) {
        // load disease_id to diseases cache
        loadCache();

        Set<Disease> diseaseList = new HashSet<>();

        // create an adjacency list where each term id is the key and node is obo term with its children oboterms
        Map<String, Node> adjList = new AdjacencyList().buildAdjacencyList(oboTerms);

        for (Node parentNode : adjList.values()) {
            // get the Disease object for parent node aka obo term from cache
            String doName = parentNode.getTerm().getName();
            Disease parentDisease = this.diseaseNameToDiseaseMap.get(doName);

            if (parentDisease == null) { // ignore the parent node and its children if there is no mapping in HumDisease or disease service
                //log.warn("Unable to find mapping for parent term {} in disease service", doName);
            } else {
                // get children nodes of term aka parent node
                List<Node> childNodes = parentNode.getChildren();
                // get disease object for each child nodes
                Set<Disease> childDiseases = getChildDiseases(childNodes);
                parentDisease.setChildren(new ArrayList<>(childDiseases));
                diseaseList.add(parentDisease);
            }
        }

        return new ArrayList<>(diseaseList);
    }

    private Set<Disease> getChildDiseases(List<Node> childNodes) {
        Set<Disease> childDiseases = new HashSet<>();
        for (Node childNode : childNodes) {
            String doName = childNode.getTerm().getName();
            Disease childDisease = this.diseaseNameToDiseaseMap.get(doName);
            if(childDisease == null){
                //log.warn("Unable to find mapping for child term {} in disease service", doName);
            } else {
                childDiseases.add(childDisease);
            }
        }
        return childDiseases;
    }

    private void loadCache() {
        this.diseaseNameToDiseaseMap = new HashMap<>();
        loadDiseaseIdDiseaseMapFromDisease();
        loadDiseaseIdDiseaseMapFromSynonym();
    }

    private void loadDiseaseIdDiseaseMapFromSynonym() {
        List<Synonym> allSyns = this.synonymDAO.findAll();
        for (Synonym s : allSyns) {
            if (!this.diseaseNameToDiseaseMap.containsKey(s.getName().trim().toLowerCase())) {
                this.diseaseNameToDiseaseMap.put(s.getName().trim().toLowerCase(), s.getDisease());
            }
        }
    }

    private void loadDiseaseIdDiseaseMapFromDisease() {
        List<Disease> allDiseases = this.diseaseDAO.findAll();
        for (Disease d : allDiseases) {
            this.diseaseNameToDiseaseMap.put(d.getName().trim().toLowerCase(), d);
        }
    }
}