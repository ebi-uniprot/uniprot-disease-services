package uk.ac.ebi.uniprot.disease.pipeline.processor.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.uniprot.disease.pipeline.request.DiseaseRequest;
import uk.ac.ebi.uniprot.disease.service.FileDownloader;
import uk.ac.ebi.uniprot.disease.service.FileHandler;

import java.io.IOException;

/**
 * Handler to download data from DisGeNET website
 * @author sahmad
 */

public class DownloadProcessor extends BaseProcessor {
    private static final Logger LOGGER = LoggerFactory.getLogger(DownloadProcessor.class);
    private static final String PROCESSOR_NAME = "DownloadProcessor";

    public String getProcessorName(){
        return PROCESSOR_NAME;
    }

    @Override
    public void processRequest(DiseaseRequest request) throws IOException {

        LOGGER.debug("Starting to downdload from {}", request.getUrl());
        long startTime = System.currentTimeMillis();

        downloadFile(request.getUrl(), request.getDownloadedFilePath());

        long endTime = System.currentTimeMillis();
        long downloadTime = endTime - startTime;
        request.getWorkflowMetrics().setDownloadTime(downloadTime);
        LOGGER.debug("File is downloaded to {}", request.getDownloadedFilePath());


        // uncompress the downloaded gz file
        uncompressFile(request.getDownloadedFilePath(), request.getUncompressedFilePath());
        LOGGER.debug("Downloaded file uncompressed into {}", request.getUncompressedFilePath());

        if(nextProcessor != null){
            LOGGER.debug("Invoking the next processor {}", nextProcessor.getProcessorName());
            nextProcessor.processRequest(request);
        }
    }

    public void uncompressFile(String downloadedFilePath, String uncompressedFilePath) throws IOException {
        FileHandler.uncompressGZFile(downloadedFilePath, uncompressedFilePath);
    }

    public void downloadFile(String url, String fileName) throws IOException {
        FileDownloader.download(url, fileName);
    }
}
