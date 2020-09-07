package uk.ac.ebi.uniprot.ds.common.dao;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

import uk.ac.ebi.uniprot.ds.common.model.SiteMapping;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class SiteMappingDAOTest {

    @Autowired
    private SiteMappingDAO siteMappingDAO;
    private SiteMapping siteMapping;
    private List<SiteMapping> siteMappingList;
    String uuid = java.util.UUID.randomUUID().toString();

    @BeforeEach
    void cleanUp(){
        if(Objects.nonNull(this.siteMapping)){
            this.siteMappingDAO.delete(this.siteMapping);
        }
        if(Objects.nonNull(this.siteMappingList)){
            this.siteMappingList.stream().forEach(this.siteMappingDAO::delete);
        }
    }

    @Test
    void testCreateCrossRef(){
        this.siteMapping = createSiteMappingObject(uuid);
        this.siteMappingDAO.save(this.siteMapping);

        Assertions.assertNotNull(this.siteMapping.getId(), "unable to create site mapping");
        Assertions.assertEquals("accession-" + uuid, this.siteMapping.getAccession());
        Assertions.assertEquals("pid-" + uuid, this.siteMapping.getProteinId());
        Assertions.assertEquals("st-" + uuid, this.siteMapping.getSiteType());
        Assertions.assertEquals("uid-" + uuid, this.siteMapping.getUnirefId());
        Assertions.assertEquals("mt-" + uuid, this.siteMapping.getMappedSite());
        Assertions.assertNotNull(this.siteMapping.getSitePosition());
        Assertions.assertNotNull(this.siteMapping.getPositionInAlignment());
        Assertions.assertNotNull(this.siteMapping.getCreatedAt());
        Assertions.assertNotNull(this.siteMapping.getUpdatedAt());
    }

    @Test
    void testGetByAccession(){
        this.siteMappingList = new ArrayList<>();
        this.siteMappingList.add(createSiteMappingObject(uuid + "-0"));// same accession
        this.siteMappingList.add(createSiteMappingObject(uuid + "-1"));
        this.siteMappingList.add(createSiteMappingObject(uuid + "-2"));
        this.siteMappingList.add(createSiteMappingObject(uuid + "-0"));// same accession
        this.siteMappingDAO.saveAll(this.siteMappingList);
        String accession = "accession-" + uuid + "-0";
        List<SiteMapping> result = this.siteMappingDAO.findAllByAccession(accession);
        Assertions.assertNotNull(result);
        Assertions.assertEquals(2, result.size());
        Assertions.assertEquals(accession, result.get(0).getAccession());
        Assertions.assertEquals(accession, result.get(1).getAccession());
    }

    @Test
    void testGetByUnknownAccession(){
        String accession = "some random";
        List<SiteMapping> result = this.siteMappingDAO.findAllByAccession(accession);
        Assertions.assertNotNull(result);
        Assertions.assertTrue(result.isEmpty());
    }


    private SiteMapping createSiteMappingObject(String uuid) {
        SiteMapping.SiteMappingBuilder builder = SiteMapping.builder();
        builder.accession("accession-" + uuid);
        builder.proteinId("pid-" + uuid);
        long l1 = ThreadLocalRandom.current().nextLong(1, 10000);
        long l2 = ThreadLocalRandom.current().nextLong(1, 10000);
        builder.sitePosition(l1);
        builder.positionInAlignment(l2);
        builder.siteType("st-" + uuid);
        builder.unirefId("uid-" + uuid);
        builder.mappedSite("mt-" + uuid);
        return builder.build();
    }
}
