package uk.ac.ebi.uniprot.ds.common.dao;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.ac.ebi.uniprot.ds.common.model.GeneCoordinate;
import uk.ac.ebi.uniprot.ds.common.model.Protein;
import uk.ac.ebi.uniprot.ds.common.model.ProteinTest;

import java.util.Optional;
import java.util.Random;
import java.util.UUID;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class GeneCoordinateDAOTest {
    @Autowired
    private GeneCoordinateDAO geneCoordinateDAO;

    @Autowired
    private ProteinDAO proteinDAO;

    private Protein protein;
    private GeneCoordinate gc;
    private String uuid = UUID.randomUUID().toString();

    @AfterEach
    void cleanUp(){
        if(this.protein != null){
            this.proteinDAO.delete(this.protein);
        }
    }

    @Test
    void testCreateGeneCoord(){
        this.protein = ProteinTest.createProteinObject(this.uuid);
        this.proteinDAO.save(this.protein);
        this.gc = GeneCoordinateDAOTest.createObject(this.uuid, this.protein);
        this.protein.getGeneCoordinates().add(this.gc);
        this.geneCoordinateDAO.save(this.gc);
        // the gene and verify
        Optional<GeneCoordinate> optGC = this.geneCoordinateDAO.findById(this.gc.getId());
        Assertions.assertTrue(optGC.isPresent(), "Unable to persist the gene coordinate");
        verifyGeneCoord(this.gc, optGC.get());
    }

    private void verifyGeneCoord(GeneCoordinate expected, GeneCoordinate actual) {
        Assertions.assertEquals(expected.getId(), actual.getId());
        Assertions.assertEquals(expected.getChromosomeNumber(), actual.getChromosomeNumber());
        Assertions.assertEquals(expected.getStartPos(), actual.getStartPos());
        Assertions.assertEquals(expected.getEndPos(), actual.getEndPos());
        Assertions.assertEquals(expected.getEnGeneId(), actual.getEnGeneId());
        Assertions.assertEquals(expected.getEnTranscriptId(), actual.getEnTranscriptId());
        Assertions.assertEquals(expected.getEnTranslationId(), actual.getEnTranslationId());
        Assertions.assertEquals(expected.getProtein(), actual.getProtein());
        Assertions.assertEquals(expected.getCreatedAt(), actual.getCreatedAt());
        Assertions.assertEquals(expected.getUpdatedAt(), actual.getUpdatedAt());

    }

    public static GeneCoordinate createObject(String uuid, Protein protein){
        String chrom = String.valueOf(new Random().nextInt(1000));
        Long start = new Random().nextLong();
        Long end = new Random().nextLong();
        String eng = "ENG-" + uuid;
        String ent = "ENT-" + uuid;
        String enp = "ENP-" + uuid;
        GeneCoordinate.GeneCoordinateBuilder bl = GeneCoordinate.builder();
        bl.chromosomeNumber(chrom).startPos(start).endPos(end);
        bl.enGeneId(eng).enTranscriptId(ent).enTranslationId(enp);
        bl.protein(protein);
        return bl.build();
    }

}
