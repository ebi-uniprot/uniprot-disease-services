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
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

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

    @Test
    void load() throws FileNotFoundException {
        Scanner reader = new Scanner(new FileInputStream(new File("/Users/sahmad/Documents/disease-service-data/efo.txt")));
        Scanner reader1 = new Scanner(new FileInputStream(new File("/Users/sahmad/Documents/disease-service-data/omimefo.txt")));
        Set<String> omimEFO = new HashSet<>();
        Set<String> drugEFO = new HashSet<>();
        while(reader.hasNext()){
            String e = reader.nextLine().trim().toLowerCase();
            if(e.contains("mondo")){
                e = e.replace("/", "");
            }
            drugEFO.add(e);
        }

        while(reader1.hasNext()){
            String e = reader1.nextLine().trim().toLowerCase();
            if(e.contains("mondo")){
                e = e.substring(e.lastIndexOf("/")+1);
            }
            omimEFO.add(e);
        }
        int count = 0;
        int total = drugEFO.size();
        for(String e : drugEFO){
            if(!omimEFO.contains(e)){
                System.out.println("No omim for " + e);
                count++;
            }
        }
        System.out.println("Total : " + total);
        System.out.println("Not found count : " + count);
    }
}
