package uk.ac.ebi.uniprot.disease.service.tsv;

import uk.ac.ebi.uniprot.disease.model.DisGeNET.GeneDiseaseAssociation;
import uk.ac.ebi.uniprot.disease.model.DisGeNET.VariantDiseaseAssociation;

import java.util.ArrayList;
import java.util.List;

/**
 * @author sahmad
 */
public class VariantDiseaseParser {
    private TSVReader reader;
    public VariantDiseaseParser(TSVReader reader){
        this.reader = reader;
    }

    public List<VariantDiseaseAssociation> parseRecords(){
        List<VariantDiseaseAssociation> parsedRecords = new ArrayList<>();
        while(this.reader.hasMoreRecord()){
            VariantDiseaseAssociation vda = parseRecord(this.reader.getRecord());
            parsedRecords.add(vda);
        }

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
