package uk.ac.ebi.uniprot.disease.cli.gda;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.uniprot.disease.pipeline.processor.*;
import uk.ac.ebi.uniprot.disease.pipeline.request.DiseaseRequest;

import java.io.IOException;
import java.util.Arrays;

/**
 * Command Line Interface to download, parse and store gda data
 *
 * @author sahmad
 */
public class GDADataLoader {
    private static final Logger LOGGER = LoggerFactory.getLogger(GDADataLoader.class);

    public static void main(String[] args) throws IOException {
        LOGGER.debug("Starting GDA pipeline with the arguments {}", Arrays.toString(args));

        GDADataLoaderArgs options = new GDADataLoaderArgs();
        JCommander jCommander = JCommander.newBuilder().addObject(options).build();

        try {
            jCommander.parse(args);
            if (options.isHelp()) {
                jCommander.usage();// if help is set, show the usage and then do nothing
            } else {
                beginProcessing(options);// start the actual processing
            }
        } catch (ParameterException pe) {
            LOGGER.error(pe.getMessage());
            LOGGER.debug("Please see the correct usage below.");
            jCommander.usage();
        }

    }

    private static void beginProcessing(GDADataLoaderArgs options) throws IOException {
        LOGGER.debug("The parsed params are {}", options);
        // Create the workflow step1 --> step2 --> step3 --> step4...
        DiseaseRequest request = getDiseaseRequest(options);
        DownloadProcessor downloadProcessor = new DownloadProcessor();
        FileParser fileParser = new FileParser();
        DataSaver dataSaver = new DataSaver();
        WorkflowReporter reporter = new WorkflowReporter();
        Cleaner cleaner = new Cleaner();
        // set the next processors
        downloadProcessor.setNextProcessor(fileParser);
        fileParser.setNextProcessor(dataSaver);
        dataSaver.setNextProcessor(reporter);
        reporter.setNextProcessor(cleaner);

        // kick off the workflow
        LOGGER.debug("Starting the workflow");
        downloadProcessor.processRequest(request);
        LOGGER.debug("The workflow completed");
    }

    private static DiseaseRequest getDiseaseRequest(GDADataLoaderArgs options) {
        LOGGER.debug("Creating the initial request for the workflow");
        DiseaseRequest.DiseaseRequestBuilder builder = DiseaseRequest.builder();
        builder.url(options.getUrl()).download(options.isDownload()).batchSize(options.getBatchSize());
        builder.downloadedFilePath(options.getDownloadedFilePath());
        builder.store(options.isStore()).uncompressedFilePath(options.getUncompressedFilePath());
        DiseaseRequest request = builder.build();
        LOGGER.debug("The initial request for the workflow {}", request);
        return request;
    }
}
