/*
 * Created by sahmad on 30/01/19 09:30
 * UniProt Consortium.
 * Copyright (c) 2002-2019.
 *
 */

package uk.ac.ebi.uniprot.ds.reader;

import org.apache.commons.lang3.StringUtils;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import uk.ac.ebi.uniprot.disease.model.sources.uniprot.CrossRef;
import uk.ac.ebi.uniprot.disease.model.sources.uniprot.Keyword;
import uk.ac.ebi.uniprot.disease.model.sources.uniprot.UniProtDisease;
import uk.ac.ebi.uniprot.disease.utils.Constants;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


public class HumDiseaseReader implements ItemReader<UniProtDisease> {
    private Scanner reader;
    private boolean dataRegionStarted;
    private final static String DATA_REGION_SEP = "___________________________________________________________________________";
    private final static String COPYRIGHT_SEP = "-----------------------------------------------------------------------";
    private final static String DATA_SEP = "//";
    private final static String LINE_SEP = "\n";
    private final static String KEY_VAL_SEP = "   ";
    private final static String ID_STR = "ID";
    private final static String AC_STR = "AC";
    private final static String AR_STR = "AR";
    private final static String DE_STR = "DE";
    private final static String SY_STR = "SY";
    private final static String DR_STR = "DR";
    private final static String KW_STR = "KW";
    private final static String SEMI_COLON = ";";
    private final static String COLON = ":";
    private final static String FULL_STOP = ".";
    private final static String EMPTY_STR = "";


    public HumDiseaseReader(String fileName) throws FileNotFoundException {
        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource(fileName).getFile());
        reader = new Scanner(file, StandardCharsets.UTF_8.name());
        dataRegionStarted = false;
    }

    @Override
    public UniProtDisease read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
        // skip the un-needed lines
        while(this.reader.hasNext() && !this.dataRegionStarted) {
            String lines = reader.next();
            if(DATA_REGION_SEP.equals(lines)){
                this.dataRegionStarted = true;
                reader.useDelimiter(DATA_SEP);
            }
        }

        UniProtDisease disease = null;
        String lines = reader.next();
        if(!lines.contains(COPYRIGHT_SEP)) {
            disease = convertToUniProtDisease(lines);
        }
        return disease;
    }

    private UniProtDisease convertToUniProtDisease(String line) {
        UniProtDisease upd = new UniProtDisease();
        // split by new line
        String[] tokens = line.split(LINE_SEP);

        StringBuilder stringBuilder = new StringBuilder();
        List<String> synonyms = new ArrayList<>();
        for (String token : tokens) {
            if (!StringUtils.isEmpty(token.trim())) {
                // split by 3 spaces
                String[] keyVal = token.split(KEY_VAL_SEP);
                switch (keyVal[Constants.ZERO]) {
                    case ID_STR:
                        upd.setIdentifier(keyVal[1].replace(FULL_STOP, EMPTY_STR));
                        break;
                    case AC_STR:
                        upd.setAccession(keyVal[1]);
                        break;
                    case AR_STR:
                        upd.setAcronym(keyVal[1].replace(FULL_STOP, EMPTY_STR));
                        break;
                    case DE_STR:
                        stringBuilder.append(keyVal[1]);
                        break;
                    case SY_STR:
                        synonyms.add(getSynonym(keyVal[1]));
                        break;
                    case DR_STR:
                        break;
                    case KW_STR:
                        break;
                    default://do nothing
                }

            }
        }

        upd.setDefinition(stringBuilder.toString());

        if (!synonyms.isEmpty()) {
            upd.setAlternativeNames(synonyms);
        }

        return upd;
    }

    private Keyword getKeyword(String val) {
        String[] tokens = val.split(COLON);
        Keyword kw = new Keyword();
        kw.setKeyId(tokens[0].trim());
        kw.setKeyValue(tokens[1].trim().replace(FULL_STOP, EMPTY_STR));
        return kw;
    }

    private CrossRef getCrossRef(String val) {
        String[] tokens = val.split(SEMI_COLON);
        CrossRef cr = new CrossRef();
        cr.setRefType(tokens[0].trim());
        cr.setRefId(tokens[1].trim().replace(FULL_STOP, EMPTY_STR));
        if (tokens.length == 3) {
            cr.setRefMeta(tokens[2].trim().replace(FULL_STOP, EMPTY_STR));
        }
        return cr;
    }

    private String getSynonym(String name) {
        return name.replace(FULL_STOP, EMPTY_STR);
    }
}
