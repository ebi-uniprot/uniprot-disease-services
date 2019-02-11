/*
 * Created by sahmad on 07/02/19 11:00
 * UniProt Consortium.
 * Copyright (c) 2002-2019.
 *
 */

package uk.ac.ebi.uniprot.ds.common.model;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Random;
import java.util.UUID;

public class BaseTest {
    @PersistenceContext
    EntityManager em;
    protected static String random = UUID.randomUUID().toString();
}
