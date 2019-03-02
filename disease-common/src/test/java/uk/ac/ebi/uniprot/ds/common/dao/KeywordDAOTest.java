/*
 * Created by sahmad on 07/02/19 10:56
 * UniProt Consortium.
 * Copyright (c) 2002-2019.
 *
 */

package uk.ac.ebi.uniprot.ds.common.dao;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.ac.ebi.uniprot.ds.common.model.*;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class KeywordDAOTest {

    @Autowired
    private DiseaseDAO diseaseDAO;
    @Autowired
    private KeywordDAO keywordDAO;

    private List<Disease> diseases;
    private String uuid;

    @BeforeEach
    void setUp(){
        uuid = UUID.randomUUID().toString();
    }

    @AfterEach
    void cleanUp(){
        if(this.diseases != null && !this.diseases.isEmpty()){
            this.diseases.forEach(disease -> this.diseaseDAO.delete(disease));
            this.diseases = null;
        }
    }

    @Test
    void testSearchDiseasesByKeyword(){
        // create 5 disease with keyword1
        String keyword1 = "keyword1";
        this.diseases = new ArrayList<>();
        IntStream.range(0, 25).forEach(i -> this.diseases.add(createDiseaseWithKeyword(this.uuid+i, keyword1)));
        // add keyword2 also to 5 diseases
        String keyword2 = "keyword2";
        // create keyword
        String keyId = "ID-" + uuid;
        String keyValue = this.uuid + keyword2 + this.uuid;
        Keyword.KeywordBuilder kb = Keyword.builder();
        kb.keyId(keyId).keyValue(keyValue);
        for(int i = 0; i < 5; i++){
            Disease d = this.diseases.get(i);
            kb.disease(d);
            d.getKeywords().add(kb.build());
        }
        this.diseaseDAO.saveAll(diseases);

        // search by keyword1
        int size = 10;
        Sort sortBy = Sort.by("id");
        List<Keyword> batch1 = this.keywordDAO.findAllByKeyValueContaining(keyword1, PageRequest.of(0, size, sortBy));
        assertEquals(size, batch1.size());
        List<Keyword> batch2 = this.keywordDAO.findAllByKeyValueContaining(keyword1, PageRequest.of(1, size, sortBy));
        assertEquals(size, batch2.size());
        List<Keyword> batch3 = this.keywordDAO.findAllByKeyValueContaining(keyword1, PageRequest.of(2, size, sortBy));
        assertEquals(5, batch3.size());
        List<Keyword> batch4 = this.keywordDAO.findAllByKeyValueContaining(keyword1, PageRequest.of(3, size, sortBy));
        assertEquals(0, batch4.size());

        // search by keyword2
        List<Keyword> batch11 = this.keywordDAO.findAllByKeyValueContaining(keyword2, PageRequest.of(0, size, sortBy));
        assertEquals(5, batch11.size());
        List<Keyword> batch22 = this.keywordDAO.findAllByKeyValueContaining(keyword2, PageRequest.of(1, size, sortBy));
        assertEquals(0, batch22.size());
    }

    private Disease createDiseaseWithKeyword(String diseaseId, String keyword) {
        Disease disease = DiseaseTest.createDiseaseObject(diseaseId);
        // create keyword
        String keyId = "ID-" + uuid;
        String keyValue = this.uuid + keyword + this.uuid;
        Keyword.KeywordBuilder builder = Keyword.builder();
        builder.keyId(keyId).keyValue(keyValue);
        builder.disease(disease);
        List<Keyword> kws = new ArrayList<>();
        kws.add(builder.build());
        disease.setKeywords(kws);

        return disease;
    }
    /**
     * static method to create keyword object
     * @param uuid
     * @return
     */
    public static Keyword createKeyword(String uuid, Disease disease){
        String keyId = "ID-" + uuid;
        String keyValue = "VAL-" + uuid;
        Keyword.KeywordBuilder builder = Keyword.builder();
        builder.keyId(keyId).keyValue(keyValue);
        builder.disease(disease);
        return builder.build();
    }
}
