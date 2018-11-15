package uk.ac.ebi.uniprot.disease.pipeline.processor;

import uk.ac.ebi.uniprot.disease.pipeline.request.DiseaseRequest;
import uk.ac.ebi.uniprot.disease.service.FileDownloader;
import uk.ac.ebi.uniprot.disease.service.FileHandler;

import java.io.IOException;

public class DownloadProcessor extends BaseProcessor {

    @Override
    public void processRequest(DiseaseRequest request) throws IOException {
        downloadFile(request.getUrl(), request.getDownloadedFilePath());
        uncompressFile(request.getDownloadedFilePath(), request.getUncompressedFilePath());
        if(nextProcessor != null){
            nextProcessor.processRequest(request);
        }
    }

    private void uncompressFile(String downloadedFilePath, String uncompressedFilePath) throws IOException {
        FileHandler.uncompressGZFile(downloadedFilePath, uncompressedFilePath);
    }

    private void downloadFile(String url, String fileName) throws IOException {
        FileDownloader.download(url, fileName);
    }
}
