package uk.ac.ebi.uniprot.ds.importer.reader;

import org.springframework.batch.item.ItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import uk.ac.ebi.uniprot.ds.common.dao.ProteinCrossRefDAO;
import uk.ac.ebi.uniprot.ds.common.model.ProteinCrossRef;
import uk.ac.ebi.uniprot.ds.importer.util.Constants;

import java.util.Iterator;
import java.util.List;

public class ChEMBLDrugReader implements ItemReader<ProteinCrossRef> {
    @Autowired
    private ProteinCrossRefDAO crossRefDAO;

    private List<ProteinCrossRef> chEMBLRefs;
    private Iterator<ProteinCrossRef> iterator;

    @Override
    public ProteinCrossRef read() {

        init();// only first time

        ProteinCrossRef crossRef = null;

        if(this.iterator.hasNext()){
            crossRef = this.iterator.next();
        }
        return crossRef;
    }

    private void init(){
        loadAllChEMBLRefs();
        setIterator();

    }

    private void loadAllChEMBLRefs() {
        if(this.chEMBLRefs == null){
            this.chEMBLRefs = this.crossRefDAO.findAllByDbType(Constants.ChEMBL_STR);
        }
    }

    private void setIterator() {
        if(this.iterator == null){
            // set iterator
            this.iterator = this.chEMBLRefs.iterator();
        }
    }
}
