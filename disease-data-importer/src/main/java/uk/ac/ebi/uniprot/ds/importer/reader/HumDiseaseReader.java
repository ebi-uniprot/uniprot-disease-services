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
import uk.ac.ebi.uniprot.ds.common.model.CrossRef;
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
    private static final String DATA_REGION_SEP = "___________________________________________________________________________";
    private static final String COPYRIGHT_SEP = "-----------------------------------------------------------------------";
    private static final String DATA_SEP = "//";
    private static final String LINE_SEP = "\n";
    private static final String KEY_VAL_SEP = "   ";
    private static final String ID_STR = "ID";
    private static final String AC_STR = "AC";
    private static final String AR_STR = "AR";
    private static final String DE_STR = "DE";
    private static final String SY_STR = "SY";
    private static final String DR_STR = "DR";
    private static final String KW_STR = "KW";
    private static final String FULL_STOP = ".";
    private static final String EMPTY_STR = "";
    private static final String SEMI_COLON =";";

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
        List<CrossRef> crossRefs = new ArrayList<>();
        for (String token : tokens) {
            if (!StringUtils.isEmpty(token.trim())) {
                // split by 3 spaces
                String[] keyVal = token.split(KEY_VAL_SEP);
                switch (keyVal[0]) {
                    case ID_STR:
                        builder.name(keyVal[1].replace(FULL_STOP, EMPTY_STR));
                        break;
                    case AC_STR:
                        //upd.setAccession(keyVal[1]); ignore accession of disease for now
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
                        CrossRef xRef = getCrossRef(keyVal[1]);
                        if(xRef != null) {
                            crossRefs.add(xRef);
                        }
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
        disease.setCrossRefs(crossRefs);
        crossRefs.forEach(crossRef -> crossRef.setDisease(disease));
        return disease;
    }

    private String getSynonym(String name) {
        return name.replace(FULL_STOP, EMPTY_STR);
    }

    private static String generateDiseaseId(String acronym) {
        return ++HumDiseaseReader.id + "-" + acronym;
    }

    private CrossRef getCrossRef(String diseaseRef) {
        String[] tokens = diseaseRef.split(SEMI_COLON);
        CrossRef cr = null;
        if(tokens.length >= 2) {
            cr = new CrossRef();
            cr.setRefType(tokens[0].trim());
            cr.setRefId(tokens[1].trim().replace(FULL_STOP, EMPTY_STR));
        }
        return cr;
    }
}
