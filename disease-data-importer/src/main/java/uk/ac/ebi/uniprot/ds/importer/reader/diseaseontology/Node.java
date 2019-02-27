package uk.ac.ebi.uniprot.ds.importer.reader.diseaseontology;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class Node {
    private List<Node> children = new ArrayList<>();
    private OBOTerm term;
    public Node(OBOTerm term){
        this.term = term;
    }
}
