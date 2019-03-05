package uk.ac.ebi.uniprot.ds.importer.writer;

import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import uk.ac.ebi.uniprot.ds.common.dao.SynonymDAO;
import uk.ac.ebi.uniprot.ds.common.model.Synonym;

import java.util.List;

public class DOSynonymWriter implements ItemWriter<Synonym> {

    @Autowired
    private SynonymDAO synonymDAO;
    @Override
    public void write(List<? extends Synonym> items){
        this.synonymDAO.saveAll(items);
    }
}
