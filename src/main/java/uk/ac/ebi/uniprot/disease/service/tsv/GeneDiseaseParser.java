package uk.ac.ebi.uniprot.disease.service.tsv;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.uniprot.disease.model.disgenet.GeneDiseaseAssociation;

import java.util.ArrayList;
import java.util.List;

/**
 * Class responsible for parsing the GDA data
 * @author sahmad
 */

public class GeneDiseaseParser {
    private static final Logger LOGGER = LoggerFactory.getLogger(GeneDiseaseParser.class);

    private TSVReader reader;
    public GeneDiseaseParser(TSVReader reader){
        this.reader = reader;
    }

    /**
     * Parsed n records
     * @param total number of records to be parsed
     * @return
     */
    public List<GeneDiseaseAssociation> parseRecords(int total){
        LOGGER.debug("Starting the parsing of data");
        int count = 0;
        List<GeneDiseaseAssociation> parsedRecords = new ArrayList<>();
        while(this.reader.hasMoreRecord() && total > count){
            GeneDiseaseAssociation gdAssociation = parseRecord(this.reader.getRecord());
            parsedRecords.add(gdAssociation);
            count++;
        }

        LOGGER.debug("Total records parsed {}", parsedRecords.size());

        return parsedRecords;
    }

    public GeneDiseaseAssociation parseRecord(List<String> record){
        GeneDiseaseAssociation.GeneDiseaseAssociationBuilder builder = GeneDiseaseAssociation.builder();
        builder.geneId(record.get(0)).geneSymbol(record.get(1)).diseaseId(record.get(2)).diseaseName(record.get(3));
        builder.score(Double.valueOf(record.get(4))).pmidCount(Integer.valueOf(record.get(5)));
        GeneDiseaseAssociation gda = builder.snpCount(Integer.valueOf(record.get(6))).source(record.get(7)).build();
        return gda;
    }
}
