package uk.ac.ebi.uniprot.disease.cli.common;

import com.beust.jcommander.Parameter;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Params for VDA and GDA data download, change the url to download
 * @author sahmad
 */
@Getter
@Setter
@ToString
public class DiseaseDataLoaderArgs {
    @Parameter(names = {"--url", "-u"}, description = "URL for the DisGeNET gda data")
    private String url;

    @Parameter(hidden = true, names = {"--download", "-d"}, arity = 1, description = "Whether to download the data from DisGeNET or use the local one. By default it is set to true")
    private boolean download = true;

    @Parameter(names = {"--path", "-p"}, description = "Path of the existing gda data file if download is set to false")
    private String path;

    @Parameter(hidden = true, names = {"--store", "-s"}, arity = 1, description = "Whether to store the data in the DB or ignore after parsing. By default it is set to true")
    private boolean store = true;

    @Parameter(names = {"--downloadPath", "-dp"}, description = "Absolute path of the file to be downloaded from the DisGeNET")
    private String downloadedFilePath;

    @Parameter(names = {"--uncompressedPath", "-up"}, description = "Absolute path of the uncompressed file")
    private String uncompressedFilePath;

    @Parameter(names = {"--batch", "-b"}, description = "The size of the batch to be parsed and updated in DB. Defaults to 200")
    private Integer batchSize = 200;

    @Parameter(names = "--help", arity = 1, help = true, description = "To get all the options and their details")
    private boolean help = false;
}

