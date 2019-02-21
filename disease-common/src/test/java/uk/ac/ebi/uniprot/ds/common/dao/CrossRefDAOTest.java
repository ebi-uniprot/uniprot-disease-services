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
import uk.ac.ebi.uniprot.ds.common.model.Disease;
import uk.ac.ebi.uniprot.ds.common.model.DiseaseTest;

import java.util.Arrays;
import java.util.Optional;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class CrossRefDAOTest {

    @Autowired
    private CrossRefDAO crossRefDAO;
    @Autowired
    private DiseaseDAO diseaseDAO;
    private CrossRef crossRef;
    private Disease disease;
    private String UUID = java.util.UUID.randomUUID().toString();

    @AfterEach
    void cleanUp(){
        if(this.disease != null){
            this.diseaseDAO.delete(this.disease);
        }
    }

    @Test
    void testCreateCrossRef(){
        this.disease = DiseaseTest.createDiseaseObject(this.UUID);
        this.crossRef = CrossRefDAOTest.createCrossRef(this.UUID, this.disease);
        this.disease.setCrossRefs(Arrays.asList(this.crossRef));
        this.diseaseDAO.save(this.disease);

        Assertions.assertNotNull(this.crossRef.getId(), "unable to create cross ref");

        // get the cross ref by id
        Optional<CrossRef> optcr = this.crossRefDAO.findById(this.disease.getCrossRefs().get(0).getId());
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
        Assertions.assertEquals(expected.getSource(), actual.getSource());
        Assertions.assertEquals(expected.getDisease().getId(), actual.getDisease().getId());
    }

    /**
     * static method to create cross ref object
     * @param uuid
     * @return
     */
    public static CrossRef createCrossRef(String uuid, Disease disease){
        String refType = "Type-" + uuid;
        String refId = "ID-" + uuid;
        String source = "SRC-" + uuid;
        CrossRef.CrossRefBuilder builder = CrossRef.builder();
        builder.refType(refType).refId(refId).source(source);
        builder.disease(disease);
        return builder.build();
    }
}
