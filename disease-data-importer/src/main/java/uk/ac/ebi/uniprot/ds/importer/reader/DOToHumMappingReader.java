package uk.ac.ebi.uniprot.ds.importer.reader;

import org.apache.commons.lang3.tuple.Pair;
import org.springframework.batch.item.ItemReader;

import java.io.FileNotFoundException;
import java.util.List;

public class DOToHumMappingReader extends TSVReader implements ItemReader<Pair<String, String>> {

    public DOToHumMappingReader(String fileName) throws FileNotFoundException {
        super(fileName);
    }

    @Override
    public Pair<String, String> read() {
        List<String> record = getRecord();

        Pair<String, String> doHumTuple = null;

        if(record != null) {
            //<Disease Ontology Name, HumDisease Name>
            doHumTuple = Pair.of(record.get(0).trim().toLowerCase(), record.get(1).trim().toLowerCase());
        }

        return doHumTuple;
    }
}
