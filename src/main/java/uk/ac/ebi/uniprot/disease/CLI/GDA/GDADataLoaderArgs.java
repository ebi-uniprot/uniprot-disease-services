package uk.ac.ebi.uniprot.disease.CLI.GDA;

import com.beust.jcommander.Parameter;
import lombok.Getter;
import lombok.ToString;

/**
 * @author sahmad
 */
@Getter
@ToString
public class GDADataLoaderArgs {
    @Parameter(names = {"--url", "-u"}, description = "URL for the DisGeNET GDA data")
    private String url = "http://www.disgenet.org/ds/DisGeNET/results/curated_gene_disease_associations.tsv.gz";

    @Parameter(names = {"--download", "-d"}, arity = 1, description = "Whether to download the data from DisGeNET or use the local one. By default it is set to true")
    private boolean download = true;

    @Parameter(names = {"--path", "-p"}, description = "Path of the existing GDA data file if download is set to false")
    private String path;

    @Parameter(names = {"--store", "-s"}, arity = 1, description = "Whether to store the data in the DB or ignore after parsing. By default it is set to true")
    private Boolean store = true;

    @Parameter(names = {"--downloadPath", "-dp"}, description = "Absolute path of the file to be downloaded from the DisGeNET")
    private String downloadedFilePath = "/tmp/gda.tsv.tgz";

    @Parameter(names = "--help", arity = 1, help = true, description = "To get all the options and their details")
    private boolean help = false;
}

