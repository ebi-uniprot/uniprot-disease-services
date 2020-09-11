package uk.ac.ebi.uniprot.ds.importer.reader.diseaseontology;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.util.List;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OBOTerm {
    @NotNull
    private String id;
    @NotNull
    private String name;
    private List<String> altIds;
    private String definition;
    private List<String> synonyms;
    private List<String> xrefs;
    private List<String> isAs; // parent ids
    private boolean isObsolete;
}
