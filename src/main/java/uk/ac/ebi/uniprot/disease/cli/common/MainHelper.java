package uk.ac.ebi.uniprot.disease.cli.common;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.uniprot.disease.pipeline.request.DiseaseRequest;

public class MainHelper {
    private final static Logger LOGGER = LoggerFactory.getLogger(MainHelper.class);

    public static JCommander parseCommandLineArgs(Object options, String[] args) {
        JCommander jCommander = JCommander.newBuilder().addObject(options).build();
        try {
            jCommander.parse(args);
        } catch (ParameterException pe) {
            LOGGER.error(pe.getMessage());
            LOGGER.debug("Please see the correct usage below.");
            jCommander.usage();
            System.exit(1); // halt the processing
        }
        return jCommander;
    }

    public static DiseaseRequest getDiseaseRequest(DiseaseDataLoaderArgs options) {
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
