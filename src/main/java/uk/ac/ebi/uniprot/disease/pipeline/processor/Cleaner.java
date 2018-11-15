package uk.ac.ebi.uniprot.disease.pipeline.processor;

import uk.ac.ebi.uniprot.disease.pipeline.request.DiseaseRequest;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class Cleaner extends BaseProcessor {
    @Override
    public void processRequest(DiseaseRequest request) throws IOException {
        // delete the downloaded and uncompressed files
        File downloadedFile = new File(request.getDownloadedFilePath());
        Files.deleteIfExists(downloadedFile.toPath());
        // delete the uncompressed file
        File uncompressedFile = new File(request.getUncompressedFilePath());
        Files.deleteIfExists(uncompressedFile.toPath());
    }
}
