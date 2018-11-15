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
import java.util.Arrays;

public class VDADataLoader {
    private static final Logger LOGGER = LoggerFactory.getLogger(VDADataLoader.class);

    public static void main(String[] args) throws IOException {
        LOGGER.debug("Starting VDA pipeline with the arguments {}", Arrays.toString(args));

        DiseaseDataLoaderArgs options = new DiseaseDataLoaderArgs();
        JCommander jCommander = MainHelper.parseCommandLineArgs(options, args);
        if (options.isHelp()) {
            jCommander.usage();// if help is set, show the usage and then do nothing
        } else {
            beginProcessing(options);// start the actual processing
        }

        LOGGER.debug("GDA pipeline completed");

    }

    private static void beginProcessing(DiseaseDataLoaderArgs options) throws IOException {
        LOGGER.debug("The parsed params are {}", options);
        // Create the workflow step1 --> step2 --> step3 --> step4...
        DiseaseRequest request = MainHelper.getDiseaseRequest(options);
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
