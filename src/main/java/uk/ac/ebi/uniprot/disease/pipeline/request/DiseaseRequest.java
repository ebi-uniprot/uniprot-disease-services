package uk.ac.ebi.uniprot.disease.pipeline.request;

import lombok.*;
import uk.ac.ebi.uniprot.disease.model.disgenet.GeneDiseaseAssociation;
import uk.ac.ebi.uniprot.disease.model.disgenet.VariantDiseaseAssociation;

import java.util.List;

/**
 * Request for data collection pipeline from DisGeNET
 * @author sahmad
 */
@Getter
@Setter
@EqualsAndHashCode
@ToString
@Builder
public class DiseaseRequest {
    private String url;
    private boolean download;
    private boolean store;
    private String downloadedFilePath;
    private String uncompressedFilePath;
    private List<GeneDiseaseAssociation> parsedGDARecords;
    private List<VariantDiseaseAssociation> parsedVDARecords;
    private int batchSize;
    private WorkflowMetrics workflowMetrics;
}
