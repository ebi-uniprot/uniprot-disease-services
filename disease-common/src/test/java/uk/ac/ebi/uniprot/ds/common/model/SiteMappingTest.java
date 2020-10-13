package uk.ac.ebi.uniprot.ds.common.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @author sahmad
 * @created 08/10/2020
 */
class SiteMappingTest {
    @Test
    void testEquals(){
        String acc = "acc";
        String pid = "protein";
        Long sp = 123L;
        Long pia = 234L;
        String st = "site type";
        String uid = "uniref";
        SiteMapping.SiteMappingBuilder b1 = SiteMapping.builder();
        b1.accession(acc).proteinId(pid).sitePosition(sp).positionInAlignment(pia);
        b1.siteType(st).unirefId(uid);
        SiteMapping sm1 = b1.build();
        // create another object
        SiteMapping.SiteMappingBuilder b2 = SiteMapping.builder();
        b2.accession(acc).proteinId(pid).sitePosition(sp).positionInAlignment(pia);
        b2.siteType(st).unirefId(uid);
        SiteMapping sm2 = b2.build();
        Assertions.assertEquals(sm1, sm2);
        Assertions.assertEquals(sm1.hashCode(), sm2.hashCode());
    }

    @Test
    void testRefEquals(){
        String acc = "acc";
        String pid = "protein";
        Long sp = 123L;
        Long pia = 234L;
        String st = "site type";
        String uid = "uniref";
        SiteMapping.SiteMappingBuilder b1 = SiteMapping.builder();
        b1.accession(acc).proteinId(pid).sitePosition(sp).positionInAlignment(pia);
        b1.siteType(st).unirefId(uid);
        SiteMapping sm1 = b1.build();
        Assertions.assertEquals(sm1, sm1);
    }

    @Test
    void testNotEquals(){
        String acc = "acc";
        String pid = "protein";
        Long sp = 123L;
        Long pia = 234L;
        String st = "site type";
        String uid = "uniref";
        SiteMapping.SiteMappingBuilder b1 = SiteMapping.builder();
        b1.accession(acc).proteinId(pid).sitePosition(sp).positionInAlignment(pia);
        b1.siteType(st).unirefId(uid);
        SiteMapping sm1 = b1.build();
        // create another object without site type
        SiteMapping.SiteMappingBuilder b2 = SiteMapping.builder();
        b2.accession(acc).proteinId(pid).sitePosition(sp).positionInAlignment(pia).unirefId(uid);
        SiteMapping sm2 = b2.build();
        Assertions.assertNotEquals(sm1, sm2);
    }

    @Test
    void testNotEqualsWithNull(){
        String acc = "acc";
        String pid = "protein";
        Long sp = 123L;
        Long pia = 234L;
        String st = "site type";
        String uid = "uniref";
        SiteMapping.SiteMappingBuilder b1 = SiteMapping.builder();
        b1.accession(acc).proteinId(pid).sitePosition(sp).positionInAlignment(pia);
        b1.siteType(st).unirefId(uid);
        SiteMapping sm1 = b1.build();
        Assertions.assertNotEquals(sm1, null);
    }
}
