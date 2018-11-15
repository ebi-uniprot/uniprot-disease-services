package uk.ac.ebi.uniprot.disease.pipeline.processor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.uniprot.disease.pipeline.request.DiseaseRequest;

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

        // delete the downloaded and uncompressed files
        File downloadedFile = new File(request.getDownloadedFilePath());
        Files.deleteIfExists(downloadedFile.toPath());
        // delete the uncompressed file
        File uncompressedFile = new File(request.getUncompressedFilePath());
        Files.deleteIfExists(uncompressedFile.toPath());

        LOGGER.debug("Deleted the files");
    }
}
