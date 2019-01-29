/*
 * Created by sahmad on 28/01/19 23:30
 * UniProt Consortium.
 * Copyright (c) 2002-2019.
 *
 */

package uk.ac.ebi.uniprot.ds.model;

import javax.persistence.EntityManager;
import javax.persistence.Persistence;
import javax.persistence.PersistenceContext;
import java.util.Random;

public class BaseTest {
    @PersistenceContext
    EntityManager em;
    protected static int random = new Random().nextInt();
}
