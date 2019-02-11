/*
 * Created by sahmad on 07/02/19 15:02
 * UniProt Consortium.
 * Copyright (c) 2002-2019.
 *
 */

package uk.ac.ebi.uniprot.ds.rest.controller;

import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import uk.ac.ebi.uniprot.ds.common.model.Disease;
import uk.ac.ebi.uniprot.ds.common.model.Protein;
import uk.ac.ebi.uniprot.ds.common.model.Synonym;
import uk.ac.ebi.uniprot.ds.common.model.Variant;
import uk.ac.ebi.uniprot.ds.rest.DataSourceTestConfig;
import uk.ac.ebi.uniprot.ds.rest.exception.AssetNotFoundException;
import uk.ac.ebi.uniprot.ds.rest.service.DiseaseService;
import uk.ac.ebi.uniprot.ds.rest.service.ProteinService;
import uk.ac.ebi.uniprot.ds.rest.utils.ModelCreationUtils;

import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

@RunWith(SpringRunner.class)
@WebMvcTest(DiseaseController.class)
@Import({DataSourceTestConfig.class})
//@ComponentScan(basePackages = {"uk.ac.ebi.uniprot.ds.rest"})
public class DiseaseControllerTest {
    private String uuid = UUID.randomUUID().toString();

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DiseaseService diseaseService;

    @MockBean
    private ProteinService proteinService;

    @Test
    public void testGetDisease() throws Exception {
        String diseaseId = "DISEASE_ID";
        Disease disease = ModelCreationUtils.createDiseaseObject(uuid);
        disease.setDiseaseId(diseaseId);

        Mockito.when(this.diseaseService.findByDiseaseId(diseaseId)).thenReturn(Optional.of(disease));

        ResultActions res = this.mockMvc.perform
                (
                        MockMvcRequestBuilders.
                                get("/v1/ds/diseases/" + diseaseId).
                                param("diseaseId", diseaseId)
                );

        res.andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.jsonPath("$.requestId", Matchers.notNullValue()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.hasError", Matchers.equalTo(false)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.warnings", Matchers.nullValue()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.result", Matchers.notNullValue()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.result.diseaseId", Matchers.equalTo(diseaseId)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.result.diseaseName", Matchers.startsWith("DN")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.result.acronym", Matchers.startsWith("ACRONYM")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.result.description", Matchers.startsWith("DESC")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.result.proteins", Matchers.nullValue()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.result.variants", Matchers.nullValue()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.result.synonyms", Matchers.nullValue()));
        Assert.assertNotNull(res);
    }

    @Test
    public void testGetDiseaseWithOtherDetails() throws Exception {

        String diseaseId = "FULL_DISEASE";
        Disease disease = ModelCreationUtils.createDiseaseObject(uuid);
        disease.setDiseaseId(diseaseId);
        // synonyms
        Synonym s1 = ModelCreationUtils.createSynonymObject(uuid);
        Synonym s2 = ModelCreationUtils.createSynonymObject(uuid);
        disease.setSynonyms(Arrays.asList(s1, s2));

        // proteins
        Protein p1 = ModelCreationUtils.createProteinObject(uuid + 1);
        Protein p2 = ModelCreationUtils.createProteinObject(uuid + 2);
        Protein p3 = ModelCreationUtils.createProteinObject(uuid + 3);
        disease.setProteins(Arrays.asList(p1, p2, p3));

        // variants
        Variant v1 = ModelCreationUtils.createVariantObject(uuid + 1);
        Variant v2 = ModelCreationUtils.createVariantObject(uuid + 2);
        Variant v3 = ModelCreationUtils.createVariantObject(uuid + 3);
        Variant v4 = ModelCreationUtils.createVariantObject(uuid + 4);
        disease.setVariants(Arrays.asList(v1, v2, v3, v4));

        Mockito.when(this.diseaseService.findByDiseaseId(diseaseId)).thenReturn(Optional.of(disease));

        ResultActions res = this.mockMvc.
                perform(MockMvcRequestBuilders.get("/v1/ds/diseases/" + diseaseId).param("diseaseId", diseaseId));

        res.andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.jsonPath("$.requestId", Matchers.notNullValue()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.hasError", Matchers.equalTo(false)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.warnings", Matchers.nullValue()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.result", Matchers.notNullValue()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.result.diseaseId", Matchers.equalTo(diseaseId)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.result.diseaseName", Matchers.startsWith("DN")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.result.acronym", Matchers.startsWith("ACRONYM")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.result.description", Matchers.startsWith("DESC")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.result.proteins.length()", Matchers.equalTo(3)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.result.variants.length()", Matchers.equalTo(4)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.result.synonyms.length()", Matchers.equalTo(2)));
    }

    @Test
    public void testNonExistentDisease() throws Exception {

        String diseaseId = "randomDisease";
        Mockito.when(this.diseaseService.findByDiseaseId(diseaseId)).thenThrow(new AssetNotFoundException("Unable to find the diseaseId '" + diseaseId + "'."));
        ResultActions res = this.mockMvc.
                perform(MockMvcRequestBuilders.get("/v1/ds/diseases/" + diseaseId).param("diseaseId", diseaseId));
        res.andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.jsonPath("$.requestId", Matchers.notNullValue()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.hasError", Matchers.equalTo(true)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errorMessage", Matchers.startsWith("Unable to find the diseaseId")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errorCode", Matchers.equalTo(404)));
    }
}
