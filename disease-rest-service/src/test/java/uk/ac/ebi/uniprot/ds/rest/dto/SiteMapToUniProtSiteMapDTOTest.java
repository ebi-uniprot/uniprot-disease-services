package uk.ac.ebi.uniprot.ds.rest.dto;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.UUID;

import uk.ac.ebi.uniprot.ds.common.model.SiteMapping;
import uk.ac.ebi.uniprot.ds.rest.utils.ModelCreationUtils;

/**
 * @author sahmad
 * @created 07/09/2020
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class SiteMapToUniProtSiteMapDTOTest {
    private String uuid = UUID.randomUUID().toString();
    @Autowired
    private ModelMapper modelMapper;

    @Test
    public void testSiteMapToSiteMapDTO(){
        SiteMapping sm = ModelCreationUtils.createSiteMappingObject(this.uuid);
        UniProtSiteMapDTO dto = modelMapper.map(sm, UniProtSiteMapDTO.class);
        verifyDTO(dto);
        Assert.assertNotNull(dto.getDbSnps());
        Assert.assertEquals(1, dto.getDbSnps().size());
        Assert.assertEquals("rs397507523", dto.getDbSnps().get(0));
        Assert.assertNotNull(dto.getFeatureTypes());
        Assert.assertEquals(2, dto.getFeatureTypes().size());
        Assert.assertEquals(FeatureType.VARIANT, dto.getFeatureTypes().get(0));
        Assert.assertEquals(FeatureType.MUTAGENESIS, dto.getFeatureTypes().get(1));
        Assert.assertNotNull(dto.getMappedSites());
        Assert.assertEquals(5, dto.getMappedSites().size());
        Assert.assertNotNull( dto.getMappedSites().get(0).getAccession());
        Assert.assertNotNull( dto.getMappedSites().get(0).getPosition());
        Assert.assertNotNull( dto.getMappedSites().get(0).getUniProtId());
        Assert.assertNotNull(dto.getMappedSites().get(0).isNew());
    }

    @Test
    public void testSiteMapWithoutSiteMappingToSiteMapDTO(){
        SiteMapping sm = ModelCreationUtils.createSiteMappingObject(this.uuid);
        sm.setSiteType(null);
        sm.setMappedSite(null);
        UniProtSiteMapDTO dto = modelMapper.map(sm, UniProtSiteMapDTO.class);
        verifyDTO(dto);
        Assert.assertNull(dto.getDbSnps());
        Assert.assertNull(dto.getFeatureTypes());
        Assert.assertNull(dto.getMappedSites());
    }

    private void verifyDTO(UniProtSiteMapDTO dto){
        Assert.assertNotNull(dto);
        Assert.assertNotNull(dto.getAccession());
        Assert.assertNotNull(dto.getUniProtId());
        Assert.assertNotNull(dto.getUnirefId());
        Assert.assertNotNull(dto.getSitePosition());
        Assert.assertNotNull(dto.getPositionInAlignment());
    }
}
