package uk.ac.ebi.uniprot.disease.pipeline.request;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Class to hold metrics about the workflow
 * @author sahmad
 */
@Getter
@Setter
@ToString
public class WorkflowMetrics {
    private long startTime;
    private long downloadTime;
    private long sizeOfDownloadedFile;
    private long sizeOfUncompressedFile;
    private long totalParseTime; // total time take to parse all the records
    private long totalSaveTime; // total time taken to persist the records
    private long recordsParsed;
    private long totalRecords;
    private long recordsSaved;
    private long totalTimeTaken;

    public WorkflowMetrics(long startTime){
        this.startTime = startTime;
    }

}
