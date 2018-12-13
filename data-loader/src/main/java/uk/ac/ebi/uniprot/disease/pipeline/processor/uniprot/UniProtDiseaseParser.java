package uk.ac.ebi.uniprot.disease.pipeline.processor.uniprot;

import org.apache.commons.lang3.StringUtils;
import uk.ac.ebi.uniprot.disease.model.sources.uniprot.AlternativeName;
import uk.ac.ebi.uniprot.disease.model.sources.uniprot.CrossRef;
import uk.ac.ebi.uniprot.disease.model.sources.uniprot.Keyword;
import uk.ac.ebi.uniprot.disease.model.sources.uniprot.UniProtDisease;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class UniProtDiseaseParser {
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


    public UniProtDiseaseParser(String fileName) throws FileNotFoundException {
        reader = new Scanner(new File(fileName), StandardCharsets.UTF_8.name());
        dataRegionStarted = false;
    }

    public List<UniProtDisease> getUniProtDiseases(int total){
        int count = 0;
        List<UniProtDisease> diseases = new ArrayList<>();
        while(this.reader.hasNext() && count < total){
            String lines = reader.next();
            if(this.dataRegionStarted){
                if(lines.contains(COPYRIGHT_SEP)){
                    this.dataRegionStarted = false;
                } else {
                    diseases.add(convertToUniProtDiseas(lines));
                    count++;
                }

            } else if(DATA_REGION_SEP.equals(lines)){
                this.dataRegionStarted = true;
                reader.useDelimiter(DATA_SEP);
            }
        }

        return diseases;
    }

    private UniProtDisease convertToUniProtDiseas(String line) {
        UniProtDisease upd = new UniProtDisease();
        // split by new line
        String[] tokens = line.split(LINE_SEP);

        StringBuilder stringBuilder = new StringBuilder();
        List<AlternativeName> synonyms = new ArrayList<>();
        List<CrossRef> crossRefs = new ArrayList<>();
        List<Keyword> keyWords = new ArrayList<>();

        for(String token : tokens){
            if(!StringUtils.isEmpty(token.trim())){
                // split by 3 spaces
                String[] keyVal = token.split(KEY_VAL_SEP);
                switch(keyVal[0]){
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
                        crossRefs.add(getCrossRef(keyVal[1]));
                        break;
                    case KW_STR:
                        keyWords.add(getKeyword(keyVal[1]));
                        break;
                }

            }
        }

        upd.setDefinition(stringBuilder.toString());
        if(!synonyms.isEmpty()){
            upd.setSynonyms(synonyms);
        }
        if(!crossRefs.isEmpty()){
            upd.setCrossRefs(crossRefs);
        }

        if(!keyWords.isEmpty()){
            upd.setKeywords(keyWords);
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
        if(tokens.length == 3){
            cr.setRefMeta(tokens[2].trim().replace(FULL_STOP, EMPTY_STR));
        }
        return cr;
    }

    private AlternativeName getSynonym(String name) {
        AlternativeName synonym = new AlternativeName();
        synonym.setName(name.replace(FULL_STOP, EMPTY_STR));
        return synonym;
    }
}
