/*
 * Created by sahmad on 07/02/19 12:20
 * UniProt Consortium.
 * Copyright (c) 2002-2019.
 *
 */

package uk.ac.ebi.uniprot.ds.rest.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class VariantDTO {
    private String origSeq;
    private String altSeq;
    private String featureId;
    private String report;
    private FeatureLocationDTO featureLocation;
    private String featureStatus;
    private String proteinAccession;

    @Getter
    @Setter
    @Builder
    public static class FeatureLocationDTO {
        private String startModifier;
        private String endModifier;
        private Integer startId;
        private Integer endId;
    }
}
