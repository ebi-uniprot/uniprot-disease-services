package uk.ac.ebi.uniprot.disease.cli.common;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.uniprot.disease.cli.gda.GDADataLoader;
import uk.ac.ebi.uniprot.disease.cli.mapping.DiseaseMappingsLoader;
import uk.ac.ebi.uniprot.disease.cli.vda.VDADataLoader;
import uk.ac.ebi.uniprot.disease.model.sources.disgenet.DataTypes;
import uk.ac.ebi.uniprot.disease.pipeline.request.DiseaseRequest;
import uk.ac.ebi.uniprot.disease.pipeline.request.WorkflowMetrics;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class MainHelper {
    private static final Logger LOGGER = LoggerFactory.getLogger(MainHelper.class);
    public static final String DEFAULT_DB_CONNECTION_PROP = "dbconnection.properties";
    private static final String ERROR_MSG = "Incorrect value %s passed for param --type. See help";

    public static JCommander parseCommandLineArgs(Object options, String[] args) {
        JCommander jCommander = JCommander.newBuilder().addObject(options).build();
        try {
            jCommander.parse(args);
        } catch (ParameterException pe) {
            LOGGER.debug("Please see the correct usage below.");
            jCommander.usage();
            throw pe;
        }
        return jCommander;
    }

    public static DiseaseRequest getDiseaseRequest(DiseaseDataLoaderArgs options, long startTime) {
        LOGGER.debug("Creating the initial request for the workflow");
        WorkflowMetrics workflowMetrics = new WorkflowMetrics(startTime);
        DiseaseRequest.DiseaseRequestBuilder builder = DiseaseRequest.builder();
        builder.url(options.getUrl()).download(options.isDownload()).batchSize(options.getBatchSize());
        builder.downloadedFilePath(options.getDownloadedFilePath());
        builder.store(options.isStore()).uncompressedFilePath(options.getUncompressedFilePath());
        builder.jdbcUrl(options.getJdbcUrl()).dbUserName(options.getDbUser()).dbPassword(options.getDbPassword());
        builder.dataType(DataTypes.valueOf(options.getDataType()));
        DiseaseRequest request = builder.workflowMetrics(workflowMetrics).build();
        LOGGER.debug("The initial request for the workflow");
        return request;
    }

    // fill the default values from propeties file if missing
    public static void fillDefaultParams(DiseaseDataLoaderArgs options, String propFile, String dbConnxProp) throws IOException {
        fillCoreParams(options, propFile);
        fillDBParams(options, dbConnxProp);
    }

    private static void fillCoreParams(DiseaseDataLoaderArgs options, String propFile) throws IOException {
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

    public static void fillDBParams(DiseaseDataLoaderArgs options, String dbConnectionProp) throws IOException {
        Properties props = loadProperties(dbConnectionProp);
        if(StringUtils.isEmpty(options.getJdbcUrl())){
            options.setJdbcUrl(props.getProperty("jdbcUrl"));
        }

        if(StringUtils.isEmpty(options.getDbUser())){
            options.setDbUser(props.getProperty("user"));
        }

        if(StringUtils.isEmpty(options.getDbPassword())){
            options.setDbPassword(props.getProperty("password"));
        }

    }

    private static Properties loadProperties(String file) throws IOException {

        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        Properties props = new Properties();
        try(InputStream inputStream = loader.getResourceAsStream(file)){
            props.load(inputStream);
        }

        return props;
    }

    private MainHelper(){}


    public static String getDefaultGDConfig(DiseaseDataLoaderArgs options) {
        if(options.getDataType().equals(DataTypes.gda.name())){
            return GDADataLoader.DEFAULT_GDA_CONFIG_LOCATION;
        } else if(options.getDataType().equals(DataTypes.gdpa.name())){
            return GDADataLoader.DEFAULT_GDPA_CONFIG_LOCATION;
        } else {
            throw new IllegalArgumentException(String.format(ERROR_MSG, options.getDataType()));
        }
    }

    public static String getDefaultVDConfig(DiseaseDataLoaderArgs options) {
        if(options.getDataType().equals(DataTypes.vda.name())){
            return VDADataLoader.DEFAULT_VDA_CONFIG_LOCATION;
        } else if(options.getDataType().equals(DataTypes.vdpa.name())){
            return VDADataLoader.DEFAULT_VDPA_CONFIG_LOCATION;
        } else {
            throw new IllegalArgumentException(String.format(ERROR_MSG, options.getDataType()));
        }
    }

    public static String getDefaultMappingConfig(DiseaseDataLoaderArgs options) {
        if(options.getDataType().equals(DataTypes.dm.name())){
            return DiseaseMappingsLoader.DEFAULT_DM_CONFIG_LOCATION;
        } else if(options.getDataType().equals(DataTypes.ug.name())){
            return DiseaseMappingsLoader.DEFAULT_UG_CONFIG_LOCATION;
        } else {
            throw new IllegalArgumentException(String.format(ERROR_MSG, options.getDataType()));
        }
    }
}
