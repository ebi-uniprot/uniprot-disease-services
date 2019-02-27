package uk.ac.ebi.uniprot.ds.importer.writer;

import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import uk.ac.ebi.uniprot.ds.common.dao.DiseaseDAO;
import uk.ac.ebi.uniprot.ds.common.model.Disease;

import java.util.List;

public class DiseaseOntologyWriter implements ItemWriter<List<Disease>> {

    @Autowired
    private DiseaseDAO diseaseDAO;

    @Override
    public void write(List<? extends List<Disease>> items) throws Exception {
        this.diseaseDAO.saveAll(items.get(0));
    }
}
