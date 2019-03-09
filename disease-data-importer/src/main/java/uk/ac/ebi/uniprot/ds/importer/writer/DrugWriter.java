package uk.ac.ebi.uniprot.ds.importer.writer;

import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import uk.ac.ebi.uniprot.ds.common.dao.DrugDAO;
import uk.ac.ebi.uniprot.ds.common.model.Drug;

import java.util.List;
import java.util.stream.Collectors;

public class DrugWriter implements ItemWriter<List<Drug>> {
    @Autowired
    private DrugDAO drugDAO;

    @Override
    public void write(List<? extends List<Drug>> items)  {
        List<Drug> drugs = items.stream().flatMap(List::stream).collect(Collectors.toList());
        this.drugDAO.saveAll(drugs);
    }
}