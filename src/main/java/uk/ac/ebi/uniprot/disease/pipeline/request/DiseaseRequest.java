package uk.ac.ebi.uniprot.disease.pipeline.request;

import lombok.*;
import uk.ac.ebi.uniprot.disease.model.disgenet.*;

import java.sql.Connection;
import java.util.List;

/**
 * Request for data collection pipeline from DisGeNET
 * @author sahmad
 */
@Getter
@Setter
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
    private List<GeneDiseasePMIDAssociation> parsedGDPARecords;
    private List<VariantDiseasePMIDAssociation> parsedVDPARecords;
    private List<DiseaseMapping> parsedDiseaseMappings;
    private List<UniProtGene> parsedUniProtGeneMappings;
    private int batchSize;
    private WorkflowMetrics workflowMetrics;
    // db related attribs
    private String jdbcUrl;
    private String dbUserName;
    private String dbPassword;
    private Connection connxn;
    private DataTypes dataType;

}
