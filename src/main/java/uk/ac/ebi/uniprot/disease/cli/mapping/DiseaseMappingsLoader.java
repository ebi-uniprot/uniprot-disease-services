package uk.ac.ebi.uniprot.disease.cli.mapping;

import com.beust.jcommander.JCommander;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.uniprot.disease.cli.common.DiseaseDataLoaderArgs;
import uk.ac.ebi.uniprot.disease.cli.common.MainHelper;
import uk.ac.ebi.uniprot.disease.pipeline.processor.common.Cleaner;
import uk.ac.ebi.uniprot.disease.pipeline.processor.common.DownloadProcessor;
import uk.ac.ebi.uniprot.disease.pipeline.processor.common.WorkflowReporter;
import uk.ac.ebi.uniprot.disease.pipeline.processor.mapping.DMDataSaver;
import uk.ac.ebi.uniprot.disease.pipeline.processor.mapping.DMFileParser;
import uk.ac.ebi.uniprot.disease.pipeline.request.DiseaseRequest;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Arrays;


/**
 * @author sahmad
 * Class responsible for downloading, parsing and storing disease mapping
 * http://www.disgenet.org/ds/DisGeNET/results/disease_mappings.tsv.gz
 */
public class DiseaseMappingsLoader {

    private static final Logger LOGGER = LoggerFactory.getLogger(DiseaseMappingsLoader.class);
    public static final String DEFAULT_DM_CONFIG_LOCATION = "dm.properties";

    public static void main(String[] args) throws IOException, SQLException {
        long startTime = System.currentTimeMillis();

        LOGGER.debug("Starting Disease mapping pipeline with the arguments {}", Arrays.toString(args));

        DiseaseDataLoaderArgs options = new DiseaseDataLoaderArgs();

        JCommander jCommander = MainHelper.parseCommandLineArgs(options, args);

        LOGGER.debug("The type of data file is ", options.getDataType());

        if (options.isHelp()) {
            jCommander.usage();// if help is set, show the usage and then do nothing
        } else {
            MainHelper.fillDefaultParams(options, DEFAULT_DM_CONFIG_LOCATION, MainHelper.DEFAULT_DB_CONNECTION_PROP);// fill the default params
            beginProcessing(options, startTime);// start the actual processing
        }

        LOGGER.debug("Disease Mapping pipeline completed", options.getDataType());

    }

    private static void beginProcessing(DiseaseDataLoaderArgs options, long startTime) throws IOException, SQLException {
        LOGGER.debug("The parsed params are {}", options);
        // Create the workflow step1 --> step2 --> step3 --> step4...
        // Download --> Parse --> Save --> Report --> Clean
        DiseaseRequest request = MainHelper.getDiseaseRequest(options, startTime);
        DownloadProcessor downloadProcessor = new DownloadProcessor();
        DMFileParser fileParser = new DMFileParser();
        DMDataSaver dataSaver = new DMDataSaver();
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
