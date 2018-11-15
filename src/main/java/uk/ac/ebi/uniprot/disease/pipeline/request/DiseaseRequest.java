package uk.ac.ebi.uniprot.disease.pipeline.request;

import lombok.*;
import uk.ac.ebi.uniprot.disease.model.DisGeNET.GeneDiseaseAssociation;

import java.util.List;

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
    private List<GeneDiseaseAssociation> parsedRecords;
}
