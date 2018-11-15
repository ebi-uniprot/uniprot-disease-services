package uk.ac.ebi.uniprot.disease.service;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.URL;

/**
 * Downloads a file and save it at the given location
 * @author sahmad
 */
public class FileDownloader {
    private static final Logger LOGGER = LoggerFactory.getLogger(FileDownloader.class);

    public static void download(String url, String filePath) throws IOException {
        LOGGER.debug("Going to download data from {} and save into {}", url, filePath);
        download(url, filePath, null, null);
        LOGGER.debug("Download completed");
    }

    public static void download(String url, String filePath, Integer connectionTimeout, Integer readTimeout) throws IOException {

        if(StringUtils.isEmpty(url) || StringUtils.isEmpty(filePath)){
            throw new IllegalArgumentException("url and/or filePath cannot be null or empty");

        }

        URL source = new URL(url);
        File destination = new File(filePath);

        FileUtils.copyURLToFile(source, destination);
    }
}
