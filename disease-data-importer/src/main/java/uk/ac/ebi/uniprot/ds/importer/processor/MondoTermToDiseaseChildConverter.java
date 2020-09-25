package uk.ac.ebi.uniprot.ds.importer.processor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.item.ItemProcessor;
import uk.ac.ebi.uniprot.ds.common.model.Disease;
import uk.ac.ebi.uniprot.ds.importer.model.DiseaseRelationDTO;
import uk.ac.ebi.uniprot.ds.importer.reader.diseaseontology.AdjacencyList;
import uk.ac.ebi.uniprot.ds.importer.reader.diseaseontology.Node;
import uk.ac.ebi.uniprot.ds.importer.reader.diseaseontology.OBOTerm;
import uk.ac.ebi.uniprot.ds.importer.util.Constants;

import java.util.*;

@Slf4j
public class MondoTermToDiseaseChildConverter implements ItemProcessor<OBOTerm, List<DiseaseRelationDTO>> {
    private Map<String, Disease> diseaseNameToDiseaseMap;
    private Map<String, Node> adjList;
    private Set<DiseaseRelationDTO> relations;// cache to avoid duplicate insert

    @Override
    public List<DiseaseRelationDTO> process(OBOTerm oboTerm) {
        // get the parent node by mondo id
        Node diseaseNode = this.adjList.get(oboTerm.getId());
        String monoDiseaseName = diseaseNode.getTerm().getName().trim();
        // get the Disease object for parent node aka obo term from cache
        Disease disease = this.diseaseNameToDiseaseMap.get(monoDiseaseName.toLowerCase());
        List<Long> path = new ArrayList<>();
        // ignore the whole path if it has cycle. We can improve it to only ignore those disease within cycle. FIXME
        // e.g. with cycle
        // 1 --> 2 --> 3 --> 4 --> 5 --> 3, we are ignoring this whole tree.
        // ideally we should skip from 3 --> 4 --> 5 --> 3 TODO
        if(Objects.nonNull(disease) && !isCycleDetected(diseaseNode, path)) {
            // get children nodes of term aka parent node
            List<Node> childDiseaseNodes = diseaseNode.getChildren();
            List<DiseaseRelationDTO> parentChildren = getDiseaseRelationDTO(disease, childDiseaseNodes);
            return parentChildren;
        } else {
                log.warn("Cycle Detected! Ignoring the parent child relationships..");
                log.warn("Path {}", path);
            return new ArrayList<>();
        }
    }

    @BeforeStep
    public void init(final StepExecution stepExecution) {// init and get the cached data from previous step
        this.relations = new HashSet<>();

        this.diseaseNameToDiseaseMap = (Map<String, Disease>) stepExecution.getJobExecution()
                .getExecutionContext().get(Constants.DISEASE_NAME_OR_OMIM_DISEASE_MAP);

        List<OBOTerm> oboTerms = (List<OBOTerm>) stepExecution.getJobExecution().getExecutionContext().
                get(Constants.MONDO_OBO_TERMS_LIST);
        // create an adjacency list where each term id is the key and node is obo term with its children oboterms
        this.adjList = new AdjacencyList().buildAdjacencyList(oboTerms);
    }

    private List<DiseaseRelationDTO> getDiseaseRelationDTO(Disease parentDisease, List<Node> childDiseaseNodes) {
        List<DiseaseRelationDTO> parentChildren = new ArrayList<>();
        for(Node child : childDiseaseNodes){
            String childName = child.getTerm().getName();
            Disease childDisease = this.diseaseNameToDiseaseMap.get(childName.toLowerCase());
            if(Objects.nonNull(childDisease)) {
                DiseaseRelationDTO parentChild = new DiseaseRelationDTO(parentDisease.getId(), childDisease.getId());
                // add if it is not already saved and parent child are not same because of bad data
                if (!this.relations.contains(parentChild) &&
                        !parentDisease.getId().equals(childDisease.getId())) {

                    parentChildren.add(parentChild);
                    this.relations.add(parentChild);

                }
            }
        }
        return parentChildren;
    }

    private boolean isCycleDetected(Node current, List<Long> path){
        Set<Long> visitedId = new HashSet<>();
        boolean isCycle = dfs(current, visitedId, path);
        return isCycle;
    }

    private boolean dfs(Node current, Set<Long> visitedId, List<Long> path) {
        Disease disease = this.diseaseNameToDiseaseMap.get(current.getTerm().getName().toLowerCase());
        if(disease != null) {
            path.add(disease.getId());
            if(visitedId.contains(disease.getId())){
                return true;
            }
            visitedId.add(disease.getId());
            for(Node child : current.getChildren()){
                boolean isCycle = dfs(child, visitedId, path);
                if(isCycle){
                    return true;
                }
            }
        }
        return false;
    }
}