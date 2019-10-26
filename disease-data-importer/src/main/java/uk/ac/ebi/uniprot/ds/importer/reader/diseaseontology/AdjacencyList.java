package uk.ac.ebi.uniprot.ds.importer.reader.diseaseontology;

import org.apache.commons.math3.util.Pair;

import java.util.*;
import java.util.stream.Collectors;

public class AdjacencyList {

    /*
      create an adjacency list
      [oboTermId -> oboTerm Object with children(list of oboterms)
      a child can have more than one parent
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
                            if (termIdNodeMap.containsKey(parenId)) {
                                Node parent = termIdNodeMap.get(parenId);
                                parent.getChildren().add(node);
                            }
                        }
                    }
                }
        );
        // print the map
//        printIt(termIdNodeMap);
        return termIdNodeMap;
    }

    private void printIt(Map<String, Node> termIdNodeMap) {
        for(Map.Entry<String, Node> entry : termIdNodeMap.entrySet()){
            Pair parent = Pair.create(entry.getKey(), entry.getValue().getTerm().getName());
            List<Pair> children = entry.getValue().getChildren().stream()
                    .map(c -> c.getTerm())
                    .map(t -> Pair.create(t.getId(), t.getName()))
                    .collect(Collectors.toList());

            System.out.println(parent + "-->" + children);
        }
    }
}
