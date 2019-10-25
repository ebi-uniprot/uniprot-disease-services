package uk.ac.ebi.uniprot.ds.importer.reader;

import org.apache.commons.lang3.tuple.Pair;
import org.springframework.batch.item.ItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import uk.ac.ebi.uniprot.ds.common.dao.DiseaseDAO;
import uk.ac.ebi.uniprot.ds.common.model.CrossRef;
import uk.ac.ebi.uniprot.ds.common.model.Disease;
import uk.ac.ebi.uniprot.ds.importer.processor.DiseaseOntologyProcessor;
import uk.ac.ebi.uniprot.ds.importer.reader.diseaseontology.DiseaseOntologyReader;
import uk.ac.ebi.uniprot.ds.importer.reader.diseaseontology.OBOTerm;

import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class DOToHumMappingReader implements ItemReader<Pair<String, String>> {
    @Autowired
    private DiseaseDAO diseaseDAO;
    private Map<String, Disease> diseaseNameToDiseaseMap;
    private Iterator<Map.Entry<String, Disease>> iterator;
    private List<OBOTerm> mondoDiseases;

    public DOToHumMappingReader() throws FileNotFoundException {
        this.diseaseNameToDiseaseMap = new HashMap<>();
    }
    private static int found = 0;
    private static int notFound = 0;
    private static int total = 0;

    @Override
    public Pair<String, String> read() throws FileNotFoundException {
        loadDiseaseIdDiseaseMapFromDisease();
        Pair<String, String> mondoHumPair = null;
        while(this.iterator.hasNext()){
            Map.Entry<String, Disease> diseaseEntry = this.iterator.next();
            //<MondoName, HumDiseaseName> pair
            mondoHumPair = getMondoHumNamePair(diseaseEntry);
            total++;
            if(mondoHumPair != null && !(mondoHumPair.getLeft().equals(mondoHumPair.getRight()))){
                found++;
                System.out.println(mondoHumPair);
                return mondoHumPair;
            } else if(mondoHumPair != null){
                found++;
                System.out.println("Same " + mondoHumPair); //TODO
            } else {
                notFound++;
                System.out.println("Not found" + diseaseEntry.getKey());
            }

            // get the
        }
        System.out.println(" Total :" + total);
        System.out.println(" Found :" + found);
        System.out.println(" Not Found :" + notFound);
        return mondoHumPair;
    }

    private Pair<String, String> getMondoHumNamePair(Map.Entry<String, Disease> diseaseEntry) {
        String diseaseName = diseaseEntry.getKey();
        String omimId = getMIMFromHD(diseaseEntry.getValue().getCrossRefs());
        String mondoDiseaseName = getMondoName(diseaseName, omimId);
        if(mondoDiseaseName != null){
            return Pair.of(mondoDiseaseName.toLowerCase(), diseaseName.toLowerCase());
        } else {
            System.out.println("Not Found ===> Disease " + diseaseName + " with MIM " + omimId);
        }

        return null;
    }

    String getMondoName(String diseaseName, String omimId) {
        for (OBOTerm mondoTerm : this.mondoDiseases) {
            String mondoOmimId = getOMIMId(mondoTerm);
            String mondoDiseaseName = mondoTerm.getName().trim();
            if (diseaseName.trim().equalsIgnoreCase(mondoDiseaseName)
            || (!omimId.isEmpty() && !mondoOmimId.isEmpty() && omimId.equalsIgnoreCase(mondoOmimId))) {
                return mondoDiseaseName;
            }
        }
        return null;
    }

    private String getOMIMId(OBOTerm mondoTerm) {
        String omimId = "";
        List<String> xrefs = mondoTerm.getXrefs();
        for (String xref : xrefs) {
            if (xref.startsWith("OMIM:")) {
                omimId = xref.split(" ")[0];
            }
        }
       return omimId;
    }

    private String getMIMFromHD(List<CrossRef> crossRefs) {
        for (CrossRef xref : crossRefs) {
            if ("MIM".equalsIgnoreCase(xref.getRefType())) {
                return "OMIM:" + xref.getRefId();
            }
        }
        return "";
    }

    private void loadDiseaseIdDiseaseMapFromDisease() throws FileNotFoundException {
        if(this.diseaseNameToDiseaseMap.isEmpty()) {
            List<Disease> allDiseases = this.diseaseDAO.findAll();
            for (Disease d : allDiseases) {
                this.diseaseNameToDiseaseMap.put(d.getName().trim().toLowerCase(), d);
            }
            this.iterator = this.diseaseNameToDiseaseMap.entrySet().iterator();
            this.mondoDiseases = new DiseaseOntologyReader("/Users/sahmad/Documents/mondo.obo").read();
        }
    }
}
