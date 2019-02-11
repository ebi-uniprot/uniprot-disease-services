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
public class ProteinToProteinDTOTest {
    private String uuid = UUID.randomUUID().toString();
    @Autowired
    private ModelMapper modelMapper;

    @Test
    public void testWithDefaultMapping() {
        Protein protein = ModelCreationUtils.createProteinObject(uuid);
        ProteinDTO proteinDTO = modelMapper.map(protein, ProteinDTO.class);
        verifyProteinDTO(protein, proteinDTO);
    }

    @Test
    public void testWithChildren() {
        Protein protein = ModelCreationUtils.createProteinObject(uuid);

        Disease d1 = ModelCreationUtils.createDiseaseObject(uuid + 1);
        Disease d2 = ModelCreationUtils.createDiseaseObject(uuid + 2);
        protein.setDiseases(Arrays.asList(d1, d2));

        Variant v1 = ModelCreationUtils.createVariantObject(uuid + 1);
        Variant v2 = ModelCreationUtils.createVariantObject(uuid + 2);
        Variant v3 = ModelCreationUtils.createVariantObject(uuid + 3);
        protein.setVariants(Arrays.asList(v1, v2, v3));

        // pathways
        Pathway p1 = ModelCreationUtils.createPathwayObject(uuid + 1);
        Pathway p2 = ModelCreationUtils.createPathwayObject(uuid + 2);
        Pathway p3 = ModelCreationUtils.createPathwayObject(uuid + 3);
        protein.setPathways(Arrays.asList(p1, p2, p3));

        // interactions
        Interaction in1 = ModelCreationUtils.createInteractionObject(uuid + 1);
        Interaction in2 = ModelCreationUtils.createInteractionObject(uuid + 2);
        Interaction in3 = ModelCreationUtils.createInteractionObject(uuid + 3);
        Interaction in4 = ModelCreationUtils.createInteractionObject(uuid + 4);
        protein.setInteractions(Arrays.asList(in1, in2, in3, in4));

        ProteinDTO proteinDTO = modelMapper.map(protein, ProteinDTO.class);
        verifyProteinDTO(protein, proteinDTO);

        Assert.assertEquals(protein.getDiseases().size(), proteinDTO.getDiseases().size());
        Assert.assertEquals(protein.getVariants().size(), proteinDTO.getVariants().size());
        Assert.assertEquals(protein.getPathways().size(), proteinDTO.getPathways().size());
        Assert.assertEquals(protein.getInteractions().size(), proteinDTO.getInteractions().size());
    }

    private void verifyProteinDTO(Protein protein, ProteinDTO proteinDTO) {
        Assert.assertEquals(protein.getProteinId(), proteinDTO.getProteinId());
        Assert.assertEquals(protein.getName(), proteinDTO.getProteinName());
        Assert.assertEquals(protein.getAccession(), proteinDTO.getAccession());
        Assert.assertEquals(protein.getDesc(), proteinDTO.getDescription());
    }
}