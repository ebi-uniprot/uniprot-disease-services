package uk.ac.ebi.uniprot.disease.cli.diseaseservice;

import uk.ac.ebi.kraken.interfaces.uniprot.UniProtEntry;
import uk.ac.ebi.kraken.interfaces.uniprot.UniProtEntryType;
import uk.ac.ebi.kraken.interfaces.uniprot.comments.CommentType;
import uk.ac.ebi.uniprot.disease.service.ProteinService;

import java.util.concurrent.BlockingQueue;

public class UniProtEntryConsumer implements Runnable {
    private final BlockingQueue<UniProtEntry> uniProtEntryQueue;


    public UniProtEntryConsumer(BlockingQueue<UniProtEntry> uniProtEntryQueue) {
        this.uniProtEntryQueue = uniProtEntryQueue;
    }

    public void run() {
        while (true) {
            try {
                UniProtEntry uniProtEntry = uniProtEntryQueue.take();
                if (uniProtEntry.getType() == UniProtEntryType.UNKNOWN) {
                    break;
                } else if (!uniProtEntry.getComments(CommentType.DISEASE).isEmpty()) {
                    new ProteinService().createProtein(uniProtEntry);
                }
                //System.out.println(Thread.currentThread().getName() + " UniProt ID: " + uniProtEntry.getUniProtId());
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
