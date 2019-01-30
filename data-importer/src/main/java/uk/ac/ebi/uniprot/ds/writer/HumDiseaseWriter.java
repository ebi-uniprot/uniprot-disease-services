/*
 * Created by sahmad on 30/01/19 10:08
 * UniProt Consortium.
 * Copyright (c) 2002-2019.
 *
 */

package uk.ac.ebi.uniprot.ds.writer;

import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import uk.ac.ebi.uniprot.ds.model.Disease;
import uk.ac.ebi.uniprot.ds.service.DiseaseService;

import java.util.List;

public class HumDiseaseWriter implements ItemWriter<Disease> {
    @Autowired
    private DiseaseService diseaseService;
    @Override
    public void write(List<? extends Disease> items) throws Exception {
        this.diseaseService.saveAll((List<Disease>) items);
    }
}
