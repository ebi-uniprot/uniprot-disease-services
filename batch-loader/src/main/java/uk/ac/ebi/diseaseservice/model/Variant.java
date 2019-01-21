/*
 * Created by sahmad on 1/18/19 9:34 AM
 * UniProt Consortium.
 * Copyright (c) 2002-2019.
 *
 */

package uk.ac.ebi.diseaseservice.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import uk.ac.ebi.kraken.interfaces.uniprot.evidences.EvidenceId;
import uk.ac.ebi.kraken.interfaces.uniprot.features.*;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@Builder
public class Variant implements Serializable {
    private static final long serialVersionUID = 8480742320594052920L;

    private String _id;
    private FeatureSequence originalSequence;
    private List<FeatureSequence> alternativeSequences;
    private FeatureId featureId;
    private List<VariantReport> variantReports;
    protected FeatureLocation featureLocation;
    private FeatureStatus featureStatus;
    private List<EvidenceId> evidenceIds;
    private List<String> diseaseIds;
    private List<String> proteinIds;

    @Override
    public String toString() {
        return "Variant{" +
                "originalSequence=" + originalSequence +
                ", alternativeSequences=" + alternativeSequences +
                ", featureId=" + featureId +
                ", variantReports=" + variantReports +
                ", featureLocation=" + featureLocation +
                ", featureStatus=" + featureStatus +
                ", evidenceIds=" + evidenceIds +
                ", diseaseIds=" + diseaseIds +
                ", proteinIds=" + proteinIds +
                '}';
    }
}
