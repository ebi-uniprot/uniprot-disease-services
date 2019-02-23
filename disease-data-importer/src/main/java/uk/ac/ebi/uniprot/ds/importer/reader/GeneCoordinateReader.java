/*
 * Created by sahmad on 29/01/19 11:49
 * UniProt Consortium.
 * Copyright (c) 2002-2019.
 *
 */

package uk.ac.ebi.uniprot.ds.importer.reader;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemReader;
import uk.ac.ebi.uniprot.dataservice.domain.coordinate.jaxb.GnEntries;
import uk.ac.ebi.uniprot.dataservice.domain.coordinate.jaxb.GnEntry;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Iterator;

@Slf4j
public class GeneCoordinateReader implements ItemReader<GnEntry> {

    private Iterator<GnEntry> iterator;

    public GeneCoordinateReader(String filePath) throws FileNotFoundException, JAXBException {
        JAXBContext jaxbContext = JAXBContext.newInstance("uk.ac.ebi.uniprot.dataservice.domain.coordinate.jaxb");
        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
        InputStream inputStream = new FileInputStream(new File(filePath));
        GnEntries entries = (GnEntries)unmarshaller.unmarshal(inputStream);
        iterator = entries.getGnEntry().iterator();
    }

    @Override
    public GnEntry read() {
        GnEntry entry = null;

        if(iterator.hasNext()){
            entry = iterator.next();
        }

        return entry;
    }
}
