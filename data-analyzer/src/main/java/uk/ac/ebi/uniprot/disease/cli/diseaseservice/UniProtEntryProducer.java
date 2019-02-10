package uk.ac.ebi.uniprot.disease.cli.diseaseservice;

import uk.ac.ebi.kraken.interfaces.uniprot.UniProtEntry;
import uk.ac.ebi.kraken.model.factories.DefaultUniProtFactory;
import uk.ac.ebi.kraken.model.uniprot.UniProtEntryImpl;
import uk.ac.ebi.kraken.parser.EntryIterator;
import uk.ac.ebi.kraken.parser.UniProtParser;
import uk.ac.ebi.uniprot.disease.utils.Constants;

import java.io.File;
import java.util.concurrent.BlockingQueue;
import java.util.stream.IntStream;

public class UniProtEntryProducer implements Runnable {
    private final BlockingQueue<UniProtEntry> uniProtEntryQueue;
    private final String fileName;
    private final int consumerCount;

    public  UniProtEntryProducer(BlockingQueue<UniProtEntry> uniProtEntryQueue, String fileName, int consumerCount){
        this.consumerCount = consumerCount;
        this.fileName = fileName;
        this.uniProtEntryQueue = uniProtEntryQueue;
    }

    @Override
    public void run() {
        File file = new File(this.fileName);
        EntryIterator iterator = UniProtParser.parseEntriesAll(file, DefaultUniProtFactory.getInstance());

        iterator.forEach(uniProtEntry -> {
            try {
                this.uniProtEntryQueue.put(uniProtEntry);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        // add the poison pills
        IntStream.range(Constants.ZERO, this.consumerCount).forEach(i -> {
            try {
                this.uniProtEntryQueue.put(new UniProtEntryImpl());
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
    }
}
