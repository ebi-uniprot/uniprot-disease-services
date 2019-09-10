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
import lombok.ToString;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import uk.ac.ebi.uniprot.ds.common.model.Protein;

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
    	public DiseaseNameNoteDTO(String diseaseName, String note){
    	    this.diseaseName = diseaseName;
    	    this.note = note;
        }

        @Override
        public String toString() {
            return "{name=" + (this.diseaseName != null ? this.diseaseName : "")
                    + "," + "note=" + (this.note != null ? this.note : "") + "}";
        }
    }

    public static List<ProteinDTO> toProteinDTOList(List<Protein> proteins, ModelMapper modelMapper){
        List<ProteinDTO> dtoList = modelMapper.map(proteins,
                new TypeToken<List<ProteinDTO>>(){}.getType());
        return dtoList;
    }
}
