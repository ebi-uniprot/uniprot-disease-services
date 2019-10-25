package uk.ac.ebi.uniprot.ds.importer.util;

import com.google.gson.Gson;
import org.codehaus.jettison.json.JSONObject;
import uk.ac.ebi.uniprot.ds.common.model.CrossRef;
import uk.ac.ebi.uniprot.ds.common.model.Disease;
import uk.ac.ebi.uniprot.ds.importer.reader.HumDiseaseReader;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import java.io.File;
import java.io.FileNotFoundException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Pattern;

public class MondoOBOReader {
    public static void main(String[] args) throws FileNotFoundException {
        MondoOBOReader mondoOBOReader = new MondoOBOReader("/Users/sahmad/Documents/mondo.obo");
        mondoOBOReader.loadMondoDisease();
        HumDiseaseReader humDiseaseReader = new HumDiseaseReader("/Users/sahmad/Documents/humdisease.txt");
        Disease disease;
        int found = 0;
        int notFound = 0;
        int total = 0;
        List<String> notFoundNames = new ArrayList<>();
        while ((disease = humDiseaseReader.read()) != null) {
            total++;
            String name = disease.getName();
            boolean exist = mondoOBOReader.doesExistInMondo(name);
            if (!exist) {
                String mim = getMIMFromHD(disease.getCrossRefs());
                exist = mondoOBOReader.doesExistInMondoByMIM(mim);
            }
            if (exist) {
                //System.out.println(name + "\t" + otherName + "\t" + mondoId);
                mondoOBOReader.populateChildren(name, otherName, mondoId);
                found++;
            } else {
                notFoundNames.add(name);
                notFound++;
            }
        }
        System.out.println("Total : " + total);
        System.out.println("Found: " + found);
        System.out.println("Not Found: " + notFound);
        System.out.println("Not Found ");
        notFoundNames.stream().forEach(System.out::println);
        System.out.println("Parent Child" + mondoOBOReader.parentChildren);
        JSONObject obj = new JSONObject(mondoOBOReader.parentChildren);
        System.out.println("Parent Child" + new Gson().toJson(mondoOBOReader.parentChildren));
    }

    private void populateChildren(String humName, String mondoName, String mondoId) {
        List<String> children = new ArrayList<>();
        for (Map<String, List<String>> mDis : this.mondoDiseases){
            List<String> parents = mDis.get("is_a");
            for(String parent : parents){
                if(parent.contains(mondoId)){
                    children.add(mDis.get("name").get(0));
                }
            }
        }
        if(this.parentChildren.containsKey(humName)){
            System.out.println("Found ERROR");
        } else if(!children.isEmpty()){
            this.parentChildren.put(humName, children);
        }
    }

    private boolean doesExistInMondoByMIM(String mim) {
        if (!mim.isEmpty()) {
            for (Map<String, List<String>> mDis : this.mondoDiseases) {
                List<String> xrefs = mDis.get("xref");
                String omimId = "";
                for (String xref : xrefs) {
                    if (xref.startsWith("OMIM:")) {
                        omimId = xref.split(" ")[0];
                    }
                }
                if (omimId.trim().equalsIgnoreCase("OMIM:" + mim)) {
                    otherName = mDis.get("name").get(0);
                    mondoId = mDis.get("id").get(0);
                    return true;
                }
            }
        }
        return false;
    }

    private static String getMIMFromHD(List<CrossRef> crossRefs) {
        for (CrossRef xref : crossRefs) {
            if ("MIM".equalsIgnoreCase(xref.getRefType())) {
                return xref.getRefId();
            }
        }
        return "";
    }

    private List<Map<String, List<String>>> mondoDiseases;
    private Map<String, List<String>> parentChildren;
    private String file;
    private Scanner reader;
    private boolean termStarted;
    private static final Pattern TERM_PATTERN = Pattern.compile("^\\s*$", Pattern.MULTILINE);
    private static final String TYPEDEF_STR = "[Typedef]";
    private static final String TERM_STR = "[Term]";
    private static final String NEW_LINE = "\n";
    private static final String COLON_SPACE = ": ";
    private static String otherName = "";
    private static String mondoId = "";

    public MondoOBOReader(String file) throws FileNotFoundException {
        this.file = file;
        this.reader = new Scanner(new File(this.file), StandardCharsets.UTF_8.name());
        this.mondoDiseases = new ArrayList<>();
        this.parentChildren = new TreeMap<>();
    }


    boolean doesExistInMondo(String diseaseName) {
        for (Map<String, List<String>> mDis : this.mondoDiseases) {
            if (diseaseName.trim().equalsIgnoreCase(mDis.get("name").get(0).trim())) {
                otherName = diseaseName;
                mondoId = mDis.get("id").get(0);
                return true;
            }

        }
        return false;
    }

    void loadMondoDisease() {
        // skip the un-needed lines
        while (this.reader.hasNext() && !this.termStarted) {
            String lines = this.reader.nextLine();
            if (lines.trim().isEmpty()) {
                this.termStarted = true;
                this.reader.useDelimiter(TERM_PATTERN);
            }
        }
        Map<String, List<String>> oboTerm;
        while ((oboTerm = readNextTerm()) != null) {
            if (oboTerm.get("is_obs").isEmpty()) {
                this.mondoDiseases.add(oboTerm);
            }
        }
        System.out.println();
    }

    private Map<String, List<String>> readNextTerm() {
        String termStr = null;
        if (this.reader.hasNext()) {
            termStr = this.reader.next();
        }

        if (termStr == null || termStr.trim().startsWith(TYPEDEF_STR)) {
            return null;
        }

        Map<String, List<String>> oboTerm = convertToOBOTerm(termStr);
        return oboTerm;
    }

    private Map<String, List<String>> convertToOBOTerm(String termStr) {
        String[] lines = termStr.split(NEW_LINE);
        List<String> id = new ArrayList<>();
        List<String> name = new ArrayList<>();
        List<String> xref = new ArrayList<>();
        List<String> isA = new ArrayList<>();
        List<String> isObs = new ArrayList<>();
        for (String line : lines) {
            if (!(line.startsWith(TERM_STR) || line.trim().isEmpty())) {
                String[] lineTokens = line.split(COLON_SPACE);
                switch (lineTokens[0]) {
                    case "id":
                        id.add(lineTokens[1]);
                        break;
                    case "name":
                        name.add(lineTokens[1]);
                        break;
                    case "xref":
                        xref.add(lineTokens[1]);
                        break;
                    case "is_a":
                        isA.add(lineTokens[1]);
                        break;
                    case "is_obsolete":
                        isObs.add(lineTokens[1]);
                        break;
                }
            }
        }
        Map<String, List<String>> result = new HashMap<>();
        result.put("id", id);
        result.put("name", name);
        result.put("xref", xref);
        result.put("is_a", isA);
        result.put("is_obs", isObs);
        return result;
    }
}


