/*
 * Created by sahmad on 12/21/18 9:20 AM
 * UniProt Consortium.
 * Copyright (c) 2002-2018.
 *
 */

package uk.ac.ebi.uniprot.disease.cli.diseaseservice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import uk.ac.ebi.kraken.interfaces.uniprot.UniProtEntry;
import uk.ac.ebi.uniprot.disease.service.ProteinService;
import uk.ac.ebi.uniprot.disease.utils.Constants;

import java.util.concurrent.*;
import java.util.stream.IntStream;

@SpringBootApplication
@ComponentScan({"uk.ac.ebi.uniprot"})
public class DiseaseServiceDataLoader implements CommandLineRunner {
    @Autowired
    private ProteinService proteinService;

    public static void main(String[] args) {
        SpringApplication.run(DiseaseServiceDataLoader.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        if(args.length != Constants.ONE){
            System.err.println("ERROR: Pass the absolute path of the swissprot data file.");
            System.exit(Constants.ONE);
        }

        //String fileName = "/Users/sahmad/Downloads/uniprot_sprot.dat";

        int consumerCountPlusOne = Runtime.getRuntime().availableProcessors();
        int consumerCount = consumerCountPlusOne - Constants.ONE;

        BlockingQueue<UniProtEntry> blockingQueue = new LinkedBlockingDeque<>();
        String fileName = args[Constants.ZERO];

        ExecutorService threadPool = Executors.newFixedThreadPool(consumerCountPlusOne);
        // create just one producer
        threadPool.execute(new UniProtEntryProducer(blockingQueue, fileName, consumerCount));
        // create and start n consumers
        IntStream.range(Constants.ZERO, consumerCount).parallel().
                forEach(i -> threadPool.execute(new UniProtEntryConsumer(blockingQueue, proteinService)));

        threadPool.shutdown();

    }
}
