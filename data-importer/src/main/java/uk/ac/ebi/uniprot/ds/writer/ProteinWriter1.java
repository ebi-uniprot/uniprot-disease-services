/*
 * Created by sahmad on 31/01/19 23:00
 * UniProt Consortium.
 * Copyright (c) 2002-2019.
 *
 */

package uk.ac.ebi.uniprot.ds.writer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import uk.ac.ebi.kraken.interfaces.uniprot.Gene;
import uk.ac.ebi.kraken.interfaces.uniprot.ProteinDescription;
import uk.ac.ebi.kraken.interfaces.uniprot.UniProtEntry;
import uk.ac.ebi.kraken.interfaces.uniprot.description.FieldType;
import uk.ac.ebi.kraken.interfaces.uniprot.description.Name;
import uk.ac.ebi.uniprot.ds.model.Protein;
import uk.ac.ebi.uniprot.ds.service.ProteinService;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ProteinWriter1 implements ItemWriter<UniProtEntry> {
    private static final Logger logger = LoggerFactory.getLogger(ProteinWriter1.class);

    @Autowired
    private ProteinService proteinService;
    private List<Protein> proteins;

    public ProteinWriter1(List<Protein> proteins){
        this.proteins = proteins;
    }

    @Override
    public void write(List<? extends UniProtEntry> entries) throws Exception {
        this.proteinService.saveAll(this.proteins);
        this.proteins.clear();
        int i = 0;
    }
}
