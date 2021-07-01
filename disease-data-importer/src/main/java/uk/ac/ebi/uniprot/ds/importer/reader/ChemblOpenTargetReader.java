package uk.ac.ebi.uniprot.ds.importer.reader;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.batch.item.ItemReader;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import uk.ac.ebi.uniprot.ds.importer.model.ChemblEntry;

public class ChemblOpenTargetReader implements ItemReader<ChemblEntry> {
    private ObjectMapper objectMapper;
    private JsonParser jsonParser;
    private Set<ChemblEntry> processedChemblEntries;

    public ChemblOpenTargetReader(String filePath) throws IOException {
        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream(filePath);

        if (inputStream == null) {
            inputStream = new FileInputStream(filePath);
        }

        JsonFactory jsonFactory = new JsonFactory();

        this.jsonParser = jsonFactory.createParser(inputStream);
        this.objectMapper = new ObjectMapper();
        this.processedChemblEntries = new HashSet<>();
    }

    @Override
    public ChemblEntry read() throws Exception {
        ChemblEntry chemblEntry;
        JsonToken token;
        do {
            token = this.jsonParser.nextToken();
            chemblEntry = getChemblEntry(token);
        } while (Objects.nonNull(token) && this.processedChemblEntries.contains(chemblEntry));

        if (Objects.nonNull(chemblEntry)) { // last object will have null chemblEntry
            this.processedChemblEntries.add(chemblEntry);
        }
        return chemblEntry;
    }

    private ChemblEntry getChemblEntry(JsonToken token) throws IOException {
        if (Objects.nonNull(token)) {
            Map<String, Object> openTargetObj = this.objectMapper.readValue(this.jsonParser, Map.class);
            ChemblEntry.ChemblEntryBuilder builder = ChemblEntry.builder();
            builder.chemblId((String) openTargetObj.get("drugId"));
            builder.status((String) openTargetObj.get("clinicalStatus"));
            String diseaseId = (String) openTargetObj.get("diseaseFromSourceMappedId");
            builder.diseaseUrl(Objects.nonNull(diseaseId) ? ChemblEntry.convertToUrl(diseaseId) : null);
            builder.targetChemblId((String) openTargetObj.get("targetFromSource"));
            builder.phase((Integer) openTargetObj.get("clinicalPhase"));
            String niceName = ((List<Map<String, String>>) openTargetObj.get("urls")).get(0).get("niceName");
            if ("ClinicalTrials".equalsIgnoreCase(niceName)) {
                String url = ((List<Map<String, String>>) openTargetObj.get("urls")).get(0).get("url");
                builder.clinicalTrialLink(url);
            }
            return builder.build();
        }
        return null;
    }
}
