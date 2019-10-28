package uk.ac.ebi.uniprot.ds.importer.writer;

import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemStream;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;
import uk.ac.ebi.uniprot.ds.importer.model.DiseaseRelationDTO;

import java.util.ArrayList;
import java.util.List;

public class ListJDBCBatchItemWriter implements ItemWriter<List<DiseaseRelationDTO>>,
        ItemStream, InitializingBean {

    private ItemWriter<DiseaseRelationDTO> delegate;

    @Override
    public void write(final List<? extends List<DiseaseRelationDTO>> lists) throws Exception {
        final List<DiseaseRelationDTO> consolidatedList = new ArrayList<>();
        for (final List<DiseaseRelationDTO> list : lists) {
            consolidatedList.addAll(list);
        }

        this.delegate.write(consolidatedList);
    }

    @Override
    public void afterPropertiesSet() {
        Assert.notNull(this.delegate, "You must set a delegate!");
    }

    @Override
    public void open(ExecutionContext executionContext) {
        if (this.delegate instanceof ItemStream) {
            ((ItemStream) this.delegate).open(executionContext);
        }
    }

    @Override
    public void update(ExecutionContext executionContext) {
        if (this.delegate instanceof ItemStream) {
            ((ItemStream) this.delegate).update(executionContext);
        }
    }

    @Override
    public void close() {
        if (this.delegate instanceof ItemStream) {
            ((ItemStream) delegate).close();
        }
    }

    public void setDelegate(ItemWriter<DiseaseRelationDTO> delegate) {
        this.delegate = delegate;
    }
}
