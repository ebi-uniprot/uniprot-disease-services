package uk.ac.ebi.uniprot.disease.service;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.net.URL;

/**
 * Downloads a file and save it at the given location
 * @author sahmad
 */
public class FileDownloader {

    public static void download(String url, String filePath) throws IOException {
        download(url, filePath, null, null);
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
