/*
 * Created by sahmad on 23/01/19 15:47
 * UniProt Consortium.
 * Copyright (c) 2002-2019.
 *
 */

package uk.ac.ebi.uniprot.ds.model;

import org.junit.jupiter.api.BeforeAll;
import uk.ac.ebi.uniprot.ds.dao.BaseDAO;
import uk.ac.ebi.uniprot.ds.dao.DiseaseDAO;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

import java.util.UUID;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class BaseTest {
    protected static EntityManager em;
    private static final String PERSISTENCE_UNIT_NAME = "disgenet_eclipselink";
    protected static int random = (int)(Math.random()*100000);
    protected static String GUID = UUID.randomUUID().toString();

    @BeforeAll
    static void setUp(){
        EntityManagerFactory emf = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
        em = emf.createEntityManager();
        assertNotNull(em, "Unable to create Entity Manager");
    }

    protected void executeInsideTransaction(Consumer<BaseDAO> action, BaseDAO dao){
        EntityTransaction txn = BaseTest.em.getTransaction();
        txn.begin();
        action.accept(dao);
        txn.commit();
    }
}
