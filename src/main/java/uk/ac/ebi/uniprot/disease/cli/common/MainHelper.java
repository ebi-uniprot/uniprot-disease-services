package uk.ac.ebi.uniprot.disease.cli.common;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.uniprot.disease.pipeline.request.DiseaseRequest;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class MainHelper {
    private static final Logger LOGGER = LoggerFactory.getLogger(MainHelper.class);

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

    // fill the default values from propeties file if missing
    public static void fillDefaultParams(DiseaseDataLoaderArgs options, String propFile) throws IOException {

        Properties props = loadProperties(propFile);

        if(StringUtils.isEmpty(options.getUrl())){
            options.setUrl(props.getProperty("url"));
        }

        if(StringUtils.isEmpty(options.getDownloadedFilePath())){
            options.setDownloadedFilePath(props.getProperty("downloadedFilePath"));
        }

        if(StringUtils.isEmpty(options.getUncompressedFilePath())){
            options.setUncompressedFilePath(props.getProperty("uncompressedFilePath"));
        }

    }

    public static Properties loadProperties(String file) throws IOException {

        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        Properties props = new Properties();
        try(InputStream inputStream = loader.getResourceAsStream(file)){
            props.load(inputStream);
        }

        return props;
    }

    private MainHelper(){}
}
