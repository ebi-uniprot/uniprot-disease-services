package uk.ac.ebi.uniprot.ds.importer.reader.diseaseontology;

import java.util.*;

public class AdjacencyList {

    public Map<String, Node>  buildAdjacencyList(List<OBOTerm> oboTerms) {
        Map<String, Node> termIdNodeMap = new HashMap<>();
        oboTerms.forEach(term -> termIdNodeMap.put(term.getId(), new Node(term)));

        termIdNodeMap.values().forEach(node ->
                {
                    List<String> parentIds = node.getTerm().getIsAs();
                    if(parentIds != null && !parentIds.isEmpty()){
                        // update all the parents with this node as a child
                        for(String parenId : parentIds){
                            if (termIdNodeMap.containsKey(parenId)) {
                                Node parent = termIdNodeMap.get(parenId);
                                parent.getChildren().add(node);
                            }
                        }
                    }
                }
        );
        return termIdNodeMap;
    }
}
