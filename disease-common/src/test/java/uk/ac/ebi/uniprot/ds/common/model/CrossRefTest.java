package uk.ac.ebi.uniprot.ds.common.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.UUID;

public class CrossRefTest {

    @Test
    void testEquals1(){
        String uuid = UUID.randomUUID().toString();
        String rt = "RT-" + uuid;
        String ri = "RI-" + uuid;
        String src = "SRC-" + uuid;
        Disease dis = DiseaseTest.createDiseaseObject(uuid);
        CrossRef.CrossRefBuilder cbr = CrossRef.builder();
        cbr.refType(rt).refId(ri).source(src).disease(dis);
        CrossRef xRef1 = cbr.build();
        CrossRef xRef2 = xRef1;
        Assertions.assertTrue(xRef1.equals(xRef2));
    }

    @Test
    void testEquals2(){
        String uuid = UUID.randomUUID().toString();
        String rt = "RT-" + uuid;
        String ri = "RI-" + uuid;
        String src = "SRC-" + uuid;
        Disease dis = DiseaseTest.createDiseaseObject(uuid);
        // create an object
        CrossRef.CrossRefBuilder cbr = CrossRef.builder();
        cbr.refType(rt).refId(ri).source(src).disease(dis);
        CrossRef xRef1 = cbr.build();

        // one more object
        CrossRef.CrossRefBuilder cbr1 = CrossRef.builder();
        cbr1.refType(rt).refId(ri).source(src).disease(dis);
        CrossRef xRef2 = cbr1.build();

        Assertions.assertTrue(xRef1.equals(xRef2));
    }

    @Test
    void testNotEquals(){
        String uuid = UUID.randomUUID().toString();
        String rt = "RT-" + uuid;
        String ri = "RI-" + uuid;
        String src = "SRC-" + uuid;
        Disease dis = DiseaseTest.createDiseaseObject(uuid);
        // create an object
        CrossRef.CrossRefBuilder cbr = CrossRef.builder();
        cbr.refType(rt).refId(ri).source(src).disease(dis);
        CrossRef xRef1 = cbr.build();

        // one more object
        CrossRef.CrossRefBuilder cbr1 = CrossRef.builder();
        cbr1.refType(rt).refId(ri).source(src+0).disease(dis);
        CrossRef xRef2 = cbr1.build();
        Assertions.assertFalse(xRef1.equals(xRef2));
    }
}
