/*
 * Created by sahmad on 05/02/19 13:22
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
import uk.ac.ebi.uniprot.ds.controller.dto.ProteinDTO;
import uk.ac.ebi.uniprot.ds.model.*;

import java.util.Arrays;
import java.util.HashSet;
import java.util.UUID;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ProteinDTOTest {
    private String uuid = UUID.randomUUID().toString();
    @Autowired
    private ModelMapper modelMapper;

    @Test
    public void testWithDefaultMapping(){
        Protein protein = ProteinTest.createProteinObject(uuid);
        ProteinDTO proteinDTO = modelMapper.map(protein, ProteinDTO.class);
        verifyProteinDTO(protein, proteinDTO);
    }

    @Test
    public void testWithChildren(){
        Protein protein = ProteinTest.createProteinObject(uuid);

        Disease d1 = DiseaseTest.createDiseaseObject(uuid + 1);
        Disease d2 = DiseaseTest.createDiseaseObject(uuid + 2);
        protein.setDiseases(new HashSet<>(Arrays.asList(d1, d2)));

        Variant v1 = VariantTest.createVariantObject(uuid + 1);
        Variant v2 = VariantTest.createVariantObject(uuid + 2);
        Variant v3 = VariantTest.createVariantObject(uuid + 3);
        protein.setVariants(Arrays.asList(v1, v2,v3));

        // pathways
        Pathway p1 = PathwayTest.createPathwayObject(uuid + 1);
        Pathway p2 = PathwayTest.createPathwayObject(uuid + 2);
        Pathway p3 = PathwayTest.createPathwayObject(uuid + 3);
        protein.setPathways(Arrays.asList(p1, p2, p3));

        // interactions
        Interaction in1 = InteractionTest.createInteractionObject(uuid + 1);
        Interaction in2 = InteractionTest.createInteractionObject(uuid + 2);
        Interaction in3 = InteractionTest.createInteractionObject(uuid + 3);
        Interaction in4 = InteractionTest.createInteractionObject(uuid + 4);
        protein.setInteractions(Arrays.asList(in1, in2, in3, in4));

        ProteinDTO proteinDTO = modelMapper.map(protein, ProteinDTO.class);
        verifyProteinDTO(protein, proteinDTO);

        Assert.assertEquals(protein.getDiseases().size(), proteinDTO.getDiseases().size());
        Assert.assertEquals(protein.getVariants().size(), proteinDTO.getVariants().size());
        Assert.assertEquals(protein.getPathways().size(),  proteinDTO.getPathways().size());
        Assert.assertEquals(protein.getInteractions().size(),  proteinDTO.getInteractions().size());
    }

    private void verifyProteinDTO(Protein protein, ProteinDTO proteinDTO) {
        Assert.assertEquals(protein.getProteinId(), proteinDTO.getProteinId());
        Assert.assertEquals(protein.getName(), proteinDTO.getProteinName());
        Assert.assertEquals(protein.getAccession(), proteinDTO.getAccession());
        Assert.assertEquals(protein.getDesc(), proteinDTO.getDescription());
    }
}
