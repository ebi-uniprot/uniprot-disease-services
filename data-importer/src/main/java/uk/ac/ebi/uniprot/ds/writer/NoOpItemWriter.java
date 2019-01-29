/*
 * Created by sahmad on 29/01/19 11:46
 * UniProt Consortium.
 * Copyright (c) 2002-2019.
 *
 */

package uk.ac.ebi.uniprot.ds.writer;

import org.springframework.batch.item.ItemWriter;
import uk.ac.ebi.kraken.interfaces.uniprot.UniProtEntry;

import java.util.List;

public class NoOpItemWriter implements ItemWriter<UniProtEntry> {

    @Override
    public void write(List<? extends UniProtEntry> list) throws Exception {
        // do nothing
        System.out.println(list);
    }
}
