/*
 * Created by sahmad on 29/01/19 11:49
 * UniProt Consortium.
 * Copyright (c) 2002-2019.
 *
 */

package uk.ac.ebi.uniprot.ds.reader;

import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import uk.ac.ebi.kraken.interfaces.uniprot.NcbiTaxonomyId;
import uk.ac.ebi.kraken.interfaces.uniprot.UniProtEntry;
import uk.ac.ebi.kraken.model.factories.DefaultUniProtFactory;
import uk.ac.ebi.kraken.parser.EntryIterator;
import uk.ac.ebi.kraken.parser.UniProtParser;

import java.io.File;
import java.util.List;

public class UniProtReader implements ItemReader<UniProtEntry> {
    private static int count = 0;
    private static final String HUMAX_TAXANOMY_ID = "9606";
    private EntryIterator iterator;

    public UniProtReader(String filePath){
        iterator = UniProtParser.parseEntriesAll(new File(filePath), DefaultUniProtFactory.getInstance());
    }

    @Override
    public UniProtEntry read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
        UniProtEntry entry = null;
//        // get only human protein
//        while(entry == null && iterator.hasNext()){
//            UniProtEntry tmpEntry = iterator.next();
//            List<NcbiTaxonomyId> taxonomyIds = tmpEntry.getNcbiTaxonomyIds();
//            if(taxonomyIds != null && !taxonomyIds.isEmpty() && HUMAX_TAXANOMY_ID.equals(taxonomyIds.get(0).getValue())){
//                entry = tmpEntry;
//            }
//        }

        if(iterator.hasNext()){
            entry = iterator.next();
            System.out.println(count++);
        }

        return entry;
    }
}
