/*
 * Created by sahmad on 04/02/19 09:56
 * UniProt Consortium.
 * Copyright (c) 2002-2019.
 *
 */

package uk.ac.ebi.uniprot.ds.controller;

import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
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
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.method.annotation.ExceptionHandlerMethodResolver;
import org.springframework.web.servlet.mvc.method.annotation.ExceptionHandlerExceptionResolver;
import org.springframework.web.servlet.mvc.method.annotation.ServletInvocableHandlerMethod;
import uk.ac.ebi.uniprot.ds.controller.error.DiseaseControllerAdvice;
import uk.ac.ebi.uniprot.ds.exception.AssetNotFoundException;
import uk.ac.ebi.uniprot.ds.model.*;
import uk.ac.ebi.uniprot.ds.service.DiseaseService;

import java.lang.reflect.Method;
import java.util.*;

@RunWith(SpringRunner.class)
@WebMvcTest(DiseaseController.class)
public class DiseaseControllerIT {
    private String uuid = UUID.randomUUID().toString();

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DiseaseService diseaseService;

    @Before
    public void setUp(){
        //this.mockMvc = MockMvcBuilders.standaloneSetup(diseaseController).setControllerAdvice(new DiseaseControllerAdvice()).build();
    }

    private ExceptionHandlerExceptionResolver createExceptionResolver() {
        ExceptionHandlerExceptionResolver exceptionResolver = new ExceptionHandlerExceptionResolver() {
            protected ServletInvocableHandlerMethod getExceptionHandlerMethod(HandlerMethod handlerMethod, Exception exception) {
                Method method = new ExceptionHandlerMethodResolver(DiseaseControllerAdvice.class).resolveMethod(exception);
                return new ServletInvocableHandlerMethod(new DiseaseControllerAdvice(), method);
            }
        };
        exceptionResolver.afterPropertiesSet();
        return exceptionResolver;
    }

    @Test
    public void testGetDisease() throws Exception {
        String diseaseId = "DISEASE_ID";
        Disease disease = DiseaseTest.createDiseaseObject(uuid);
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
