/*
 * Created by sahmad on 06/02/19 09:41
 * UniProt Consortium.
 * Copyright (c) 2002-2019.
 *
 */

package uk.ac.ebi.uniprot.ds.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ProteinPathwaysDTO {
    private String accession;
    private String proteinId;
    private String proteinName;
    private String gene;
    private List<PathwayDTO> pathways;

    @Getter
    @Setter
    public static class PathwayDTO {
        String primaryId;
        String description;
        public PathwayDTO(String primaryId, String description){
            this.primaryId = primaryId;
            this.description = description;
        }
    }
}
