package uk.ac.ebi.uniprot.disease.service.tsv;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.uniprot.disease.model.disgenet.GeneDiseaseAssociation;
import uk.ac.ebi.uniprot.disease.model.disgenet.GeneDiseasePMIDAssociation;

import java.util.ArrayList;
import java.util.List;

/**
 * Class responsible for parsing the GDA and GDPA data
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
    public List<GeneDiseaseAssociation> parseGDARecords(int total){
        //LOGGER.debug("Starting the parsing of data");
        int count = 0;
        List<GeneDiseaseAssociation> parsedRecords = new ArrayList<>();
        while(this.reader.hasMoreRecord() && total > count){
            List<String> record = this.reader.getRecord();
            try {
                GeneDiseaseAssociation gdAssociation = parseGDARecord(record);
                parsedRecords.add(gdAssociation);
                count++;
            }catch(Exception ex){
                LOGGER.error("Error is {}", ex.getLocalizedMessage());
                LOGGER.error("Failed record {}", record);
            }
        }

        //LOGGER.debug("Total records parsed {}", parsedRecords.size());

        return parsedRecords;
    }

    public GeneDiseaseAssociation parseGDARecord(List<String> record){
        GeneDiseaseAssociation.GeneDiseaseAssociationBuilder builder = GeneDiseaseAssociation.builder();
        builder.geneId(Long.parseLong(record.get(0))).geneSymbol(record.get(1)).diseaseId(record.get(2)).diseaseName(record.get(3));
        builder.score(Double.valueOf(record.get(4))).pmidCount(Integer.valueOf(record.get(5)));
        GeneDiseaseAssociation gda = builder.snpCount(Integer.valueOf(record.get(6))).source(record.get(7)).build();
        return gda;
    }

    /**
     * Parsed n records from DGPA
     * @param total number of records to be parsed
     * @return
     */
    public List<GeneDiseasePMIDAssociation> parseGDPARecords(int total){
        //LOGGER.debug("Starting the parsing of data");
        int count = 0;
        List<GeneDiseasePMIDAssociation> parsedRecords = new ArrayList<>();
        while(this.reader.hasMoreRecord() && total > count){
            List<String> record = this.reader.getRecord();
            try {
                GeneDiseasePMIDAssociation gdpa = parseGDPARecord(record);
                parsedRecords.add(gdpa);
                count++;
            } catch(Exception ex){
                LOGGER.error("Error is {}", ex.getLocalizedMessage());
                LOGGER.error("Failed record {}", record);
            }
        }

        //LOGGER.debug("Total records parsed {}", parsedRecords.size());

        return parsedRecords;
    }

    /**
     * Parse the gene disease pmid data
     * @param record
     * @return
     */
    public GeneDiseasePMIDAssociation parseGDPARecord(List<String> record){
        GeneDiseasePMIDAssociation.GeneDiseasePMIDAssociationBuilder builder = GeneDiseasePMIDAssociation.builder();
        builder.geneId(Long.parseLong(record.get(0))).diseaseId(record.get(1)).associationType(record.get(2));
        builder.sentence(record.get(3));
        try {
            builder.pmid(Long.parseLong(record.get(4)));
        } catch(NumberFormatException nfe){
            // do nothing
        }
        builder.score(Double.valueOf(record.get(5)));
        builder.source(record.get(6)).diseaseName(record.get(7)).diseaseType(record.get(8)).geneSymbol(record.get(9));

        GeneDiseasePMIDAssociation gdpa = builder.build();
        return gdpa;
    }
}
