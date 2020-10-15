package uk.ac.ebi.uniprot.ds.importer.reader.graph;

import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toMap;

public class AdjacencyList {
    /*
      create an adjacency list
      [oboTermId -> oboTerm Object with its children(list of oboterms)]
      Note - A child can have more than one parent
     */
    public Map<String, Node>  buildAdjacencyList(List<OBOTerm> oboTerms) {
        // create a map with key as obo term id and term as object wrapped in Node
        Map<String, Node> termIdNodeMap = oboTerms.stream().collect(toMap(OBOTerm::getId, term -> new Node(term)));

        // add each value as child of another node(s)(parent(s))
        termIdNodeMap.values().forEach(node ->
                {
                    List<String> parentIds = node.getTerm().getIsAs();
                    // FIXME Hard Code - add Alzheimer Disease 9(MONDO:0012153) as a child of Alzheimer disease(id: MONDO:0004975)
                    if("MONDO:0012153".equalsIgnoreCase(node.getTerm().getId())){
                        parentIds.add("MONDO:0004975");
                    }
                    if(parentIds != null && !parentIds.isEmpty()){
                        // update all the parents with this node as a child
                        for(String parentId : parentIds){
                            if (termIdNodeMap.containsKey(parentId)) {
                                Node parent = termIdNodeMap.get(parentId);
                                parent.getChildren().add(node);
                            }
                        }
                    }
                }
        );
        return termIdNodeMap;
    }
}
