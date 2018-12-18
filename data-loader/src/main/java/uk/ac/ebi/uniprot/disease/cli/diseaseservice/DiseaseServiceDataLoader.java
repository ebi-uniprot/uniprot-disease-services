package uk.ac.ebi.uniprot.disease.cli.diseaseservice;

import uk.ac.ebi.kraken.interfaces.uniprot.UniProtEntry;

import java.util.concurrent.*;

public class DiseaseServiceDataLoader {
    public static void main(String[] args) {
        int consumerCountPlusOne = Runtime.getRuntime().availableProcessors();
        BlockingQueue<UniProtEntry> blockingQueue = new LinkedBlockingDeque<>();
        String fileName = "/Users/sahmad/Downloads/uniprot_sprot.dat";//TODO Parametrized it
        ExecutorService threadPool = Executors.newFixedThreadPool(consumerCountPlusOne);
        // create just one producer
        threadPool.execute(new UniProtEntryProducer(blockingQueue, fileName, consumerCountPlusOne - 1));
        // create and start n consumers
        for(int i = 0; i < consumerCountPlusOne - 1; i++) {
            threadPool.execute(new UniProtEntryConsumer(blockingQueue));
        }

        threadPool.shutdown();
    }
}
