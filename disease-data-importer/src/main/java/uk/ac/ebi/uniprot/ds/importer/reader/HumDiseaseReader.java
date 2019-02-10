/*
 * Created by sahmad on 30/01/19 09:30
 * UniProt Consortium.
 * Copyright (c) 2002-2019.
 *
 */

package uk.ac.ebi.uniprot.ds.importer.reader;

import org.apache.commons.lang3.StringUtils;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import uk.ac.ebi.uniprot.ds.common.model.Disease;
import uk.ac.ebi.uniprot.ds.common.model.Synonym;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


public class HumDiseaseReader implements ItemReader<Disease> {
    private static Integer id = 0;
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
        reader = new Scanner(new File(fileName), StandardCharsets.UTF_8.name());
        dataRegionStarted = false;
    }

    @Override
    public Disease read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
        // skip the un-needed lines
        while (this.reader.hasNext() && !this.dataRegionStarted) {
            String lines = reader.next();
            if (DATA_REGION_SEP.equals(lines)) {
                this.dataRegionStarted = true;
                reader.useDelimiter(DATA_SEP);
            }
        }

        Disease disease = null;
        String lines = reader.next();
        if (!lines.contains(COPYRIGHT_SEP)) {
            disease = convertToDisease(lines);
        }
        return disease;
    }

    private Disease convertToDisease(String line) {
        Disease.DiseaseBuilder builder = Disease.builder();
        // split by new line
        String[] tokens = line.split(LINE_SEP);

        StringBuilder desc = new StringBuilder();
        List<Synonym> synonyms = new ArrayList<>();
        for (String token : tokens) {
            if (!StringUtils.isEmpty(token.trim())) {
                // split by 3 spaces
                String[] keyVal = token.split(KEY_VAL_SEP);
                switch (keyVal[0]) {
                    case ID_STR:
                        builder.name(keyVal[1].replace(FULL_STOP, EMPTY_STR));
                        break;
                    case AC_STR:
                        //upd.setAccession(keyVal[1]); TODO ignore accession of disease for now
                        break;
                    case AR_STR:
                        String acronym = keyVal[1].replace(FULL_STOP, EMPTY_STR);
                        builder.diseaseId(generateDiseaseId(acronym));
                        builder.acronym(acronym);
                        break;
                    case DE_STR:
                        desc.append(keyVal[1]);
                        break;
                    case SY_STR:
                        Synonym synonym = new Synonym(getSynonym(keyVal[1]));
                        synonyms.add(synonym);
                        break;
                    case DR_STR:
                        break;
                    case KW_STR:
                        break;
                    default://do nothing
                }

            }
        }

        builder.desc(desc.toString());
        Disease disease = builder.build();
        disease.setSynonyms(synonyms);
        synonyms.forEach(synonym -> synonym.setDisease(disease));

        return disease;
    }

    private String getSynonym(String name) {
        return name.replace(FULL_STOP, EMPTY_STR);
    }

    private String generateDiseaseId(String acronym) {
        return ++HumDiseaseReader.id + "-" + acronym;
    }
}
