/*
 * Created by sahmad on 04/02/19 09:56
 * UniProt Consortium.
 * Copyright (c) 2002-2019.
 *
 */

package uk.ac.ebi.uniprot.ds.controller;

import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import uk.ac.ebi.uniprot.ds.model.*;
import uk.ac.ebi.uniprot.ds.service.DiseaseService;

import java.util.*;

@RunWith(SpringRunner.class)
@WebMvcTest(DiseaseController.class)
public class DiseaseControllerIT {
    private String uuid = UUID.randomUUID().toString();

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DiseaseService diseaseService;

    @Test
    public void testGetDisease() throws Exception {
        String diseaseId = "DISEASE_ID";
        Disease disease = DiseaseTest.createDiseaseObject(uuid);
        disease.setDiseaseId(diseaseId);

        Mockito.when(this.diseaseService.findByDiseaseId(diseaseId)).thenReturn(Optional.of(disease));

        ResultActions res = this.mockMvc.perform
                (
                        MockMvcRequestBuilders.
                                get("/v1/ds/disease/" + diseaseId).
                                param("diseaseId", diseaseId)
                );

        res.andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.jsonPath("$.diseaseId", Matchers.equalTo(diseaseId)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.diseaseName", Matchers.startsWith("DN")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.acronym", Matchers.startsWith("ACRONYM")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.description", Matchers.startsWith("DESC")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.proteins", Matchers.nullValue()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.variants", Matchers.nullValue()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.synonyms", Matchers.nullValue()));
        Assert.assertNotNull(res);
    }

    @Test
    public void testGetDiseaseWithOtherDetails() throws Exception {

        String diseaseId = "FULL_DISEASE";
        Disease disease = DiseaseTest.createDiseaseObject(uuid);
        disease.setDiseaseId(diseaseId);
        // synonyms
        Synonym s1 = SynonymTest.createSynonymObject(uuid);
        Synonym s2 = SynonymTest.createSynonymObject(uuid);
        disease.setSynonyms(Arrays.asList(s1, s2));

        // proteins
        Protein p1 = ProteinTest.createProteinObject(uuid + 1);
        Protein p2 = ProteinTest.createProteinObject(uuid + 2);
        Protein p3 = ProteinTest.createProteinObject(uuid + 3);
        disease.setProteins(new HashSet<>(Arrays.asList(p1, p2, p3)));

        // variants
        Variant v1 = VariantTest.createVariantObject(uuid + 1);
        Variant v2 = VariantTest.createVariantObject(uuid + 2);
        Variant v3 = VariantTest.createVariantObject(uuid + 3);
        Variant v4 = VariantTest.createVariantObject(uuid + 4);
        disease.setVariants(Arrays.asList(v1, v2, v3, v4));

        Mockito.when(this.diseaseService.findByDiseaseId(diseaseId)).thenReturn(Optional.of(disease));

        ResultActions res = this.mockMvc.
                perform(MockMvcRequestBuilders.get("/v1/ds/disease/" + diseaseId).param("diseaseId", diseaseId));

        res.andDo(MockMvcResultHandlers.print())
        .andExpect(MockMvcResultMatchers.jsonPath("$.diseaseId", Matchers.equalTo(diseaseId)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.diseaseName", Matchers.startsWith("DN")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.acronym", Matchers.startsWith("ACRONYM")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.description", Matchers.startsWith("DESC")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.proteins.length()", Matchers.equalTo(3)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.variants.length()", Matchers.equalTo(4)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.synonyms.length()", Matchers.equalTo(2)));
    }

    @Test//FIXME add custom error
    public void testNonExistentDisease() throws Exception {
        /*String diseaseId = "randomDisease";
        ResultActions res = this.mockMvc.
                perform(MockMvcRequestBuilders.get("/disease/" + diseaseId).param("diseaseId", diseaseId));
        res.andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.jsonPath("$.timestamp", Matchers.notNullValue()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.status", Matchers.equalTo(500)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.error", Matchers.equalTo("Internal Server Error")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message", Matchers.equalTo("No value present")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.path", Matchers.endsWith(diseaseId)));
*/
    }
}
