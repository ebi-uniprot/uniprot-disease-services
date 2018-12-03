package uk.ac.ebi.uniprot.disease.cli.vda;

import com.beust.jcommander.JCommander;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.uniprot.disease.cli.common.MainHelper;
import uk.ac.ebi.uniprot.disease.cli.common.DiseaseDataLoaderArgs;
import uk.ac.ebi.uniprot.disease.pipeline.processor.common.Cleaner;
import uk.ac.ebi.uniprot.disease.pipeline.processor.common.DownloadProcessor;
import uk.ac.ebi.uniprot.disease.pipeline.processor.common.WorkflowReporter;
import uk.ac.ebi.uniprot.disease.pipeline.processor.vda.VDADataSaver;
import uk.ac.ebi.uniprot.disease.pipeline.processor.vda.VDAFileParser;
import uk.ac.ebi.uniprot.disease.pipeline.request.DiseaseRequest;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Arrays;

public class VDADataLoader {
    private static final Logger LOGGER = LoggerFactory.getLogger(VDADataLoader.class);
    public static final String DEFAULT_VDA_CONFIG_LOCATION = "vda.properties";
    public static final String DEFAULT_VDPA_CONFIG_LOCATION = "vdpa.properties";

    public static void main(String[] args) throws IOException, SQLException {
        long startTime = System.currentTimeMillis();
        LOGGER.debug("Starting VDA pipeline with the arguments {}", Arrays.toString(args));

        DiseaseDataLoaderArgs options = new DiseaseDataLoaderArgs();
        JCommander jCommander = MainHelper.parseCommandLineArgs(options, args);
        if (options.isHelp()) {
            jCommander.usage();// if help is set, show the usage and then do nothing
        } else {
            // fill the default params vals for the missing ones
            MainHelper.fillDefaultParams(options, MainHelper.getDefaultVDConfig(options), MainHelper.DEFAULT_DB_CONNECTION_PROP);
            beginProcessing(options, startTime);// start the actual processing
        }

        LOGGER.debug("VDA pipeline completed");

    }

    private static void beginProcessing(DiseaseDataLoaderArgs options, long startTime) throws IOException, SQLException {
        LOGGER.debug("The parsed params are {}", options);
        // Create the workflow step1 --> step2 --> step3 --> step4...
        DiseaseRequest request = MainHelper.getDiseaseRequest(options, startTime);
        DownloadProcessor downloadProcessor = new DownloadProcessor();
        VDAFileParser fileParser = new VDAFileParser();
        VDADataSaver dataSaver = new VDADataSaver();
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
