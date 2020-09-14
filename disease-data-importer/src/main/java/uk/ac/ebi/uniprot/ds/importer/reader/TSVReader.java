package uk.ac.ebi.uniprot.ds.importer.reader;

import lombok.extern.slf4j.Slf4j;
import uk.ac.ebi.uniprot.ds.importer.util.Constants;

import java.io.Closeable;
import java.io.File;
import java.io.FileNotFoundException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

/**
 * @author sahmad
 */
@Slf4j
public abstract class TSVReader implements Closeable {
    private final Scanner tsvReader;
    private String peekRecord;
    private boolean isHeader; // flag to skip the first line/header in tsv file

    public TSVReader(String fileName, boolean readFirstLine) throws FileNotFoundException {
        this.tsvReader = new Scanner(new File(fileName), StandardCharsets.UTF_8.name());
        this.isHeader = readFirstLine;
        this.peekRecord = null;
    }

    public List<String> getRecord() {
        if (hasMoreRecord()) {
            String[] tokensArray = this.peekRecord.split(Constants.TAB);
            List<String> tokens = getTokens(tokensArray);
            this.peekRecord = null;
            return tokens;
        } else {
            return Collections.emptyList();
        }
    }

    public void close() {
        this.tsvReader.close();
    }

    private boolean hasMoreRecord() {
        if (this.peekRecord != null) {
            return Boolean.TRUE;
        }
        if (!this.tsvReader.hasNextLine()) {
            return Boolean.FALSE;
        }

        String nextRecord = this.tsvReader.nextLine().trim();

        if (nextRecord.isEmpty() || this.isHeader) { // skip the header(first line) and empty line
            this.isHeader = Boolean.FALSE;
            return hasMoreRecord();
        }

        this.peekRecord = nextRecord;
        return Boolean.TRUE;
    }


    private List<String> getTokens(String[] tokensArray) {
        List<String> tokens = new ArrayList<>();
        Collections.addAll(tokens, tokensArray);
        return tokens;
    }
}
