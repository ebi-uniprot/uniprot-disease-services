package uk.ac.ebi.uniprot.ds.common.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @author sahmad
 * @created 08/10/2020
 */
class DiseaseProteinIdTest {
    @Test
    void testEquals(){
        DiseaseProteinId dp1 = new DiseaseProteinId();
        dp1.setDisease(123L);
        dp1.setProtein(234L);
        DiseaseProteinId dp2 = new DiseaseProteinId();
        dp2.setDisease(123L);
        dp2.setProtein(234L);
        Assertions.assertEquals(dp1, dp2);
        Assertions.assertEquals(dp1.hashCode(), dp2.hashCode());
    }

    @Test
    void testRefEquals(){
        DiseaseProteinId dp1 = new DiseaseProteinId();
        dp1.setDisease(123L);
        dp1.setProtein(234L);
        Assertions.assertEquals(dp1, dp1);
        Assertions.assertEquals(dp1.hashCode(), dp1.hashCode());
    }

    @Test
    void testNotEquals(){
        DiseaseProteinId dp1 = new DiseaseProteinId();
        dp1.setDisease(123L);
        dp1.setProtein(234L);
        DiseaseProteinId dp2 = new DiseaseProteinId();
        dp2.setDisease(1234L);
        dp2.setProtein(234L);
        Assertions.assertNotEquals(dp1, dp2);
    }

    @Test
    void testNotEqualsWithNull(){
        DiseaseProteinId dp1 = new DiseaseProteinId();
        dp1.setDisease(123L);
        dp1.setProtein(234L);
        Assertions.assertNotEquals(dp1, null);
    }
}
