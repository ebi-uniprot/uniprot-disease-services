package uk.ac.ebi.uniprot.ds.importer.reader;

import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ParseException;
import uk.ac.ebi.uniprot.ds.common.model.SiteMapping;

import java.io.FileNotFoundException;
import java.util.List;
import java.util.Objects;

/**
 * @author lgonzales
 * @since 09/09/2020
 */
public class SiteMappingReader extends TSVReader implements ItemReader<SiteMapping> {

    public static final String PROTEIN_DATA_SPLIT_REGEX = "\\||:";

    public SiteMappingReader(String fileName) throws FileNotFoundException {
       super(fileName, false);
    }

    @Override
    public SiteMapping read() {
        List<String> record =  getRecord();
        if(Objects.nonNull(record) && !record.isEmpty()){
            SiteMapping.SiteMappingBuilder builder = SiteMapping.builder();
            if(record.size() == 4){ // site type can be null in input file
                parseProteinData(record.get(0), builder);
                builder.positionInAlignment(Long.valueOf(record.get(1)));
                builder.unirefId(record.get(2));
                builder.mappedSite(record.get(3));
            } else if(record.size() == 5){
                builder.siteType(record.get(0));
                parseProteinData(record.get(1), builder);
                builder.positionInAlignment(Long.valueOf(record.get(2)));
                builder.unirefId(record.get(3));
                builder.mappedSite(record.get(4));
            } else {
                throw new IllegalArgumentException("Illegal record " + record);
            }
            return builder.build();
        } else {
            return null;
        }
    }

    private void parseProteinData(String proteinData, SiteMapping.SiteMappingBuilder builder) {
        String[] proteinInfo = proteinData.split(PROTEIN_DATA_SPLIT_REGEX);
        if(proteinInfo.length == 3) {
            builder.accession(proteinInfo[0]);
            builder.proteinId(proteinInfo[1]);
            builder.sitePosition(Long.valueOf(proteinInfo[2]));
        } else {
            throw new ParseException("Unable to parse protein info '"+proteinData+"'");
        }
    }

}
