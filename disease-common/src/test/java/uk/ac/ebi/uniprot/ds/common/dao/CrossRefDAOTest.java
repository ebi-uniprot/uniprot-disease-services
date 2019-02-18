package uk.ac.ebi.uniprot.ds.common.dao;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.ac.ebi.uniprot.ds.common.model.CrossRef;

import java.util.Optional;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class CrossRefDAOTest {

    @Autowired
    private CrossRefDAO crossRefDAO;
    private CrossRef crossRef;
    private String UUID = java.util.UUID.randomUUID().toString();

    @AfterEach
    void cleanUp(){
        if(this.crossRef != null){
            this.crossRefDAO.delete(crossRef);
        }
    }

    @Test
    void testCreateCrossRef(){
        this.crossRef = CrossRefDAOTest.createCrossRef(this.UUID);
        this.crossRefDAO.save(this.crossRef);
        Assertions.assertNotNull(this.crossRef.getId(), "unable to create cross ref");

        // get the cross ref by id
        Optional<CrossRef> optcr = this.crossRefDAO.findById(this.crossRef.getId());
        Assertions.assertTrue(optcr.isPresent());
        CrossRef cr = optcr.get();
        verifyCrossRef(this.crossRef, cr);
    }

    private void verifyCrossRef(CrossRef expected, CrossRef actual) {
        Assertions.assertEquals(expected.getId(), actual.getId());
        Assertions.assertEquals(expected.getRefType(), actual.getRefType());
        Assertions.assertEquals(expected.getRefId(), actual.getRefId());
        Assertions.assertEquals(expected.getDisease(), actual.getDisease());
        Assertions.assertEquals(expected.getCreatedAt(), actual.getCreatedAt());
        Assertions.assertEquals(expected.getUpdatedAt(), actual.getUpdatedAt());
    }

    /**
     * static method to create cross ref object
     * @param uuid
     * @return
     */
    public static CrossRef createCrossRef(String uuid){
        String refType = "Type-" + uuid;
        String refId = "ID-" + uuid;
        CrossRef.CrossRefBuilder builder = CrossRef.builder();
        builder.refType(refType).refId(refId);
        return builder.build();
    }
}
