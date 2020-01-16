package uk.ac.ebi.uniprot.ds.graphql.model;

import lombok.Data;

@Data
public class GeneCoordinateType {
    private String chromosomeNumber;
    private Long startPos;
    private Long endPos;
    private String enGeneId;
    private String enTranscriptId;
    private String enTranslationId;
}
