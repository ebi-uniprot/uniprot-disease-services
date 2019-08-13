package uk.ac.ebi.uniprot.ds.rest.controller;

import uk.ac.ebi.uniprot.ds.common.model.*;
import uk.ac.ebi.uniprot.ds.rest.dto.ProteinDTO;

import java.util.*;
import java.util.stream.Collectors;

public class ProteinDownloadHelper {
    public static final String DEFAULT_FIELDS = "Protein Accession,Protein Name,Function,Gene Info,Interactions,Pathways,Variants,Diseases,Drugs";

    public static Map<String, String> getProteinMap(Protein protein) {
        Map<String, String> map = new HashMap<>();
        map.put("Protein Accession", protein.getAccession());
        map.put("Protein Name", protein.getName());

        map.put("Diseases", getDiseases(protein));

        map.put("Function", protein.getDesc());
        map.put("Gene Info", protein.getName());

        map.put("Interactions", getInteractionAccessions(protein));

        map.put("Pathways", getPathwayPrimaryIds(protein));

        map.put("Variants", getPathwayIds(protein));

        map.put("Drugs", getDrugNames(protein));

        return map;
    }

    public static String getDiseases(Protein protein) {
        Set<DiseaseProtein> disProts = protein.getDiseaseProteins();
        String diseases = "";
        if (disProts != null && !disProts.isEmpty()) {
            diseases = disProts.stream()
                    .map(dp -> new ProteinDTO.DiseaseNameNoteDTO(dp.getDisease().getName(), dp.getDisease().getNote()))
                    .map(disease -> disease.toString())
                    .collect(Collectors.joining(";"));
        }
        return diseases;
    }

    public static String getInteractionAccessions(Protein protein) {
        List<Interaction> ints = protein.getInteractions();
        String accessions = "";
        if (ints != null) {
            accessions = ints.stream().map(in -> in.getAccession()).collect(Collectors.joining(";"));
        }
        return accessions;
    }

    public static String getPathwayPrimaryIds(Protein protein) {
        List<ProteinCrossRef> ints = protein.getProteinCrossRefs();
        String pids = "";

        if (ints != null) {
            pids = ints.stream().filter(val -> val.getDbType().equals("Reactome"))
                    .map(in -> in.getPrimaryId()).collect(Collectors.joining(";"));
        }
        return pids;
    }

    public static String getPathwayIds(Protein protein) {
        List<Variant> variants = protein.getVariants();
        String pathwayIds = "";
        if (variants != null) {
            pathwayIds = variants.stream().map(var -> var.getFeatureId()).collect(Collectors.joining(";"));
        }
        return pathwayIds;
    }


    public static String getDrugNames(Protein protein) {
        String drugNames = "";

        if (protein != null && protein.getProteinCrossRefs() != null) {

            Set<Drug> drugs = protein.getProteinCrossRefs()
                    .stream()
                    .filter(xref -> xref.getDrugs() != null && !xref.getDrugs().isEmpty())
                    .map(xref -> xref.getDrugs())
                    .flatMap(List::stream)
                    .collect(Collectors.toSet());

            if (drugs != null && !drugs.isEmpty()) { // drug --> name
                // get just the name
                drugNames = drugs.stream().map(d -> d.getName()).collect(Collectors.joining(";"));
            }

        }

        return drugNames;
    }


    public static List<String> getFieldsValues(List<String> headers, Map<String, String> mappedProtein) {
        return headers
                .stream()
                .map(header -> mappedProtein.getOrDefault(header, ""))
                .collect(Collectors.toList());
    }

    public static String getTabSeparatedStr(List<String> list) {
        return list.stream().collect(Collectors.joining("\t", "", "\n"));
    }
}
