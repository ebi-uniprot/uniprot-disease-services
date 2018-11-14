package uk.ac.ebi.uniprot.disease.CLI.GDA;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.uniprot.disease.service.FileDownloader;

import java.io.IOException;

/**
 * Command Line Interface to download, parse and store GDA data
 *
 * @author sahmad
 */
public class GDADataLoader {
    private static final Logger LOGGER = LoggerFactory.getLogger(GDADataLoader.class);

    public static void main(String[] args) throws IOException {
        GDADataLoaderArgs options = new GDADataLoaderArgs();
        JCommander jCommander = JCommander.newBuilder()
                .addObject(options)
                .build();
        try {
            jCommander.parse(args);
            FileDownloader.download(options.getUrl(), options.getDownloadedFilePath());
            if (options.isHelp()) {
                jCommander.usage();
            }
            LOGGER.info("The passed params are {}", options);
        } catch (ParameterException pe) {
            LOGGER.error(pe.getMessage());
            jCommander.usage();
        }
    }
}
