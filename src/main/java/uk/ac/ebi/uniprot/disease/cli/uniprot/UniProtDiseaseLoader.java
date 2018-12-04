package uk.ac.ebi.uniprot.disease.cli.uniprot;

import uk.ac.ebi.uniprot.disease.cli.common.DiseaseDataLoaderArgs;
import uk.ac.ebi.uniprot.disease.cli.common.MainHelper;
import uk.ac.ebi.uniprot.disease.model.uniprot.AlternativeName;
import uk.ac.ebi.uniprot.disease.model.uniprot.CrossRef;
import uk.ac.ebi.uniprot.disease.model.uniprot.Keyword;
import uk.ac.ebi.uniprot.disease.model.uniprot.UniProtDisease;
import uk.ac.ebi.uniprot.disease.pipeline.processor.uniprot.UniProtDataSaver;
import uk.ac.ebi.uniprot.disease.pipeline.processor.uniprot.UniProtDiseaseParser;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class UniProtDiseaseLoader {
    private static final Integer BATCH_SIZE = 200;

    public static void main(String[] args) throws IOException, SQLException {
        // load the db connection info
        DiseaseDataLoaderArgs options = new DiseaseDataLoaderArgs();
        MainHelper.fillDBParams(options, MainHelper.DEFAULT_DB_CONNECTION_PROP);

        // parse the file
        UniProtDiseaseParser parser = new UniProtDiseaseParser("src/main/data.uniprot/humdisease.txt");

        // save the data
        storeData(parser, options.getDbUser(), options.getDbPassword(), options.getJdbcUrl());

    }

    private static void storeData(UniProtDiseaseParser parser, String dbUser, String dbPass, String jdbcUrl) throws SQLException {
        UniProtDataSaver saver = new UniProtDataSaver(dbUser, dbPass, jdbcUrl);

        for(List<UniProtDisease> diseases = parser.getUniProtDiseases(BATCH_SIZE);
            !diseases.isEmpty(); diseases = parser.getUniProtDiseases(BATCH_SIZE)) {

            storeData(saver, diseases);
        }
    }

    private static void storeData(UniProtDataSaver saver, List<UniProtDisease> diseases) throws SQLException {
        for (UniProtDisease disease : diseases) {

            Integer diseaseId = saver.createDisease(disease.getIdentifier(), disease.getAcronym(), disease.getAccession(),
                    disease.getDefinition());
            storeCrossRefs(saver, diseaseId, disease.getCrossRefs());
            storeSynonyms(saver, diseaseId, disease.getSynonyms());
            storeKeywords(saver, diseaseId, disease.getKeywords());
        }
    }

    private static void storeKeywords(UniProtDataSaver saver, Integer diseaseId, List<Keyword> keywords) throws SQLException {
        if(keywords != null){
            for(Keyword keyword:keywords){
                saver.createKeyword(keyword.getKeyId(), keyword.getKeyValue(), diseaseId);
            }
        }
    }

    private static void storeSynonyms(UniProtDataSaver saver, Integer diseaseId, List<AlternativeName> synonyms) throws SQLException {
        if(synonyms != null){
            for(AlternativeName synonym:synonyms){
                saver.createSynonym(synonym.getName(), diseaseId);
            }

        }
    }

    private static void storeCrossRefs(UniProtDataSaver saver, Integer diseaseId, List<CrossRef> crossRefs) throws SQLException {
        if(crossRefs != null){
            for(CrossRef crossRef : crossRefs){
                saver.createCrossRef(crossRef.getRefType(), crossRef.getRefId(), crossRef.getRefMeta(), diseaseId);
            }
        }
    }
}
