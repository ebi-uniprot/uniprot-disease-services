package uk.ac.ebi.uniprot.disease.service;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.uniprot.disease.utils.Constants;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * File responsible for handling *.gz file like uncompressing and uncompressing
 * @author sahmad
 */
public class FileHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(FileHandler.class);

    /**
     * uncompresses the gz file
     * @param inputFile Absolute file path of the gz file with the name
     * @param outputFile Absolute file path of the uncompressed file with the name
     * @throws IOException
     */
    public static void uncompressGZFile(String inputFile, String outputFile) throws IOException {
        LOGGER.debug("Start uncompressing {} into {}", inputFile, outputFile);
        File input = new File(inputFile);
        File output = new File(outputFile);

        try(GZIPInputStream inputStream = new GZIPInputStream(new FileInputStream(input))){
            try(FileOutputStream outputStream = new FileOutputStream(output)){
                byte[] buffer = new byte[Constants.ONE_KB];
                int len;
                while((len = inputStream.read(buffer)) != -1){
                    outputStream.write(buffer, Constants.ZERO, len);
                }
            }
        }

        // convert to utf-8
        /*try (OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(outputFile), StandardCharsets.UTF_8)) {
            try(BufferedReader reader = new BufferedReader(new FileReader(tmpFile))){
                String line = null;
                while((line = reader.readLine()) != null){
                    writer.append(line);
                    writer.append('\n');
                }
            }

        }*/

        // another way to convert to utf-8
        if(!(inputFile.contains("gdpa") || inputFile.contains("vdpa"))) { // do this for small files only
            File file = new File(outputFile);
            String content = FileUtils.readFileToString(file, StandardCharsets.ISO_8859_1.name());
            FileUtils.write(file, content, StandardCharsets.UTF_8.name());
        }

        LOGGER.debug("Uncompressed {} into {}", inputFile, outputFile);
    }

    /**
     * compresses to gz file
     * @param inputFile Absolute path of the file to be compressed (with the name)
     * @param outputFile Absolute path of the output compressed file(with the name)
     * @throws IOException
     */
    public static void compressToGZFile(String inputFile, String outputFile) throws IOException {
        LOGGER.debug("Start compressing {} into {}", inputFile, outputFile);

        File input = new File(inputFile);
        File output = new File(outputFile);

        try(GZIPOutputStream outputStream = new GZIPOutputStream(new FileOutputStream(output))){
            try(FileInputStream inputStream = new FileInputStream(input)){
                byte[] buffer = new byte[Constants.ONE_KB];
                int len;
                while((len = inputStream.read(buffer)) != -1){
                    outputStream.write(buffer, Constants.ZERO, len);
                }
            }
        }

        LOGGER.debug("Compressed {} into {}", inputFile, outputFile);
    }

    private FileHandler(){}

}
