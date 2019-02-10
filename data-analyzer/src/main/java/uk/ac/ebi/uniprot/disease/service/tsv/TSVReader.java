package uk.ac.ebi.uniprot.disease.service.tsv;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.uniprot.disease.utils.Constants;

import java.io.Closeable;
import java.io.File;
import java.io.FileNotFoundException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * @author sahmad
 */
public class TSVReader implements Closeable {
    private static final Logger LOGGER = LoggerFactory.getLogger(TSVReader.class);

    private Scanner tsvReader;
    private String peekRecord;
    private boolean isHeader = true; // flag to skip the first line/header in tsv file
    private long recordCount = 0;


    public TSVReader(String fileName) throws FileNotFoundException {
        this.tsvReader = new Scanner(new File(fileName), StandardCharsets.UTF_8.name());
        this.peekRecord = null;
    }

    public boolean hasMoreRecord() {
        if(this.peekRecord != null){
            return true;
        }
        if(!this.tsvReader.hasNextLine()){
            return false;
        }

        String nextRecord = this.tsvReader.nextLine().trim();

        if(nextRecord.isEmpty() || this.isHeader){ // skip the header(first line) and empty line
            this.isHeader = false;
            return hasMoreRecord();
        }

        this.peekRecord = nextRecord;
        return true;
    }

    public List<String> getRecord(){
        if(hasMoreRecord()){
            recordCount++;
            String[] tokensArray = this.peekRecord.split(Constants.TAB);
            List<String> tokens = getTokens(tokensArray);
            this.peekRecord = null;
            return tokens;
        } else {
            return new ArrayList<>();
        }
    }

    public long getRecordCount(){
        return this.recordCount;
    }

    public void close() {
        this.tsvReader.close() ;
    }

    private List<String> getTokens(String[] tokensArray) {

        List<String> tokens = new ArrayList<>();
        for(String token: tokensArray){
            tokens.add(token);
        }

        return tokens;
    }

    public List<List<String>> getRecords(int recordCount) {
        int count = 0;
        List<List<String>> records = new ArrayList<>();
        while (this.hasMoreRecord() && count < recordCount) {
            List<String> record = this.getRecord();
            records.add(record);
            count++;
        }

        return records;
    }
}
