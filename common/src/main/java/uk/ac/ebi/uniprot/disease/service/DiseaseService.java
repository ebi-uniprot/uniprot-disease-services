package uk.ac.ebi.uniprot.disease.service;

import uk.ac.ebi.kraken.interfaces.uniprot.UniProtEntry;
import uk.ac.ebi.kraken.interfaces.uniprot.comments.Comment;
import uk.ac.ebi.kraken.interfaces.uniprot.comments.DiseaseCommentStructured;
import uk.ac.ebi.kraken.model.uniprot.comments.Comments;
import uk.ac.ebi.uniprot.disease.model.Disease;
import uk.ac.ebi.uniprot.disease.model.Protein;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class DiseaseService {
    public Set<Disease> getDiseases(List<DiseaseCommentStructured> diseaseComments, Protein protein) {
        Set<Disease> diseases = diseaseComments.stream().map(dc -> convertToDisease(dc, protein)).collect(Collectors.toSet());
        return diseases;
    }

    public Disease convertToDisease(DiseaseCommentStructured diseaseComment, Protein protein){
        Disease.DiseaseBuilder builder = Disease.builder();
        uk.ac.ebi.kraken.interfaces.uniprot.comments.Disease disease = diseaseComment.getDisease();
        String descr = disease.getDescription().getValue();
        String name = disease.getDiseaseId().getValue();
        String acronym = disease.getAcronym().getValue();
        builder.description(descr).name(name).acronym(acronym);

        //Set<Protein> proteins = new HashSet<>();
        //proteins.add(protein);
        //builder.proteins(proteins);

        return builder.build();
    }
}
