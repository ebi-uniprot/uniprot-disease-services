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
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.ac.ebi.uniprot.ds.common.model.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class PublicationDAOTest {
    @Autowired
    private PublicationDAO pubDAO;
    private String uuid;


    private Publication pub;

    @BeforeEach
    void setUp(){
        this.uuid = UUID.randomUUID().toString();
    }

    @AfterEach
    void cleanUp(){
        if(this.pub != null){
            this.pubDAO.deleteById(this.pub.getId());
        }

    }

    @Test
    void testCreatePublication(){
        this.pub = createPublicationObject(this.uuid, null, null);
        this.pubDAO.save(this.pub);

        // get and verify
        Optional<Publication> optPub = this.pubDAO.findById(this.pub.getId());
        assertTrue(optPub.isPresent());
        verifyPub(this.pub, optPub.get());
    }

    private void verifyPub(Publication actual, Publication expected) {
        assertEquals(actual.getId(), expected.getId());
        assertEquals(actual.getPubId(), expected.getPubId());
        assertEquals(actual.getPubType(), expected.getPubType());
        assertEquals(actual.getDisease(), expected.getDisease());
        assertEquals(actual.getProtein(), expected.getProtein());
        assertEquals(actual.getCreatedAt(), expected.getCreatedAt());
        assertEquals(actual.getUpdatedAt(), expected.getUpdatedAt());
    }

    public static Publication createPublicationObject(String rand, Disease disease, Protein protein){
        Publication.PublicationBuilder bldr = Publication.builder();
        bldr.pubType("type-" + rand);
        bldr.pubId("id-" + rand);
        bldr.disease(disease);
        bldr.protein(protein);
        return bldr.build();
    }



}
