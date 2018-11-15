package uk.ac.ebi.uniprot.disease.service.tsv;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.uniprot.disease.model.disgenet.VariantDiseaseAssociation;

import java.util.ArrayList;
import java.util.List;

/**
 * @author sahmad
 */
public class VariantDiseaseParser {
    private static final Logger LOGGER = LoggerFactory.getLogger(VariantDiseaseParser.class);
    private TSVReader reader;
    public VariantDiseaseParser(TSVReader reader){
        this.reader = reader;
    }

    public List<VariantDiseaseAssociation> parseRecords(int total){
        LOGGER.debug("Starting the parsing of data");
        List<VariantDiseaseAssociation> parsedRecords = new ArrayList<>();
        int count = 0;
        while(this.reader.hasMoreRecord() && total > count){
            VariantDiseaseAssociation vda = parseRecord(this.reader.getRecord());
            parsedRecords.add(vda);
            count++;
        }
        LOGGER.debug("Total records parsed {}", parsedRecords.size());

        return parsedRecords;
    }

    public VariantDiseaseAssociation parseRecord(List<String> record){
        VariantDiseaseAssociation.VariantDiseaseAssociationBuilder builder = VariantDiseaseAssociation.builder();
        builder.snpId(record.get(0)).diseaseId(record.get(1)).diseaseName(record.get(2));
        builder.score(Double.valueOf(record.get(3))).pmidCount(Integer.valueOf(record.get(4)));
        VariantDiseaseAssociation vda = builder.source(record.get(5)).build();
        return vda;
    }
}
