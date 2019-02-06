/*
 * Created by sahmad on 06/02/19 09:47
 * UniProt Consortium.
 * Copyright (c) 2002-2019.
 *
 */

package uk.ac.ebi.uniprot.ds.dto;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import uk.ac.ebi.uniprot.ds.controller.dto.ProteinPathwaysDTO;
import uk.ac.ebi.uniprot.ds.model.Pathway;
import uk.ac.ebi.uniprot.ds.model.PathwayTest;
import uk.ac.ebi.uniprot.ds.model.Protein;
import uk.ac.ebi.uniprot.ds.model.ProteinTest;

import java.util.Arrays;
import java.util.UUID;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ProteinPathwaysDTOTest {
    private String uuid = UUID.randomUUID().toString();

    @Autowired
    private ModelMapper modelMapper;

    @Test
    public void testProteinToProteinPathwaysDTOWithoutPathways(){
        Protein p = ProteinTest.createProteinObject(uuid);
        ProteinPathwaysDTO dto = modelMapper.map(p, ProteinPathwaysDTO.class);
        verifyDTO(p, dto);
        Assert.assertNull(p.getPathways());
    }

    @Test
    public void testProteinToProteinPathwaysDTO(){
        Protein pP = ProteinTest.createProteinObject(uuid);
        // create few pathways
        Pathway p1 = PathwayTest.createPathwayObject(uuid + 1);
        Pathway p2 = PathwayTest.createPathwayObject(uuid + 2);
        Pathway p3 = PathwayTest.createPathwayObject(uuid + 3);
        pP.setPathways(Arrays.asList(p1, p2, p3));

        ProteinPathwaysDTO dto = modelMapper.map(pP, ProteinPathwaysDTO.class);
        verifyDTO(pP, dto);
        Assert.assertEquals(pP.getPathways().size(), dto.getPathways().size());
    }

    private void verifyDTO(Protein p, ProteinPathwaysDTO dto) {
        Assert.assertEquals(p.getProteinId(), dto.getProteinId());
        Assert.assertEquals(p.getName(), dto.getProteinName());
        Assert.assertEquals(p.getAccession(), dto.getAccession());
        Assert.assertEquals(p.getGene(), dto.getGene());
    }
}
