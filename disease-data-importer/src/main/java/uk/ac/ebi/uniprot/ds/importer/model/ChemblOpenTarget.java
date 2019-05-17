package uk.ac.ebi.uniprot.ds.importer.model;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@EqualsAndHashCode
@ToString
public class ChemblOpenTarget { // Class to represent chembl open target json object
    private String chemblTargetUrl;//evidence.target2drug.urls[(nice_name="ChEMBL target information")].url
    private String moleculeType;// drug.molecule_type
    private String moleculeName;// drug.molecule_name
    private String chemblSourceUrl;//drug.id
    private Integer clinicalTrialPhase;// evidence.drug2clinic.clinical_trial_phase.numeric_index
    private String clinicalTrialLink;// evidence.drug2clinic.urls["nice_name"== "Clinical Trials Information].url
    private List<String> drugEvidences;//evidence.target2drug.provenance_type.literature.references[].lit_id
    private String mechOfAction;//evidence.target2drug.mechanism_of_action
}
