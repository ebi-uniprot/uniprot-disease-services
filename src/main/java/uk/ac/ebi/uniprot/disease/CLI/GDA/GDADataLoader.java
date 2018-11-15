package uk.ac.ebi.uniprot.disease.CLI.GDA;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.uniprot.disease.pipeline.processor.*;
import uk.ac.ebi.uniprot.disease.pipeline.request.DiseaseRequest;

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
            if (options.isHelp()) {
                jCommander.usage();
                return;
            } else {
                beginProcessing(options);
            }
            LOGGER.debug("The passed params are {}", options);
        } catch (ParameterException pe) {
            LOGGER.error(pe.getMessage());
            jCommander.usage();
        }
    }

    private static void beginProcessing(GDADataLoaderArgs options) throws IOException {

        DiseaseRequest request = getDiseaseRequest(options);
        DownloadProcessor downloadProcessor = new DownloadProcessor();
        FileParser fileParser = new FileParser();
        DataSaver dataSaver = new DataSaver();
        WorkflowReporter reporter = new WorkflowReporter();
        Cleaner cleaner = new Cleaner();
        // set the next processor
        downloadProcessor.setNextProcessor(fileParser);
        fileParser.setNextProcessor(dataSaver);
        dataSaver.setNextProcessor(reporter);
        reporter.setNextProcessor(cleaner);

        // kick of the workflow
        downloadProcessor.processRequest(request);
    }

    private static DiseaseRequest getDiseaseRequest(GDADataLoaderArgs options) {
        DiseaseRequest.DiseaseRequestBuilder builder = DiseaseRequest.builder();
        builder.url(options.getUrl()).download(options.isDownload()).downloadedFilePath(options.getDownloadedFilePath());
        DiseaseRequest request = builder.store(options.isStore()).uncompressedFilePath(options.getUncompressedFilePath()).build();
        return request;
    }
}
