package uk.ac.ebi.uniprot.ds.importer.processor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import uk.ac.ebi.uniprot.ds.common.dao.CrossRefDAO;
import uk.ac.ebi.uniprot.ds.common.model.CrossRef;
import uk.ac.ebi.uniprot.ds.common.model.Disease;
import uk.ac.ebi.uniprot.ds.importer.reader.diseaseontology.AdjacencyList;
import uk.ac.ebi.uniprot.ds.importer.reader.diseaseontology.Node;
import uk.ac.ebi.uniprot.ds.importer.reader.diseaseontology.OBOTerm;

import java.util.*;

@Slf4j
public class DiseaseOntologyProcessor implements ItemProcessor<List<OBOTerm>, List<Disease>> {
    private static final String MIM_STR = "MIM";
    private static final String OMIM_STR = "OMIM";
    private static final String MeSH_STR = "MeSH";
    private static final String MESH_STR = "MESH";
    private static final String COLON = ":";
    @Autowired
    private CrossRefDAO crossRefDAO;
    private Map<String, List<Disease>> mimOrMeshIdDiseaseMap;

    @Override
    public List<Disease> process(List<OBOTerm> oboTerms) {
        // load refId to diseases cache
        loadCache();

        Set<Disease> diseaseList = new HashSet<>();

        // create an adjacency list where each term id is the key and node is obo term with its children oboterms
        Map<String, Node> adjList = new AdjacencyList().buildAdjacencyList(oboTerms);

        for (Node parentNode : adjList.values()) {
            // all the diseases which are known by given xrefs in parentNode term
            Set<Disease> diseasesByXRefs = getDiseasesByXRefs(parentNode.getTerm().getXrefs());
            if (diseasesByXRefs.size() > 1) {
                System.out.println();
            }

            if (diseasesByXRefs.isEmpty()) {
                log.warn("Unable to find mapping for parent term {} in disease service", parentNode.getTerm().getId());
            } else {
                // get children of term aka node
                List<Node> childNodes = parentNode.getChildren();

                // get disease object for each child nodes
                Set<Disease> childDiseases = getChildDiseases(childNodes);

                // add those children for each parent diseases
                for (Disease dXref : diseasesByXRefs) {
                    dXref.setChildren(new ArrayList<>(childDiseases));
                    diseaseList.add(dXref);
                }

            }
        }

        return new ArrayList<>(diseaseList);
    }

    private Set<Disease> getChildDiseases(List<Node> childNodes) {
        Set<Disease> childDiseases = new HashSet<>();
        for (Node childNode : childNodes) { // get disease object by xref of each node. flatten it
            Set<Disease> childDiseasesByXRefs = getDiseasesByXRefs(childNode.getTerm().getXrefs());
            if (childDiseasesByXRefs.size() > 1) {
                System.out.println();
            }
            if (childNode.getTerm().getXrefs() != null && !childNode.getTerm().getXrefs().isEmpty()
                    && childDiseasesByXRefs.isEmpty()) {
                log.warn("Unable to find mapping for child term {} in disease service", childNode.getTerm().getId());
            }

            childDiseases.addAll(childDiseasesByXRefs);
        }
        return childDiseases;
    }

    private void loadCache() {
        this.mimOrMeshIdDiseaseMap = getRefIdDiseaseMap(MIM_STR);
        this.mimOrMeshIdDiseaseMap.putAll(getRefIdDiseaseMap(MeSH_STR));
    }

    private Map<String, List<Disease>> getRefIdDiseaseMap(String refType) {
        Map<String, List<Disease>> refIdDiseaseMap = new HashMap<>();
        // get the reftype cross refs
        List<CrossRef> mimXrefs = this.crossRefDAO.findAllByRefType(refType);

        for (CrossRef mimRef : mimXrefs) {
            String mimId = mimRef.getRefId();
            if (refIdDiseaseMap.containsKey(mimId)) { // update the value list
                List<Disease> diseases = refIdDiseaseMap.get(mimId);
                diseases.add(mimRef.getDisease());
                refIdDiseaseMap.put(mimId, diseases);
            } else {
                List<Disease> diseases = new ArrayList<>();
                diseases.add(mimRef.getDisease());
                refIdDiseaseMap.put(mimId, diseases);
            }
        }
        return refIdDiseaseMap;
    }


    private Set<Disease> getDiseasesByXRefs(List<String> xrefs) {
        Set<Disease> diseaseSet = new HashSet<>();
        if (xrefs != null) {
            for (String xref : xrefs) {
                if (xref.startsWith(OMIM_STR) || xref.startsWith(MESH_STR)) {
                    String xrefId = xref.split(COLON)[1];
                    List<Disease> diseaseList = this.mimOrMeshIdDiseaseMap.get(xrefId);
                    if(diseaseList == null){
                        log.warn("Unable to find xref {} in disease service", xrefId);
                    } else {
                        diseaseSet.addAll(diseaseList);
                    }
                }
            }
        }

        return diseaseSet;
    }
}