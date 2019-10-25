package uk.ac.ebi.uniprot.ds.importer.reader.diseaseontology;

import java.util.*;

public class AdjacencyList {

    /*
      create an adjacency list
      [oboTermId -> oboTerm Object with children(list of oboterms)
     */
    public Map<String, Node>  buildAdjacencyList(List<OBOTerm> oboTerms) {
        Map<String, Node> termIdNodeMap = new HashMap<>();
        // create a map with key as obo term id and term as object
        oboTerms.forEach(term -> termIdNodeMap.put(term.getId(), new Node(term)));

        // add each value as child of another node(parent)
        termIdNodeMap.values().forEach(node ->
                {
                    List<String> parentIds = node.getTerm().getIsAs();
                    if(parentIds != null && !parentIds.isEmpty()){
                        // update all the parents with this node as a child
                        for(String parenId : parentIds){
                            if(parenId.contains("MONDO:0004975")){
                                System.out.println();//TODO
                            }
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
