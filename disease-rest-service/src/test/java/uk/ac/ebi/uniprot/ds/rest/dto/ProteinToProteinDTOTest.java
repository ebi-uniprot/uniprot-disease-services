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

        // create few drugs
        Drug drug1 = ModelCreationUtils.createDrugObject(uuid + 1);
        Drug drug2 = ModelCreationUtils.createDrugObject(uuid + 2);
        Drug drug3 = ModelCreationUtils.createDrugObject(uuid + 3);

        // protein cross ref
        ProteinCrossRef p1 = ModelCreationUtils.createProteinXRefObject(uuid + 1);
        p1.setDrugs(Arrays.asList(drug1, drug2, drug3));
        ProteinCrossRef p2 = ModelCreationUtils.createProteinXRefObject(uuid + 2);
        p2.setDrugs(Arrays.asList(drug1, drug2, drug3));
        ProteinCrossRef p3 = ModelCreationUtils.createProteinXRefObject(uuid + 3);
        p3.setDrugs(Arrays.asList(drug1, drug2, drug3));
        protein.setProteinCrossRefs(Arrays.asList(p1, p2, p3));

        // interactions
        Interaction in1 = ModelCreationUtils.createInteractionObject(uuid + 1);
        Interaction in2 = ModelCreationUtils.createInteractionObject(uuid + 2);
        Interaction in3 = ModelCreationUtils.createInteractionObject(uuid + 3);
        Interaction in4 = ModelCreationUtils.createInteractionObject(uuid + 4);
        protein.setInteractions(Arrays.asList(in1, in2, in3, in4));

        // create GeneCoordinates
        GeneCoordinate gc1 = ModelCreationUtils.createGeneCoordinateObject(uuid + 1);
        GeneCoordinate gc2 = ModelCreationUtils.createGeneCoordinateObject(uuid + 2);
        GeneCoordinate gc3 = ModelCreationUtils.createGeneCoordinateObject(uuid + 3);
        GeneCoordinate gc4 = ModelCreationUtils.createGeneCoordinateObject(uuid + 4);
        GeneCoordinate gc5 = ModelCreationUtils.createGeneCoordinateObject(uuid + 5);
        protein.setGeneCoordinates(Arrays.asList(gc1, gc2, gc3, gc4, gc5));

        // create few publications
        Publication pb1 = ModelCreationUtils.createPublicationObject(this.uuid + 1);
        Publication pb2 = ModelCreationUtils.createPublicationObject(this.uuid + 2);
        Publication pb3 = ModelCreationUtils.createPublicationObject(this.uuid + 3);
        Publication pb4 = ModelCreationUtils.createPublicationObject(this.uuid + 4);
        protein.setPublications(Arrays.asList(pb1, pb2, pb3, pb4));

        ProteinDTO proteinDTO = modelMapper.map(protein, ProteinDTO.class);
        verifyProteinDTO(protein, proteinDTO);

        Assert.assertEquals(protein.getDiseases().size(), proteinDTO.getDiseases().size());
        Assert.assertEquals(protein.getVariants().size(), proteinDTO.getVariants().size());
     //   Assert.assertEquals(protein.getProteinCrossRefs().size(), proteinDTO.getPathways().size());
        Assert.assertEquals(protein.getInteractions().size(), proteinDTO.getInteractions().size());
        Assert.assertEquals(protein.getGeneCoordinates().size(), proteinDTO.getGeneCoordinates().size());
        Assert.assertEquals(protein.getPublications().size(), proteinDTO.getPublications().size());
        Assert.assertEquals(p1.getDrugs().size(), proteinDTO.getDrugs().size());
    }

    private void verifyProteinDTO(Protein protein, ProteinDTO proteinDTO) {
        Assert.assertEquals(protein.getProteinId(), proteinDTO.getProteinId());
        Assert.assertEquals(protein.getName(), proteinDTO.getProteinName());
        Assert.assertEquals(protein.getAccession(), proteinDTO.getAccession());
        Assert.assertEquals(protein.getDesc(), proteinDTO.getDescription());
    }
}
