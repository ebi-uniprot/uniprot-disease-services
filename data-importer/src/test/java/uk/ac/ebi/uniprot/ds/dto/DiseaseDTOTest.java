/*
 * Created by sahmad on 04/02/19 13:07
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
import uk.ac.ebi.uniprot.ds.controller.dto.DiseaseDTO;
import uk.ac.ebi.uniprot.ds.model.*;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@RunWith(SpringRunner.class)
@SpringBootTest
public class DiseaseDTOTest {
    private String uuid = UUID.randomUUID().toString();
    @Autowired
    private ModelMapper modelMapper;

    @Test
    public void testDefaultMapping(){
        Disease disease = DiseaseTest.createDiseaseObject(this.uuid);
        DiseaseDTO diseaseDTO = modelMapper.map(disease, DiseaseDTO.class);
        verifyDiseaseDTO(disease, diseaseDTO);
    }

    @Test
    public void testDiseaseToDiseaseDTO(){
        Disease disease = DiseaseTest.createDiseaseObject();

        Protein p1 = ProteinTest.createProteinObject(this.uuid + 1);
        Protein p2 = ProteinTest.createProteinObject(this.uuid + 2);
        Protein p3 = ProteinTest.createProteinObject(this.uuid + 3);
        Set<Protein> ps = new HashSet<>();
        ps.add(p1);ps.add(p2);ps.add(p3);
        disease.setProteins(ps);

        Synonym s1 = SynonymTest.createSynonymObject(this.uuid + 1);
        Synonym s2 = SynonymTest.createSynonymObject(this.uuid + 2);
        disease.setSynonyms(Arrays.asList(s1, s2));

        Variant v1 = VariantTest.createVariantObject(this.uuid + 1);
        Variant v2 = VariantTest.createVariantObject(this.uuid + 2);
        Variant v3 = VariantTest.createVariantObject(this.uuid + 3);
        Variant v4 = VariantTest.createVariantObject(this.uuid + 4);
        disease.setVariants(Arrays.asList(v1, v2, v3, v4));

        DiseaseDTO dto = modelMapper.map(disease, DiseaseDTO.class);
        verifyDiseaseDTO(disease, dto);

        // verify other details
        Assert.assertEquals(disease.getProteins().size(),  dto.getProteins().size());
        Assert.assertEquals(disease.getSynonyms().size(), dto.getSynonyms().size());
        Assert.assertEquals(disease.getVariants().size(), dto.getVariants().size());
    }

    private void verifyDiseaseDTO(Disease disease, DiseaseDTO diseaseDTO) {
        Assert.assertEquals(disease.getDiseaseId(), diseaseDTO.getDiseaseId());
        Assert.assertEquals(disease.getName(), diseaseDTO.getDiseaseName());
        Assert.assertEquals(disease.getAcronym(), diseaseDTO.getAcronym());
        Assert.assertEquals(disease.getDesc(), diseaseDTO.getDescription());
    }
}
