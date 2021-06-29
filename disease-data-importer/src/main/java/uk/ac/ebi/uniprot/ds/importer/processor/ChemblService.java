package uk.ac.ebi.uniprot.ds.importer.processor;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import lombok.extern.slf4j.Slf4j;
import uk.ac.ebi.uniprot.ds.importer.model.DrugIndication;
import uk.ac.ebi.uniprot.ds.importer.model.Mechanism;
import uk.ac.ebi.uniprot.ds.importer.model.Molecule;
import uk.ac.ebi.uniprot.ds.importer.util.Constants;

/**
 * @author sahmad
 * @created 28/06/2021
 */
@Slf4j
public class ChemblService {
    private RestTemplate restTemplate;
    private ObjectMapper objectMapper;

    private static final String GET_MOLECULE_URL = "https://www.ebi.ac.uk/chembl/api/data/molecule/";
    private static final String GET_DRUGS_URL = "https://www.ebi.ac.uk/chembl/api/data/drug_indication?";
    private static final String GET_MECHANISMS_URL = "https://www.ebi.ac.uk/chembl/api/data/mechanism?";
    private static final Integer PAGE_SIZE = 500;

    public ChemblService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
        this.objectMapper = new ObjectMapper();
    }

    public Molecule getMolecule(String chemblId) throws IOException {
        log.info("Getting molecule for chemblId {}", chemblId);
        ResponseEntity<String> response = this.restTemplate.getForEntity(GET_MOLECULE_URL + chemblId + "?format=json", String.class);
        HttpStatus statusCode = response.getStatusCode();

        if (!HttpStatus.OK.equals(statusCode)) {
            throw new HttpServerErrorException(statusCode, "Chembl API error for " + GET_MOLECULE_URL);
        }

        JsonNode jsonNode = this.objectMapper.readTree(response.getBody());
        Molecule.MoleculeBuilder builder = Molecule.builder();
        builder.name(jsonNode.get("pref_name").asText()).chemblId(chemblId).moleculeType(jsonNode.get("molecule_type").asText());
        builder.sourceType("ChEMBL");
        return builder.build();
    }

    public List<DrugIndication> getDrugIndications(String chemblId) throws IOException {
        log.info("Getting drug indications for chemblId {}", chemblId);
        List<DrugIndication> drugIndications = new ArrayList<>();
        Integer offset = 0;
        JsonNode pageMeta;
        do {
            String queryParams = "molecule_chembl_id=" + chemblId + "&format=json&limit=" + PAGE_SIZE + "&offset=" + offset;
            ResponseEntity<String> response = this.restTemplate.getForEntity(GET_DRUGS_URL + queryParams, String.class);
            checkStatus(response, GET_DRUGS_URL);
            JsonNode jsonNode = this.objectMapper.readTree(response.getBody());
            Iterator<JsonNode> drugIterator = jsonNode.get("drug_indications").iterator();
            while (drugIterator.hasNext()) {
                JsonNode drugNode = drugIterator.next();
                DrugIndication.DrugIndicationBuilder builder = DrugIndication.builder();
                builder.efoId(drugNode.get("efo_id").asText()).maxPhase(drugNode.get("max_phase_for_ind").asInt());
                Iterator<JsonNode> iter = drugNode.get("indication_refs").iterator();
                List<String> clinicalTrialLinks = new ArrayList<>();
                while (iter.hasNext()) {
                    clinicalTrialLinks.add(iter.next().get("ref_url").asText());
                }
                builder.clinicalTrialLinks(clinicalTrialLinks);
                drugIndications.add(builder.build());
            }
            pageMeta = jsonNode.get("page_meta");
            offset = offset + PAGE_SIZE;
        } while (!pageMeta.get("next").isNull());

        return drugIndications;
    }

    public List<Mechanism> getMechanisms(String chemblId, String chemblIdParamName) throws IOException {
        log.info("Getting drug mechanisms for chemblId {}", chemblId);
        List<Mechanism> mechanisms = new ArrayList<>();
        Integer offset = 0;
        JsonNode pageMeta;
        do {
            String queryParams = chemblIdParamName + "=" + chemblId + "&format=json&limit=" + PAGE_SIZE + "&offset=" + offset;
            ResponseEntity<String> response = this.restTemplate.getForEntity(GET_MECHANISMS_URL + queryParams, String.class);
            checkStatus(response, GET_MECHANISMS_URL);
            JsonNode jsonNode = this.objectMapper.readTree(response.getBody());
            Iterator<JsonNode> mechanismIter = jsonNode.get("mechanisms").iterator();
            while (mechanismIter.hasNext()) {
                JsonNode mechanism = mechanismIter.next();
                Mechanism.MechanismBuilder builder = Mechanism.builder();
                builder.mechanismOfAction(mechanism.get("mechanism_of_action").asText());
                builder.targetChemblId(mechanism.get("target_chembl_id").asText());
                builder.moleculeChemblId(mechanism.get("molecule_chembl_id").asText());
                builder.parentMoleculeChemblId(mechanism.get("parent_molecule_chembl_id").asText());
                Iterator<JsonNode> iter = mechanism.get("mechanism_refs").iterator();
                List<String> evidences = new ArrayList<>();
                while (iter.hasNext()) {
                    JsonNode evidence = iter.next();
                    if (Constants.PUBMED_STR.equalsIgnoreCase(evidence.get("ref_type").asText())) {
                        evidences.add(evidence.get("ref_url").asText());
                    }
                }
                builder.evidences(evidences);
                mechanisms.add(builder.build());
            }
            pageMeta = jsonNode.get("page_meta");
            offset = offset + PAGE_SIZE;
        } while (!pageMeta.get("next").isNull());
        return mechanisms;
    }

    private void checkStatus(ResponseEntity<String> response, String requestUrl){
        HttpStatus statusCode = response.getStatusCode();
        if (!HttpStatus.OK.equals(statusCode)) {
            throw new HttpServerErrorException(statusCode, "Chembl API error for " + requestUrl);
        }
    }
}
