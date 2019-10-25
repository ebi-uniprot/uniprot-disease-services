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
import uk.ac.ebi.uniprot.ds.common.model.Disease;
import uk.ac.ebi.uniprot.ds.importer.reader.diseaseontology.OBOTerm;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Pattern;

public class MondoReader implements ItemReader<List<OBOTerm>> {
    private List<OBOTerm> oboTerms;

    public List<OBOTerm> read() {
        List<OBOTerm> copyOfOboTerms = this.oboTerms; // just to make sure that it returns the terms only once to processor
        this.oboTerms = null;
        return copyOfOboTerms;
    }

    @BeforeStep
    public void getStepExecution(final StepExecution stepExecution) {// get the cached data from previous step
        this.oboTerms = (List<OBOTerm>) stepExecution.getJobExecution().getExecutionContext().get("oboterms");
        System.out.println();
    }
}
