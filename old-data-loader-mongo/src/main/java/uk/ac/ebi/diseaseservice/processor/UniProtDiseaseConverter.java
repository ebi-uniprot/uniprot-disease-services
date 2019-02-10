/*
 * Created by sahmad on 1/16/19 3:57 PM
 * UniProt Consortium.
 * Copyright (c) 2002-2019.
 *
 */

package uk.ac.ebi.diseaseservice.processor;

import org.springframework.batch.item.ItemProcessor;
import uk.ac.ebi.uniprot.disease.model.Disease;
import uk.ac.ebi.uniprot.disease.model.sources.uniprot.UniProtDisease;

public class UniProtDiseaseConverter implements ItemProcessor<UniProtDisease, Disease> {
    @Override
    public Disease process(UniProtDisease item) throws Exception {
        Disease.DiseaseBuilder builder = Disease.builder();
        builder.name(item.getIdentifier()).description(item.getDefinition());
        builder.acronym(item.getAcronym()).synonyms(item.getAlternativeNames());
        return builder.build();
    }
}
