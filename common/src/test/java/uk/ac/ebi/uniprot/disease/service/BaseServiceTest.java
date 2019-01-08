/*
 * Created by sahmad on 12/21/18 1:41 PM
 * UniProt Consortium.
 * Copyright (c) 2002-2018.
 *
 */

package uk.ac.ebi.uniprot.disease.service;

import org.junit.Assert;
import org.junit.BeforeClass;
import uk.ac.ebi.kraken.interfaces.uniprot.UniProtEntry;
import uk.ac.ebi.kraken.model.factories.DefaultUniProtFactory;
import uk.ac.ebi.kraken.parser.EntryIterator;
import uk.ac.ebi.kraken.parser.UniProtParser;

import java.io.File;

public class BaseServiceTest {
    protected final static DiseaseService diseaseService = new DiseaseService();
    protected static final ProteinService proteinService = new ProteinService(new DiseaseService());

    private static final String DATA_FILE_PATH = "src/test/resources/sample_uniprot_sprot.dat";
    protected static EntryIterator iterator;
    protected static UniProtEntry uniProtEntry;

    @BeforeClass
    public static void getIterator(){
        iterator = UniProtParser.parseEntriesAll(new File(DATA_FILE_PATH), DefaultUniProtFactory.getInstance());
        if(BaseServiceTest.iterator.hasNext()){
            uniProtEntry = BaseServiceTest.iterator.next();
        }
        Assert.assertNotNull("Unable to read the sample data file", uniProtEntry);
    }
}
