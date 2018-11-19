package uk.ac.ebi.uniprot.disease.cli.gda;

import com.beust.jcommander.JCommander;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.uniprot.disease.cli.common.MainHelper;
import uk.ac.ebi.uniprot.disease.cli.common.DiseaseDataLoaderArgs;
import uk.ac.ebi.uniprot.disease.pipeline.processor.common.Cleaner;
import uk.ac.ebi.uniprot.disease.pipeline.processor.common.DownloadProcessor;
import uk.ac.ebi.uniprot.disease.pipeline.processor.common.WorkflowReporter;
import uk.ac.ebi.uniprot.disease.pipeline.processor.gda.GDADataSaver;
import uk.ac.ebi.uniprot.disease.pipeline.processor.gda.GDAFileParser;
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
    public static final String DEFAULT_GDA_CONFIG_LOCATION = "gda.properties";

    public static void main(String[] args) throws IOException {

        long startTime = System.currentTimeMillis();

        LOGGER.debug("Starting GDA pipeline with the arguments {}", Arrays.toString(args));

        DiseaseDataLoaderArgs options = new DiseaseDataLoaderArgs();


        JCommander jCommander = MainHelper.parseCommandLineArgs(options, args);

        if (options.isHelp()) {
            jCommander.usage();// if help is set, show the usage and then do nothing
        } else {
            MainHelper.fillDefaultParams(options, DEFAULT_GDA_CONFIG_LOCATION);// fill the default params
            beginProcessing(options, startTime);// start the actual processing
        }

        LOGGER.debug("GDA pipeline completed");
    }

    private static void beginProcessing(DiseaseDataLoaderArgs options, long startTime) throws IOException {
        LOGGER.debug("The parsed params are {}", options);
        // Create the workflow step1 --> step2 --> step3 --> step4...
        DiseaseRequest request = MainHelper.getDiseaseRequest(options, startTime);
        DownloadProcessor downloadProcessor = new DownloadProcessor();
        GDAFileParser fileParser = new GDAFileParser();
        GDADataSaver dataSaver = new GDADataSaver();
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
}
