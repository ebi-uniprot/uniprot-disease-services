package uk.ac.ebi.uniprot.ds.graphql.model;

import graphql.annotations.annotationTypes.GraphQLName;
import lombok.Data;

@Data
@GraphQLName("GeneCoordinate")
public class GeneCoordinateType {
    private String chromosomeNumber;
    private Long startPos;
    private Long endPos;
    private String enGeneId;
    private String enTranscriptId;
    private String enTranslationId;
}
