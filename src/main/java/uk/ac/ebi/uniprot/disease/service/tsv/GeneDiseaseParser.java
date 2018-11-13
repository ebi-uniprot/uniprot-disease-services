package uk.ac.ebi.uniprot.disease.service.tsv;

import uk.ac.ebi.uniprot.disease.model.DisGeNET.GeneDiseaseAssociation;

import java.util.ArrayList;
import java.util.List;

/**
 * @author sahmad
 */
public class GeneDiseaseParser {
    private TSVReader reader;
    public GeneDiseaseParser(TSVReader reader){
        this.reader = reader;
    }

    public List<GeneDiseaseAssociation> parseRecords(){
        List<GeneDiseaseAssociation> parsedRecords = new ArrayList<>();
        while(this.reader.hasMoreRecord()){
            GeneDiseaseAssociation gdAssociation = parseRecord(this.reader.getRecord());
            parsedRecords.add(gdAssociation);
        }

        return parsedRecords;
    }

    public GeneDiseaseAssociation parseRecord(List<String> record){
        GeneDiseaseAssociation.GeneDiseaseAssociationBuilder builder = GeneDiseaseAssociation.builder();
        builder.geneId(record.get(0)).geneSymbol(record.get(1)).diseaseId(record.get(2)).diseaseName(record.get(3));
        builder.score(Double.valueOf(record.get(4))).pmidCount(Integer.valueOf(record.get(5)));
        GeneDiseaseAssociation gdAssociation = builder.snpCount(Integer.valueOf(record.get(6))).source(record.get(7)).build();
        return gdAssociation;
    }
}
