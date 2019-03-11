package uk.ac.ebi.uniprot.ds.rest.dto;

/*
 * Created by sahmad on 06/02/19 09:47
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
import uk.ac.ebi.uniprot.ds.common.model.ProteinCrossRef;
import uk.ac.ebi.uniprot.ds.common.model.Protein;
import uk.ac.ebi.uniprot.ds.rest.utils.ModelCreationUtils;

import java.util.Arrays;
import java.util.UUID;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ProteinToProteinCrossRefsDTOTest {
    private String uuid = UUID.randomUUID().toString();

    @Autowired
    private ModelMapper modelMapper;

    @Test
    public void testProteinToProteinXRefsDTOWithoutXRefs() {
        Protein p = ModelCreationUtils.createProteinObject(uuid);
        ProteinWithCrossRefsDTO dto = modelMapper.map(p, ProteinWithCrossRefsDTO.class);
        verifyDTO(p, dto);
        Assert.assertNull(dto.getXrefs());
    }

    @Test
    public void testProteinToProteinCrossRefsDTO() {
        Protein pP = ModelCreationUtils.createProteinObject(uuid);
        // create few protein cross ref
        ProteinCrossRef p1 = ModelCreationUtils.createProteinXRefObject(uuid + 1);
        ProteinCrossRef p2 = ModelCreationUtils.createProteinXRefObject(uuid + 2);
        ProteinCrossRef p3 = ModelCreationUtils.createProteinXRefObject(uuid + 3);
        pP.setProteinCrossRefs(Arrays.asList(p1, p2, p3));

        ProteinWithCrossRefsDTO dto = modelMapper.map(pP, ProteinWithCrossRefsDTO.class);
        verifyDTO(pP, dto);
        Assert.assertEquals(pP.getProteinCrossRefs().size(), dto.getXrefs().size());
    }

    private void verifyDTO(Protein p, ProteinWithCrossRefsDTO dto) {
        Assert.assertEquals(p.getProteinId(), dto.getProteinId());
        Assert.assertEquals(p.getName(), dto.getProteinName());
        Assert.assertEquals(p.getAccession(), dto.getAccession());
        Assert.assertEquals(p.getGene(), dto.getGene());
    }
}
