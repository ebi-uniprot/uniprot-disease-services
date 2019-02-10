/*
 * Created by sahmad on 30/01/19 10:08
 * UniProt Consortium.
 * Copyright (c) 2002-2019.
 *
 */

package uk.ac.ebi.uniprot.ds.importer.writer;

import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import uk.ac.ebi.uniprot.ds.common.dao.DiseaseDAO;
import uk.ac.ebi.uniprot.ds.common.model.Disease;

import java.util.List;

public class HumDiseaseWriter implements ItemWriter<Disease> {
    @Autowired
    private DiseaseDAO diseaseDAO;
    @Override
    public void write(List<? extends Disease> items) throws Exception {
        this.diseaseDAO.saveAll((List<Disease>) items);
    }
}
