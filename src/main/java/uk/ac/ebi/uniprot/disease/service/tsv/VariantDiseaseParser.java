package uk.ac.ebi.uniprot.disease.service.tsv;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.uniprot.disease.model.disgenet.VariantDiseaseAssociation;
import uk.ac.ebi.uniprot.disease.model.disgenet.VariantDiseasePMIDAssociation;

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

    public List<VariantDiseaseAssociation> parseVDARecords(int total){
        LOGGER.debug("Starting the parsing of data");
        List<VariantDiseaseAssociation> parsedRecords = new ArrayList<>();
        int count = 0;
        while(this.reader.hasMoreRecord() && total > count){
            List<String> record = this.reader.getRecord();
            try {
                VariantDiseaseAssociation vda = parseVDARecord(record);
                parsedRecords.add(vda);
                count++;
            }catch(Exception ex){
                LOGGER.error("Error is {}", ex.getLocalizedMessage());
                LOGGER.error("Failed record {}", record);
            }
        }
        LOGGER.debug("Total records parsed {}", parsedRecords.size());

        return parsedRecords;
    }

    public VariantDiseaseAssociation parseVDARecord(List<String> record){
        VariantDiseaseAssociation.VariantDiseaseAssociationBuilder builder = VariantDiseaseAssociation.builder();
        builder.snpId(record.get(0)).diseaseId(record.get(1)).diseaseName(record.get(2));
        builder.score(Double.valueOf(record.get(3))).pmidCount(Integer.valueOf(record.get(4)));
        VariantDiseaseAssociation vda = builder.source(record.get(5)).build();
        return vda;
    }

    public List<VariantDiseasePMIDAssociation> parseVDPARecords(int total){
        LOGGER.debug("Starting the parsing of data");
        List<VariantDiseasePMIDAssociation> parsedRecords = new ArrayList<>();
        int count = 0;
        while(this.reader.hasMoreRecord() && total > count){
            List<String> record = this.reader.getRecord();
            try {
                VariantDiseasePMIDAssociation vdpa = parseVDPARecord(record);
                parsedRecords.add(vdpa);
                count++;
            }catch(Exception ex){
                LOGGER.error("Error is", ex);
                LOGGER.error("Failed record {}", record);
            }
        }
        LOGGER.debug("Total records parsed {}", parsedRecords.size());

        return parsedRecords;
    }

    public VariantDiseasePMIDAssociation parseVDPARecord(List<String> record){
        //snpId   diseaseId       sentence        pmid    score
        // originalSource  diseaseName     diseaseType     chromosome      position
        VariantDiseasePMIDAssociation.VariantDiseasePMIDAssociationBuilder builder = VariantDiseasePMIDAssociation.builder();
        builder.snpId(record.get(0)).diseaseId(record.get(1)).sentence(record.get(2));
        try {
            builder.pmid(Long.parseLong(record.get(3)));
        } catch(NumberFormatException nfe){
            // do nothing
        }
        builder.score(Double.valueOf(record.get(4))).originalSource(record.get(5));
        builder.diseaseName(record.get(6)).diseaseType(record.get(7));
        try{
            builder.chromosome(Integer.parseInt(record.get(8)));
        }catch(NumberFormatException nfe){

        }
        try{
            builder.chromosomePosition(Long.parseLong(record.get(9)));
        }catch(NumberFormatException nfe){

        }
        VariantDiseasePMIDAssociation vdpa = builder.build();
        return vdpa;
    }
}
