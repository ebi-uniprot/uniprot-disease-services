package uk.ac.ebi.uniprot.ds.common.dao;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.ac.ebi.uniprot.ds.common.model.*;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

/*
 * Created by sahmad on 07/02/19 10:56
 * UniProt Consortium.
 * Copyright (c) 2002-2019.
 *
 */

@ExtendWith(SpringExtension.class)
@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class DiseaseProteinDAOTest {

    @Autowired
    private DiseaseDAO diseaseDAO;
    @Autowired
    private ProteinDAO proteinDAO;
    @Autowired
    private DiseaseProteinDAO diseaseProteinDAO;

    private Disease disease;
    private Protein protein;
    private List<Disease> diseases;
    private String uuid;

    @BeforeEach
    void setUp(){
        uuid = UUID.randomUUID().toString();
    }

    @AfterEach
    void cleanUp(){

        if(this.protein != null){
            this.proteinDAO.delete(this.protein);
            this.protein = null;
        }

        if(this.disease != null){
            this.diseaseDAO.delete(this.disease);
            this.disease = null;
        }

        if(this.diseases != null && !this.diseases.isEmpty()){
            this.diseases.forEach(disease -> this.diseaseDAO.delete(disease));
            this.diseases = null;
        }
    }


    @Test
    void testGetDiseasesByProteinId(){

        // create a disease and save it
        this.disease = DiseaseTest.createDiseaseObject(uuid);
        this.diseaseDAO.save(this.disease);

        // create protein with disease protein
        this.protein = ProteinTest.createProteinObject(uuid);
        // create disease protein row object
        DiseaseProtein diseaseProtein = new DiseaseProtein();
        diseaseProtein.setProtein(this.protein);
        diseaseProtein.setDisease(this.disease);
        diseaseProtein.setIsMapped(true);

        this.protein.getDiseaseProteins().add(diseaseProtein);
        this.proteinDAO.save(this.protein);

        List<DiseaseProtein> dpList = this.diseaseProteinDAO.findAllByProtein(this.protein);
        assertEquals(1, dpList.size());

    }




}
