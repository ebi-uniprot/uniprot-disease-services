package uk.ac.ebi.uniprot.ds.rest.dto;
/*
 * Created by sahmad on 06/02/19 19:43
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
import uk.ac.ebi.uniprot.ds.common.model.Disease;
import uk.ac.ebi.uniprot.ds.common.model.Protein;
import uk.ac.ebi.uniprot.ds.rest.utils.ModelCreationUtils;

import java.util.Arrays;
import java.util.UUID;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ProteinToProteinDiseasesDTOTest {
    private String uuid = UUID.randomUUID().toString();

    @Autowired
    private ModelMapper modelMapper;

    @Test
    public void testProteinToProteinDiseasesDTOWithoutDiseases(){
        Protein p = ModelCreationUtils.createProteinObject(uuid);
        ProteinDiseasesDTO dto = modelMapper.map(p, ProteinDiseasesDTO.class);
        verifyDTO(p, dto);
        Assert.assertNull(dto.getDiseases());
    }

    @Test
    public void testProteinToProteinDiseasesDTO(){
        Protein p = ModelCreationUtils.createProteinObject(uuid);
        // create few diseases
        Disease d1 = ModelCreationUtils.createDiseaseObject(uuid + 1);
        Disease d2 = ModelCreationUtils.createDiseaseObject(uuid + 2);
        Disease d3 = ModelCreationUtils.createDiseaseObject(uuid + 3);
        p.setDiseases(Arrays.asList(d1, d2, d3));

        ProteinDiseasesDTO dto = modelMapper.map(p, ProteinDiseasesDTO.class);
        verifyDTO(p, dto);
        Assert.assertEquals(p.getDiseases().size(), dto.getDiseases().size());
    }

    private void verifyDTO(Protein p, ProteinDiseasesDTO dto) {
        Assert.assertEquals(p.getProteinId(), dto.getProteinId());
        Assert.assertEquals(p.getName(), dto.getProteinName());
        Assert.assertEquals(p.getAccession(), dto.getAccession());
        Assert.assertEquals(p.getGene(), dto.getGene());
    }
}
