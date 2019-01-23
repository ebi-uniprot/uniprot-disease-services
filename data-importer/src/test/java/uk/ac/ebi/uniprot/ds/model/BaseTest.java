/*
 * Created by sahmad on 23/01/19 15:47
 * UniProt Consortium.
 * Copyright (c) 2002-2019.
 *
 */

package uk.ac.ebi.uniprot.ds.model;

import org.junit.jupiter.api.BeforeAll;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class BaseTest {
    static EntityManager em;
    private static final String PERSISTENCE_UNIT_NAME = "disgenet_eclipselink";
    static int random = (int)(Math.random()*100000);

    @BeforeAll
    static void setUp(){
        EntityManagerFactory emf = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
        em = emf.createEntityManager();
        assertNotNull(em, "Unable to create Entity Manager");
    }
}
