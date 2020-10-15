/*
 * Created by sahmad on 30/01/19 09:30
 * UniProt Consortium.
 * Copyright (c) 2002-2019.
 *
 */

package uk.ac.ebi.uniprot.ds.importer.reader;

import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.item.ItemReader;
import uk.ac.ebi.uniprot.ds.importer.reader.graph.OBOTerm;
import uk.ac.ebi.uniprot.ds.importer.util.Constants;

import java.util.*;

/**
 * Read the term from the cache and pass to the processor to create parent child relationsip
 */
public class MondoTermReader implements ItemReader<OBOTerm> {
    private List<OBOTerm> oboTerms;
    private Iterator<OBOTerm> iterator;
    public OBOTerm read() {
        OBOTerm oboTerm = null;
        if(this.iterator.hasNext()){
            oboTerm = this.iterator.next();
        }
        return oboTerm;
    }

    @BeforeStep
    public void init(final StepExecution stepExecution) {// get the cached data from previous step, initialise and put other things in cache
        this.oboTerms = (List<OBOTerm>) stepExecution.getJobExecution().getExecutionContext().get(Constants.MONDO_OBO_TERMS_LIST);
        this.iterator = this.oboTerms.iterator();
    }
}
