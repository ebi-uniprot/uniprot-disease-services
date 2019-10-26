package uk.ac.ebi.uniprot.ds.importer.reader;

import org.springframework.batch.item.ItemReader;
import uk.ac.ebi.uniprot.ds.importer.reader.diseaseontology.OBOTerm;
import java.io.File;
import java.io.FileNotFoundException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Pattern;

public class MondoDiseaseReader implements ItemReader<OBOTerm> {

    // Constants
    private static final String TERM_STR = "[Term]";
    private static final String TYPEDEF_STR = "[Typedef]";
    private static final Pattern TERM_PATTERN = Pattern.compile("^\\s*$", Pattern.MULTILINE);
    private static final String NEW_LINE = "\n";
    private static final String COLON_SPACE = ": ";
    private static final String SPACE_EXCL = " !";
    private static final String ID = "id";
    private static final String NAME = "name";
    private static final String SYNONYM = "synonym";
    private static final String IS_A = "is_a";
    private static final String DEF = "def";
    private static final String ALT_ID = "alt_id";
    private static final String XREF = "xref";
    private static final String IS_OBSOLETE = "is_obsolete";

    private Scanner reader;
    private boolean termStarted;

    public MondoDiseaseReader(String fileName) throws FileNotFoundException {
        this.reader = new Scanner(new File(fileName), StandardCharsets.UTF_8.name());
        this.termStarted = false;
    }

    public OBOTerm read() {
        // skip the un-needed lines
        while (this.reader.hasNext() && !this.termStarted) {
            String lines = this.reader.nextLine();
            if (lines.trim().isEmpty()) {
                this.termStarted = true;
                this.reader.useDelimiter(TERM_PATTERN);
            }
        }

        OBOTerm oboTerm;
        // read until non-obsolete term found
        while ((oboTerm = readNextTerm()) != null && oboTerm.isObsolete()) ;
        return oboTerm;
    }

    private OBOTerm readNextTerm() {
        String termStr = null;

        if (this.reader.hasNext()) {
            termStr = this.reader.next();
        }

        if (termStr == null || termStr.trim().startsWith(TYPEDEF_STR)) {
            return null;
        }

        OBOTerm oboTerm = convertToOBOTerm(termStr);
        return oboTerm;
    }

    private OBOTerm convertToOBOTerm(String termStr) {

        OBOTerm.OBOTermBuilder builder = OBOTerm.builder();

        String[] lines = termStr.split(NEW_LINE);

        List<String> synonyms = new ArrayList<>();
        List<String> altIds = new ArrayList<>();
        List<String> xrefs = new ArrayList<>();
        List<String> parentIds = new ArrayList<>();

        for (String line : lines) {
            if (!(line.startsWith(TERM_STR) || line.trim().isEmpty())) {
                String[] lineTokens = line.split(COLON_SPACE);
                switch (lineTokens[0]) {
                    case ID:
                        builder.id(lineTokens[1].trim());
                        break;
                    case NAME:
                        builder.name(lineTokens[1].trim());
                        break;
                    case SYNONYM:
                        synonyms.add(lineTokens[1]);
                        break;
                    case IS_A:
                        parentIds.add(lineTokens[1].split(SPACE_EXCL)[0].split(" ")[0].trim());
                        break;
                    case DEF:
                        builder.definition(lineTokens[1].split("\" \\[")[0].substring(1));
                        break;
                    case ALT_ID:
                        altIds.add(lineTokens[1]);
                        break;
                    case XREF:
                        xrefs.add(lineTokens[1].split(" ")[0].trim());
                        break;
                    case IS_OBSOLETE:
                        builder.isObsolete(Boolean.valueOf(lineTokens[1]));
                        break;
                    default:
                        // do nothing
                }
            }
        }

        builder.isAs(parentIds);
        builder.synonyms(synonyms);
        builder.altIds(altIds);
        builder.xrefs(xrefs);

        return builder.build();
    }
}
