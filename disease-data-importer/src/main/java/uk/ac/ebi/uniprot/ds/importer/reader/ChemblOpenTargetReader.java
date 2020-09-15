package uk.ac.ebi.uniprot.ds.importer.reader;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.batch.item.ItemReader;
import uk.ac.ebi.uniprot.ds.importer.model.ChemblOpenTarget;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

// Json Schema for open target json dump https://raw.githubusercontent.com/opentargets/json_schema/1.5.0/opentargets.json

public class ChemblOpenTargetReader implements ItemReader<ChemblOpenTarget> {
    private ObjectMapper objectMapper;
    private JsonParser jsonParser;
    private static final String DRUG = "drug";
    private static final String EVIDENCE = "evidence";
    private static final String DRUG_2_CLINIC = "drug2clinic";
    private static final String TARGET_2_DRUG = "target2drug";
    private static final String URLS = "urls";
    private static final String URL = "url";
    private static final String NICE_NAME = "nice_name";
    private static final String MOLECULE_TYPE = "molecule_type";
    private static final String MOLECULE_NAME = "molecule_name";
    private static final String CLINICAL_TRIAL_PHASE = "clinical_trial_phase";
    private static final String NUMERIC_INDEX = "numeric_index";
    private static final String MECHANISM_OF_ACTION = "mechanism_of_action";
    private static final String ID = "id";
    private static final String PROVENANCE_TYPE = "provenance_type";
    private static final String LITERATURE = "literature";
    private static final String REFERENCES = "references";
    private static final String LIT_ID = "lit_id";
    private static final String CLINICAL_TRIALS_INFORMATION = "Clinical Trials Information";
    private static final String CHEMBL_TARGET_INFORMATION = "ChEMBL target information";

    public ChemblOpenTargetReader(String filePath) throws IOException {
        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream(filePath);

        if(inputStream == null) {
            inputStream = new FileInputStream(filePath);
        }

        JsonFactory jsonFactory = new JsonFactory();

        this.jsonParser = jsonFactory.createParser(inputStream);
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public ChemblOpenTarget read() throws Exception {
        JsonToken token = this.jsonParser.nextToken();
        ChemblOpenTarget chemblOpenTarget = null;

        if(token != null){
            Map<String, Object> openTargetObj = this.objectMapper.readValue(this.jsonParser, Map.class);
            chemblOpenTarget = convertToChemblOpenTarget(openTargetObj);
        }

        return chemblOpenTarget;
    }

    private ChemblOpenTarget convertToChemblOpenTarget(Map<String, Object> openTargetObj) {
        String chemblTargetUrl = getChemblTargetUrl(openTargetObj);
        String moleculeType = (String) ((Map) openTargetObj.get(DRUG)).get(MOLECULE_TYPE);
        String moleculeName = (String) ((Map) openTargetObj.get(DRUG)).get(MOLECULE_NAME);

        String chemblSourceUrl = (String) ((Map) openTargetObj.get(DRUG)).get(ID);

        // evidence.drug2clinic.clinical_trial_phase.numeric_index
        Integer clinicalTrialPhase = (Integer) ((Map) (
                (Map)
                        ((Map) openTargetObj.get(EVIDENCE)).get(DRUG_2_CLINIC)
        ).get(CLINICAL_TRIAL_PHASE)
        ).get(NUMERIC_INDEX);

        //evidence.target2drug.mechanism_of_action
        String mechOfAction = (String)
                (((Map) ((Map) openTargetObj.get(EVIDENCE)).get(TARGET_2_DRUG)).get(MECHANISM_OF_ACTION));

        // evidence.drug2clinic.urls["nice_name"== "Clinical Trials Information"].url
        String clinicalTrialLink = getClinicalTrialLink(openTargetObj);

        List<String> drugEvidences = getDrugEvidences(openTargetObj);
        // generally it is EFO url "http://www.ebi.ac.uk/efo/EFO_1002014"
        // but sometimes it can be Mondo id as well e.g. "/MONDO_0000396"
        String chemblDiseaseId = (String) ((Map) openTargetObj.get("unique_association_fields")).get("disease_id");

        if(Objects.nonNull(chemblDiseaseId) && chemblDiseaseId.startsWith("/MONDO_")){
            chemblDiseaseId = "http://purl.obolibrary.org/obo" + chemblDiseaseId;
        }


        ChemblOpenTarget.ChemblOpenTargetBuilder openTargetBuilder = ChemblOpenTarget.builder();
        openTargetBuilder.chemblSourceUrl(chemblSourceUrl).chemblTargetUrl(chemblTargetUrl);
        openTargetBuilder.clinicalTrialPhase(clinicalTrialPhase).clinicalTrialLink(clinicalTrialLink);
        openTargetBuilder.drugEvidences(drugEvidences).mechOfAction(mechOfAction);
        openTargetBuilder.moleculeName(moleculeName).moleculeType(moleculeType);
        openTargetBuilder.diseaseId(chemblDiseaseId);


        return openTargetBuilder.build();

    }

    //evidence.target2drug.provenance_type.literature.references[].lit_id
    private List<String> getDrugEvidences(Map<String, Object> openTargetObj) {
        List<String> evidences = null;
        Map<String, List<Map<String, String>>> lit = (Map<String, List<Map<String, String>>>)
                ((Map) ((Map) ((Map) openTargetObj.get(EVIDENCE)).get(TARGET_2_DRUG))
                        .get(PROVENANCE_TYPE)).get(LITERATURE);

        if (lit != null) {
            evidences = lit.get(REFERENCES).stream().map(evVal -> evVal.get(LIT_ID)).collect(Collectors.toList());
        }

        return evidences;
    }

    // evidence.drug2clinic.urls["nice_name"== "Clinical Trials Information"].url
    private String getClinicalTrialLink(Map<String, Object> openTargetObj) {

        List<Map<String, String>> urls = (List<Map<String, String>>)
                ((Map) ((Map) openTargetObj.get(EVIDENCE)).get(DRUG_2_CLINIC)).get(URLS);

        return urls.stream()
                .filter(nameUrlMap -> CLINICAL_TRIALS_INFORMATION.equals(nameUrlMap.get(NICE_NAME)))
                .map(nameUrlMap -> nameUrlMap.get(URL)).findFirst().orElse(null);
    }

    //evidence.target2drug.urls[(nice_name="ChEMBL target information")].url
    private String getChemblTargetUrl(Object obj) {


        List<Map<String, String>> urls = (List<Map<String, String>>)
                ((Map) ((Map) ((Map) obj).get(EVIDENCE)).get(TARGET_2_DRUG)).get(URLS);

        return urls.stream()
                .filter(nameUrlMap -> CHEMBL_TARGET_INFORMATION.equals(nameUrlMap.get(NICE_NAME)))
                .map(nameUrlMap -> nameUrlMap.get(URL)).findFirst().orElse(null);
    }
}
