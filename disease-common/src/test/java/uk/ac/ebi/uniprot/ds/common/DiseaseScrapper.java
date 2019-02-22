package uk.ac.ebi.uniprot.ds.common;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.ac.ebi.uniprot.ds.common.dao.CrossRefDAO;
import uk.ac.ebi.uniprot.ds.common.model.CrossRef;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class DiseaseScrapper {

    @Autowired
    private CrossRefDAO crossRefDAO;

    //@Test
    void getDisease() throws IOException {
        int notFoundCount = 0;
        int foundCount = 0;
        List<CrossRef> xRefs = crossRefDAO.findAll();
        for (CrossRef xRef : xRefs) {
            if ("MIM".equals(xRef.getRefType())) {
                String omim = "OMIM:" + xRef.getRefId();
                String url = "http://disease-ontology.org/search?adv_search=True&field-1=xref&value-1=";
                String resp = getDiseaseByOMIM(url, omim);
                if (resp.contains("No results found")) {
                    notFoundCount++;
                    System.out.println("Not Found:" + omim);
                } else {
                    foundCount++;
                    System.out.println("Found:" + omim);
                }
            }
        }
        System.out.println("Found Count " + foundCount);
        System.out.println("Not Found Count " + notFoundCount);
    }

    @Test
    void getDiseaseFromEFO() throws IOException {
        int notFoundCount = 0;
        int foundCount = 0;
        List<CrossRef> xRefs = crossRefDAO.findAll();
        for (CrossRef xRef : xRefs) {
            if ("MIM".equals(xRef.getRefType())) {
                String omim = "OMIM:" + xRef.getRefId();
                String url = "https://www.ebi.ac.uk/spot/oxo/api/terms/";
                String resp = "Internal Server Error";
                try {
                    resp = getDiseaseByOMIM(url, omim);
                } catch (IOException ioe) {
                    // do nothing
                }
                if (resp.contains("Internal Server Error") || resp.contains("\"label\" : \"\"")) {
                    notFoundCount++;
                    System.out.println("Not Found:" + omim);
                    System.out.println("Not found count: " + notFoundCount);
                } else {
                    foundCount++;
                    System.out.println("Found:" + omim);
                    System.out.println("Found count: " + foundCount);
                }
            }
        }
        System.out.println("Found Count " + foundCount);
        System.out.println("Not Found Count " + notFoundCount);
    }

    private String getDiseaseByOMIM(String urlStr, String omim) throws IOException {
        URL url = new URL(urlStr + omim);
        StringBuilder result = new StringBuilder();
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String line;
        while ((line = rd.readLine()) != null) {
            result.append(line);
        }
        rd.close();
        return result.toString();

    }
}
