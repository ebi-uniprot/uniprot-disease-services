package uk.ac.ebi.uniprot.ds.importer.model;

import lombok.Builder;
import lombok.Data;

/**
 * @author sahmad
 * @created 23/06/2021
 * sample entry
 * {
 *   "clinicalStatus": "Recruiting",
 *   "diseaseFromSourceMappedId": "EFO_0000249",
 *   "targetFromSource": "CHEMBL2243",
 *   "drugId": "CHEMBL112",
 *   "diseaseFromSource": "Alzheimer's Disease",
 *   "clinicalPhase": 4,
 *   "datasourceId": "chembl",
 *   "targetFromSourceId": "O00519",
 *   "studyStartDate": "2016-04-01",
 *   "datatypeId": "known_drug",
 *   "urls": [
 *     {
 *       "niceName": "ClinicalTrials",
 *       "url": "https://clinicaltrials.gov/search?id=%22NCT02719834%22"
 *     }
 *   ]
 * }
 */
@Data
@Builder
public class ChemblEntry {
    private static final String OBO_URL = "http://purl.obolibrary.org/obo/";
    private static final String EFO_URL = "http://www.ebi.ac.uk/efo/";
    private static final String GO_URL = "https://www.ebi.ac.uk/QuickGO/term/";
    private static final String MP_URL = "https://www.mousephenotype.org/data/phenotypes/";
    private static final String ORPHANET_URL = "http://www.orpha.net/ORDO/Orphanet";
    private String chemblId;// drugId
    private String diseaseUrl;// efo or mondo id
    private Integer phase; //
    private String targetChemblId;
    private String status;
    private String clinicalTrialLink;// there is always just one url in the array

    public static String convertToUrl(String diseaseId){
        String[] tokens = diseaseId.split("_");
        if(tokens.length == 1){
            tokens = diseaseId.split(":");
        }

        if(tokens.length < 2){
            return "";
        }
        String idType = tokens[0].toUpperCase();
        String id = tokens[1];
        String diseaseUrl;
        switch (idType){
            case "DOID":
            case "HP":
            case "MONDO" :
                diseaseUrl = OBO_URL + idType + "_" + id;
                break;
            case "EFO":
                diseaseUrl = EFO_URL + idType + "_" + id;
                break;
            case "GO":
                diseaseUrl = GO_URL + idType + ":" + id;
                break;
            case "MP" :
                diseaseUrl = MP_URL + idType + ":" + id;
                break;
            case "ORPHANET":
                diseaseUrl = ORPHANET_URL + "_" + id;
                break;
            default: throw new IllegalArgumentException("Unknown disease id type " + idType);
        }
        return diseaseUrl;
    }
}
