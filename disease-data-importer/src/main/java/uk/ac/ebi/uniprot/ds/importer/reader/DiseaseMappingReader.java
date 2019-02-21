package uk.ac.ebi.uniprot.ds.importer.reader;

import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import uk.ac.ebi.uniprot.ds.importer.model.DiseaseMapping;

import java.io.FileNotFoundException;
import java.util.List;

public class DiseaseMappingReader extends TSVReader implements ItemReader<DiseaseMapping>  {
    //TODO add filter predicate

    public DiseaseMappingReader(String fileName) throws FileNotFoundException {
        super(fileName);
    }

    @Override
    public DiseaseMapping read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
        List<String> row = getRecord();
        DiseaseMapping dm = null;
        if(row != null){
            dm = convertToDiseaseMapping(row);
        }
        return dm;
    }

    private DiseaseMapping convertToDiseaseMapping(List<String> row) {
        DiseaseMapping.DiseaseMappingBuilder bldr = DiseaseMapping.builder();
        bldr.diseaseId(row.get(0)).name(row.get(1)).vocab(row.get(2));
        bldr.code(row.get(3)).vocabName(row.get(4));
        return bldr.build();
    }
}
