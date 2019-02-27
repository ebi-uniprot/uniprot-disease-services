package uk.ac.ebi.uniprot.ds.importer.processor;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import uk.ac.ebi.uniprot.ds.common.dao.CrossRefDAO;
import uk.ac.ebi.uniprot.ds.common.model.CrossRef;
import uk.ac.ebi.uniprot.ds.common.model.Disease;
import uk.ac.ebi.uniprot.ds.importer.reader.diseaseontology.AdjacencyList;
import uk.ac.ebi.uniprot.ds.importer.reader.diseaseontology.Node;
import uk.ac.ebi.uniprot.ds.importer.reader.diseaseontology.OBOTerm;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DiseaseOntologyProcessor implements ItemProcessor<List<OBOTerm>, List<Disease>> {
    @Autowired
    private CrossRefDAO crossRefDAO;

    @Override
    public List<Disease> process(List<OBOTerm> oboTerms) throws Exception {
        List<Disease> diseaseList = new ArrayList<>();

        Map<String, Node> adjList = new AdjacencyList().buildAdjacencyList(oboTerms);

        for (Map.Entry<String, Node> entry : adjList.entrySet()) {
            Node node = entry.getValue();
            List<CrossRef> crossRef = getCrossRefs(node);
            if (crossRef != null && !crossRef.isEmpty()) {
                Disease disease = crossRef.get(0).getDisease();
                List<Disease> children = getChildren(node.getChildren());
                if(!children.isEmpty()) {
                    disease.setChildren(children);
                    diseaseList.add(disease);
                }
            }
        }

        return diseaseList;
    }

    private List<Disease> getChildren(List<Node> nodes) {
        List<Disease> children = new ArrayList<>();
        for (Node node : nodes) {
            List<CrossRef> crossRef = getCrossRefs(node);
            Disease disease = null;
            if (crossRef != null && !crossRef.isEmpty()) {
                disease = crossRef.get(0).getDisease();
                children.add(disease);
            }
        }
        return children;
    }

    private List<CrossRef> getCrossRefs(Node node) {
        List<String> xrefs = node.getTerm().getXrefs();
        String omim = getOMIM(xrefs);
        if(omim != null) {
            List<CrossRef> crossRefs = this.crossRefDAO.findAllByRefTypeAndRefId("MIM", omim);
            if(crossRefs.size() > 1){
                return null;
            }
            return crossRefs;
        }
        return null;
    }

    private static String getOMIM(List<String> xrefs) {
        if (xrefs == null || xrefs.isEmpty()) {
            return null;
        }
        for (String xref : xrefs) {
            if (xref.startsWith("OMIM")) {
                return xref.split(":")[1];
            }
        }

        return null;
    }
}
