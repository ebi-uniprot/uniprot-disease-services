package uk.ac.ebi.uniprot.ds.importer.writer;

import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemStream;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;

public class ListJDBCBatchItemWriter<T> implements ItemWriter<List<T>>,
        ItemStream, InitializingBean {

    private ItemWriter<T> delegate;

    @Override
    public void write(final List<? extends List<T>> lists) throws Exception {
        final List<T> consolidatedList = new ArrayList<>();
        for (final List<T> list : lists) {
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

    public void setDelegate(ItemWriter<T> delegate) {
        this.delegate = delegate;
    }
}
