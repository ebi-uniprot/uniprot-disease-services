package uk.ac.ebi.uniprot.disease.pipeline.processor.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.uniprot.disease.pipeline.request.DiseaseRequest;
import uk.ac.ebi.uniprot.disease.utils.JDBCConnectionUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

/**
 * Class responsible for doing clean up in the end of the workflow
 * @author sahmad
 */

public class Cleaner extends BaseProcessor {
    private static final Logger LOGGER = LoggerFactory.getLogger(Cleaner.class);
    private static final String PROCESSOR_NAME = "Cleaner";

    @Override
    public String getProcessorName(){
        return PROCESSOR_NAME;
    }

    @Override
    public void processRequest(DiseaseRequest request) throws IOException {
        LOGGER.debug("Deleting the files {}, {}", request.getDownloadedFilePath(), request.getUncompressedFilePath());

        // close the db connection
        JDBCConnectionUtils.closeConnection(request.getConnxn());
        // delete the downloaded and uncompressed files
        File downloadedFile = new File(request.getDownloadedFilePath());
        File uncompressedFile = new File(request.getUncompressedFilePath());

        updateMetrics(request, downloadedFile, uncompressedFile);

        // delete the files
        Files.deleteIfExists(downloadedFile.toPath());
        Files.deleteIfExists(uncompressedFile.toPath());
        LOGGER.debug("Deleted the files");

        long endTime = System.currentTimeMillis();
        setTotalTime(request, endTime);
    }

    private void setTotalTime(DiseaseRequest request, long endTime) {
        long startTime = request.getWorkflowMetrics().getStartTime();
        request.getWorkflowMetrics().setTotalTimeTaken(endTime - startTime);
    }

    private void updateMetrics(DiseaseRequest request, File downloadedFile, File uncompressedFile) {
        request.getWorkflowMetrics().setSizeOfDownloadedFile(downloadedFile.length());
        request.getWorkflowMetrics().setSizeOfUncompressedFile(uncompressedFile.length());
    }
}
