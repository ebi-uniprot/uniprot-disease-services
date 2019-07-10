/*
 * Created by sahmad on 07/02/19 12:20
 * UniProt Consortium.
 * Copyright (c) 2002-2019.
 *
 */

package uk.ac.ebi.uniprot.ds.rest.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ProteinDTO {
    private String proteinId;
    private String proteinName;
    private String accession;
    private String gene;
    private String description;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Boolean isExternallyMapped;
    private List<String> pathways;
    private List<String> interactions;
    private List<String> variants;
    private List<DiseaseNameNoteDTO> diseases;
    private List<GeneCoordinateDTO> geneCoordinates;
    private List<PublicationDTO> publications;
    private List<String> drugs;

    @Getter
    @Setter
    @Builder
    public static class GeneCoordinateDTO {
        String chromosome;
        Long start;
        Long end;
        String ensemblGeneId;
        String ensemblTranscriptId;
        String ensemblTranslationId;
    }
    @Getter
    @Setter
    @Builder
    public static class DiseaseNameNoteDTO{
    	private String diseaseName;
    	private String note;
    }
}
