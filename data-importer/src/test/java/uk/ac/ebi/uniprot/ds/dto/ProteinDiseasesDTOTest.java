/*
 * Created by sahmad on 06/02/19 19:43
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
import uk.ac.ebi.uniprot.ds.controller.dto.ProteinDiseasesDTO;
import uk.ac.ebi.uniprot.ds.controller.dto.ProteinPathwaysDTO;
import uk.ac.ebi.uniprot.ds.model.*;

import java.util.Arrays;
import java.util.HashSet;
import java.util.UUID;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ProteinDiseasesDTOTest {
    private String uuid = UUID.randomUUID().toString();

    @Autowired
    private ModelMapper modelMapper;

    @Test
    public void testProteinToProteinDiseasesDTOWithoutDiseases(){
        Protein p = ProteinTest.createProteinObject(uuid);
        ProteinDiseasesDTO dto = modelMapper.map(p, ProteinDiseasesDTO.class);
        verifyDTO(p, dto);
        Assert.assertNull(dto.getDiseases());
    }

    @Test
    public void testProteinToProteinDiseasesDTO(){
        Protein p = ProteinTest.createProteinObject(uuid);
        // create few pathways
        Disease d1 = DiseaseTest.createDiseaseObject(uuid + 1);
        Disease d2 = DiseaseTest.createDiseaseObject(uuid + 2);
        Disease d3 = DiseaseTest.createDiseaseObject(uuid + 3);
        p.setDiseases(new HashSet<>(Arrays.asList(d1, d2, d3)));

        ProteinDiseasesDTO dto = modelMapper.map(p, ProteinDiseasesDTO.class);
        verifyDTO(p, dto);
        Assert.assertEquals(p.getPathways().size(), dto.getDiseases().size());
    }

    private void verifyDTO(Protein p, ProteinDiseasesDTO dto) {
        Assert.assertEquals(p.getProteinId(), dto.getProteinId());
        Assert.assertEquals(p.getName(), dto.getProteinName());
        Assert.assertEquals(p.getAccession(), dto.getAccession());
        Assert.assertEquals(p.getGene(), dto.getGene());
    }
}
