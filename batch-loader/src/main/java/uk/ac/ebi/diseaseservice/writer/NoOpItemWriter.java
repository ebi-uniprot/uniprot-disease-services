/*
 * Created by sahmad on 1/16/19 10:56 AM
 * UniProt Consortium.
 * Copyright (c) 2002-2019.
 *
 */

package uk.ac.ebi.diseaseservice.writer;

import org.springframework.batch.item.ItemWriter;
import uk.ac.ebi.uniprot.disease.model.sources.uniprot.UniProtDisease;

import java.util.List;

public class NoOpItemWriter implements ItemWriter<UniProtDisease> {

    @Override
    public void write(List<? extends UniProtDisease> list) throws Exception {
        // do nothing
        //System.out.println(list);
    }
}
