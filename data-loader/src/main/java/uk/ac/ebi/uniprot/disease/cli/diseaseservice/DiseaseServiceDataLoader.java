package uk.ac.ebi.uniprot.disease.cli.diseaseservice;

import uk.ac.ebi.kraken.interfaces.uniprot.UniProtEntry;
import uk.ac.ebi.uniprot.disease.utils.Constants;

import java.util.concurrent.*;
import java.util.stream.IntStream;

public class DiseaseServiceDataLoader {
    public static void main(String[] args) {
        int consumerCountPlusOne = Runtime.getRuntime().availableProcessors();
        int consumerCount = consumerCountPlusOne - Constants.ONE;

        BlockingQueue<UniProtEntry> blockingQueue = new LinkedBlockingDeque<>();
        String fileName = "/Users/sahmad/Downloads/uniprot_sprot.dat";//TODO Parametrized it

        ExecutorService threadPool = Executors.newFixedThreadPool(consumerCountPlusOne);
        // create just one producer
        threadPool.execute(new UniProtEntryProducer(blockingQueue, fileName, consumerCount));
        // create and start n consumers
        IntStream.range(Constants.ZERO, consumerCount).parallel().
                forEach(i -> threadPool.execute(new UniProtEntryConsumer(blockingQueue)));

        threadPool.shutdown();
    }
}
