package uk.ac.ebi.uniprot.ds.rest.dto;

/*
 * Created by sahmad on 04/02/19 13:07
 * UniProt Consortium.
 * Copyright (c) 2002-2019.
 *
 */


import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import uk.ac.ebi.uniprot.ds.common.model.*;
import uk.ac.ebi.uniprot.ds.rest.utils.ModelCreationUtils;

import java.util.*;
import java.util.stream.IntStream;

@RunWith(SpringRunner.class)
@SpringBootTest
public class DiseaseToDiseaseDTOTest {
    private String uuid = UUID.randomUUID().toString();
    @Autowired
    private ModelMapper modelMapper;

    @Test
    public void testDefaultMapping(){
        Disease disease = ModelCreationUtils.createDiseaseObject(this.uuid);
        DiseaseDTO diseaseDTO = modelMapper.map(disease, DiseaseDTO.class);
        verifyDiseaseDTO(disease, diseaseDTO);
    }

    @Test
    public void testDiseaseToDiseaseDTO(){
        Disease disease = ModelCreationUtils.createDiseaseObject(this.uuid);

        Protein p1 = ModelCreationUtils.createProteinObject(this.uuid + 1);
        Protein p2 = ModelCreationUtils.createProteinObject(this.uuid + 2);
        Protein p3 = ModelCreationUtils.createProteinObject(this.uuid + 3);
        List<Protein> ps = new ArrayList<>();
        ps.add(p1);ps.add(p2);ps.add(p3);
        disease.setProteins(ps);

        Synonym s1 = ModelCreationUtils.createSynonymObject(this.uuid + 1);
        Synonym s2 = ModelCreationUtils.createSynonymObject(this.uuid + 2);
        disease.setSynonyms(Arrays.asList(s1, s2));

        Variant v1 = ModelCreationUtils.createVariantObject(this.uuid + 1);
        Variant v2 = ModelCreationUtils.createVariantObject(this.uuid + 2);
        Variant v3 = ModelCreationUtils.createVariantObject(this.uuid + 3);
        Variant v4 = ModelCreationUtils.createVariantObject(this.uuid + 4);
        disease.setVariants(Arrays.asList(v1, v2, v3, v4));

        // create few parent diseases
        Disease pd1 = ModelCreationUtils.createDiseaseObject(this.uuid + 1);
        Disease pd2 = ModelCreationUtils.createDiseaseObject(this.uuid + 2);
        Disease pd3 = ModelCreationUtils.createDiseaseObject(this.uuid + 3);
        disease.setParents(Arrays.asList(pd1, pd2, pd3));

        // create few publications
        Publication pb1 = ModelCreationUtils.createPublicationObject(this.uuid + 1);
        Publication pb2 = ModelCreationUtils.createPublicationObject(this.uuid + 2);
        Publication pb3 = ModelCreationUtils.createPublicationObject(this.uuid + 3);
        Publication pb4 = ModelCreationUtils.createPublicationObject(this.uuid + 4);
        disease.setPublications(Arrays.asList(pb1, pb2, pb3, pb4));

        DiseaseDTO dto = modelMapper.map(disease, DiseaseDTO.class);
        verifyDiseaseDTO(disease, dto);

        // verify other details
        Assert.assertEquals(disease.getProteins().size(),  dto.getProteins().size());
        Assert.assertEquals(disease.getSynonyms().size(), dto.getSynonyms().size());
        Assert.assertEquals(disease.getVariants().size(), dto.getVariants().size());
        Assert.assertEquals(disease.getParents().size(), dto.getParents().size());
        Assert.assertEquals(disease.getPublications().size(), dto.getPublications().size());
    }

    @Test
    public void testDiseasesToDiseaseDTOList(){
        List<Disease> diseases = new ArrayList<>();
        IntStream.range(0, 10).forEach(i -> diseases.add(ModelCreationUtils.createDiseaseObject(this.uuid+i)));
        // convert the list of diseases to list of disease dtos
        List<DiseaseDTO> dtos = modelMapper.map(diseases, new TypeToken<List<DiseaseDTO>>() {}.getType());
        Assert.assertEquals(10, dtos.size());
        IntStream.range(0, 10).forEach(i -> verifyDiseaseDTO(diseases.get(i), dtos.get(i)));
    }

    private void verifyDiseaseDTO(Disease disease, DiseaseDTO diseaseDTO) {
        Assert.assertEquals(disease.getDiseaseId(), diseaseDTO.getDiseaseId());
        Assert.assertEquals(disease.getName(), diseaseDTO.getDiseaseName());
        Assert.assertEquals(disease.getAcronym(), diseaseDTO.getAcronym());
        Assert.assertEquals(disease.getDesc(), diseaseDTO.getDescription());
    }
}
