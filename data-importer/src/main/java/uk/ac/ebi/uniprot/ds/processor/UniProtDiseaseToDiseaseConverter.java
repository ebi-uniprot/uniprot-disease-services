/*
 * Created by sahmad on 30/01/19 09:40
 * UniProt Consortium.
 * Copyright (c) 2002-2019.
 *
 */

package uk.ac.ebi.uniprot.ds.processor;

import org.springframework.batch.item.ItemProcessor;
import uk.ac.ebi.uniprot.disease.model.sources.uniprot.UniProtDisease;
import uk.ac.ebi.uniprot.ds.model.Disease;
import uk.ac.ebi.uniprot.ds.model.Synonym;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class UniProtDiseaseToDiseaseConverter implements ItemProcessor<UniProtDisease, Disease> {
    private static Integer id = 0;
    @Override
    public Disease process(UniProtDisease item) throws Exception {
        Disease.DiseaseBuilder builder = Disease.builder();
        builder.diseaseId(generateDiseaseId(item.getAcronym()));
        builder.name(item.getIdentifier()).desc(item.getDefinition());
        builder.acronym(item.getAcronym()).synonyms(new ArrayList<>());
        Disease disease = builder.build();

        if(item.getAlternativeNames() != null && !item.getAlternativeNames().isEmpty()) {
            addSynonyms(item.getAlternativeNames(), disease);

        }

        return disease;
    }

    private void addSynonyms(List<String> altNames, Disease disease) {

        for (String name: altNames) {
            Synonym synonym = Synonym.builder().name(name).build();
            disease.addSynonym(synonym);
        }
    }

    private String generateDiseaseId(String acronym) {
        return ++id + "-" + acronym;
    }
}
