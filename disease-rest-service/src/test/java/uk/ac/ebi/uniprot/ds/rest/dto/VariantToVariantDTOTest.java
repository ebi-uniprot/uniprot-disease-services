package uk.ac.ebi.uniprot.ds.rest.dto;

/*
 * Created by sahmad on 05/02/19 13:22
 * UniProt Consortium.
 * Copyright (c) 2002-2019.
 *
 */


import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import uk.ac.ebi.uniprot.ds.common.model.*;
import uk.ac.ebi.uniprot.ds.rest.utils.ModelCreationUtils;

import java.util.Arrays;
import java.util.UUID;

@RunWith(SpringRunner.class)
@SpringBootTest
public class VariantToVariantDTOTest {
    private String uuid = UUID.randomUUID().toString();
    @Autowired
    private ModelMapper modelMapper;

    @Test
    public void testWithDefaultMapping() {
        Variant variant = ModelCreationUtils.createVariantObject(uuid);
        VariantDTO variantDTO = modelMapper.map(variant, VariantDTO.class);
        verifyVariantDTO(variant, variantDTO);
    }
    @Test
    public void testVariantWithFL(){
        Variant variant = ModelCreationUtils.createVariantObject(uuid);
        FeatureLocation fl = ModelCreationUtils.createFeatureLocationObject(uuid);
        variant.setFeatureLocation(fl);
        VariantDTO variantDTO = modelMapper.map(variant, VariantDTO.class);
        verifyVariantDTO(variant, variantDTO);
    }

    private void verifyVariantDTO(Variant variant, VariantDTO variantDTO) {
        Assert.assertEquals(variant.getOrigSeq(), variantDTO.getOrigSeq());
        Assert.assertEquals(variant.getAltSeq(), variantDTO.getAltSeq());
        Assert.assertEquals(variant.getFeatureId(), variantDTO.getFeatureId());
        Assert.assertEquals(variant.getReport(), variantDTO.getReport());
        Assert.assertEquals(variant.getFeatureStatus(), variantDTO.getFeatureStatus());
        if(variant.getFeatureLocation() != null) {
            Assert.assertEquals(variant.getFeatureLocation().getStartModifier(), variantDTO.getFeatureLocation().getStartModifier());
            Assert.assertEquals(variant.getFeatureLocation().getEndModifier(), variantDTO.getFeatureLocation().getEndModifier());
            Assert.assertEquals(variant.getFeatureLocation().getStartId(), variantDTO.getFeatureLocation().getStartId());
            Assert.assertEquals(variant.getFeatureLocation().getEndId(), variantDTO.getFeatureLocation().getEndId());
        }
    }
}
