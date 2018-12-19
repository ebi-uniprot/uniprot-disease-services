package uk.ac.ebi.uniprot.disease.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.kraken.interfaces.uniprot.Gene;
import uk.ac.ebi.kraken.interfaces.uniprot.ProteinDescription;
import uk.ac.ebi.kraken.interfaces.uniprot.UniProtEntry;
import uk.ac.ebi.kraken.interfaces.uniprot.comments.CommentType;
import uk.ac.ebi.kraken.interfaces.uniprot.comments.FunctionComment;
import uk.ac.ebi.kraken.interfaces.uniprot.comments.InteractionComment;
import uk.ac.ebi.kraken.interfaces.uniprot.description.FieldType;
import uk.ac.ebi.kraken.interfaces.uniprot.description.Name;
import uk.ac.ebi.uniprot.disease.model.Protein;

import java.util.List;
import java.util.stream.Collectors;

public class ProteinService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ProteinService.class);

    public void createProtein(UniProtEntry uniProtEntry) {
        Protein protein = convertToProtein(uniProtEntry);
        // DAO layer code goes here to persist the protein object
        LOGGER.debug("The protein saved: {}", protein);
    }

    private Protein convertToProtein(UniProtEntry entry) {
        Protein.ProteinBuilder builder = Protein.builder();
        builder.id(entry.getUniProtId().getValue());
        builder.name(getDescription(entry.getProteinDescription()));
        builder.accession(entry.getPrimaryUniProtAccession().getValue());
        builder.gene(getGene(entry.getGenes()));
        builder.functions(getFunctions(entry.getComments(CommentType.FUNCTION)));
        builder.interactionCount(getInteractionCount(entry.getComments(CommentType.INTERACTION)));
        builder.diseaseCount(entry.getComments(CommentType.DISEASE).size());
        return builder.build();
    }

    private Integer getInteractionCount(List<InteractionComment> comments) {

        Integer iCount = comments.parallelStream().map(InteractionComment::getInteractions).mapToInt(List::size).sum();

        return iCount;
    }

    private List<String> getFunctions(List<FunctionComment> comments) {

        List<String> functions = comments.stream().map(FunctionComment::getValue).collect(Collectors.toList());

        return functions;
    }

    private String getDescription(ProteinDescription pd) {
        Name name;

        if (pd.hasRecommendedName()) {
            name = pd.getRecommendedName();
        } else {
            name = pd.getSubNames().get(0);
        }

        return name.getFieldsByType(FieldType.FULL).get(0).getValue();
    }

    private static String getGene(List<Gene> genes) {
        String geneName = null;
        String orfName = null;
        String olnName  = null;

        for (Gene gene : genes) {
            if (gene.hasGeneName()) {
                geneName = gene.getGeneName().getValue();
                break;
            } else if (gene.getOrderedLocusNames() != null && !gene.getOrderedLocusNames().isEmpty()) {
                olnName = gene.getOrderedLocusNames().get(0).getValue();
            } else if (gene.getORFNames() != null && !gene.getORFNames().isEmpty()) {
                orfName = gene.getORFNames().get(0).getValue();
            }

        }

        return (geneName != null) ? geneName : ((olnName != null) ? olnName : orfName);
    }
}
