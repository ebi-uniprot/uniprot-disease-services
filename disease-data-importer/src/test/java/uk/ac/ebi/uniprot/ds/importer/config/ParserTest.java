package uk.ac.ebi.uniprot.ds.importer.config;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import uk.ac.ebi.uniprot.dataservice.domain.coordinate.jaxb.GnEntries;
import uk.ac.ebi.uniprot.dataservice.domain.coordinate.jaxb.GnEntry;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;

public class ParserTest {

    @Test
    void testParser() throws JAXBException, FileNotFoundException {
        JAXBContext jaxbContext = JAXBContext.newInstance("uk.ac.ebi.uniprot.dataservice.domain.coordinate.jaxb");
        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
        InputStream inputStream = new FileInputStream(new File("src/test/resources/sample_gene_coord.xml"));
        GnEntries entries = (GnEntries)unmarshaller.unmarshal(inputStream);
        Assertions.assertNotNull(entries);
        List<GnEntry> gnEntryList = entries.getGnEntry();
        Assertions.assertNotNull(gnEntryList);
        Assertions.assertEquals(1, gnEntryList.size());
        Assertions.assertNotNull(gnEntryList.get(0).getGnCoordinate());
        Assertions.assertEquals(1, gnEntryList.get(0).getGnCoordinate().size());
    }
}
