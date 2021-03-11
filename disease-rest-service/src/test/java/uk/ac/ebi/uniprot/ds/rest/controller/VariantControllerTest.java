/*
 * Created by sahmad on 07/02/19 15:02
 * UniProt Consortium.
 * Copyright (c) 2002-2019.
 *
 */

package uk.ac.ebi.uniprot.ds.rest.controller;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import uk.ac.ebi.uniprot.ds.common.dao.DrugDAO;
import uk.ac.ebi.uniprot.ds.common.model.*;
import uk.ac.ebi.uniprot.ds.rest.RestServiceSpringBootApplication;
import uk.ac.ebi.uniprot.ds.rest.service.DiseaseService;
import uk.ac.ebi.uniprot.ds.rest.service.DrugService;
import uk.ac.ebi.uniprot.ds.rest.service.ProteinService;
import uk.ac.ebi.uniprot.ds.rest.service.UniProtSiteMapService;
import uk.ac.ebi.uniprot.ds.rest.service.VariantService;
import uk.ac.ebi.uniprot.ds.rest.utils.ModelCreationUtils;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@RunWith(SpringRunner.class)
@WebMvcTest(ProteinController.class)
@ContextConfiguration(classes={RestServiceSpringBootApplication.class})
public class VariantControllerTest {
    private String uuid = UUID.randomUUID().toString();

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private VariantService variantService;

    @MockBean
    private ProteinService proteinService;

    @MockBean
    private DiseaseService diseaseService;

    @MockBean
    private DrugService drugService;

    @MockBean
    private UniProtSiteMapService uniProtSiteMapService;

    @MockBean
    private DrugDAO drugDAO;

    @Test
    public void testGetVariants() throws Exception {
        String accession = "ACCESSION_ID";
        Variant v1 = ModelCreationUtils.createVariantObject(this.uuid+1);
        FeatureLocation fl = ModelCreationUtils.createFeatureLocationObject(this.uuid+1);
        v1.setFeatureLocation(fl);
        Variant v2 = ModelCreationUtils.createVariantObject(this.uuid+2);
        FeatureLocation fl1 = ModelCreationUtils.createFeatureLocationObject(this.uuid+2);
        v2.setFeatureLocation(fl1);
        List<Variant> variants = Arrays.asList(v1, v2);

        Mockito.when(this.variantService.getVariantsByAccession(accession)).thenReturn(variants);

        ResultActions res = this.mockMvc.perform
                (
                        MockMvcRequestBuilders.
                                get("/protein/" + accession + "/variants").
                                param("accession", accession)
                );

        res.andDo(MockMvcResultHandlers.print())
                .andExpect(jsonPath("$.requestId", notNullValue()))
                .andExpect(jsonPath("$.hasError", equalTo(false)))
                .andExpect(jsonPath("$.warnings", nullValue()))
                .andExpect(jsonPath("$.total", nullValue()))
                .andExpect(jsonPath("$.offset", nullValue()))
                .andExpect(jsonPath("$.maxReturn", nullValue()))
                .andExpect(jsonPath("$.results", notNullValue()))
                .andExpect(jsonPath("$.results.length()", equalTo(variants.size())))
                .andExpect(jsonPath("$.results[*].origSeq", notNullValue()))
                .andExpect(jsonPath("$.results[*].altSeq", notNullValue()))
                .andExpect(jsonPath("$.results[*].featureId", notNullValue()))
                .andExpect(jsonPath("$.results[*].report", notNullValue()))
                .andExpect(jsonPath("$.results[*].featureStatus", notNullValue()))
                .andExpect(jsonPath("$.results[*].featureLocation", notNullValue()))
                .andExpect(jsonPath("$.results[*].featureLocation.startModifier", notNullValue()))
                .andExpect(jsonPath("$.results[*].featureLocation.endModifier", notNullValue()))
                .andExpect(jsonPath("$.results[*].featureLocation.startId", notNullValue()))
                .andExpect(jsonPath("$.results[*].featureLocation.endId", notNullValue()));
    }

    @Test
    public void testGetVariantsByDiseaseId() throws Exception {
        String diseaseId = "DI-00001";
        Variant v1 = ModelCreationUtils.createVariantObject(this.uuid+1);
        FeatureLocation fl = ModelCreationUtils.createFeatureLocationObject(this.uuid+1);
        v1.setFeatureLocation(fl);
        Protein p1 = ModelCreationUtils.createProteinObject(this.uuid + 1);
        v1.setProtein(p1);
        Variant v2 = ModelCreationUtils.createVariantObject(this.uuid+2);
        FeatureLocation fl1 = ModelCreationUtils.createFeatureLocationObject(this.uuid+2);
        v2.setFeatureLocation(fl1);
        Protein p2 = ModelCreationUtils.createProteinObject(this.uuid + 2);
        v2.setProtein(p2);
        List<Variant> variants = Arrays.asList(v1, v2);

        Mockito.when(this.variantService.getVariantsByDiseaseId(diseaseId)).thenReturn(variants);

        ResultActions res = this.mockMvc.perform
                (
                        MockMvcRequestBuilders.
                                get("/disease/" + diseaseId + "/variants").
                                param("diseaseId", diseaseId)
                );

        res.andDo(MockMvcResultHandlers.print())
                .andExpect(jsonPath("$.requestId", notNullValue()))
                .andExpect(jsonPath("$.hasError", equalTo(false)))
                .andExpect(jsonPath("$.warnings", nullValue()))
                .andExpect(jsonPath("$.total", nullValue()))
                .andExpect(jsonPath("$.offset", nullValue()))
                .andExpect(jsonPath("$.maxReturn", nullValue()))
                .andExpect(jsonPath("$.results", notNullValue()))
                .andExpect(jsonPath("$.results.length()", equalTo(variants.size())))
                .andExpect(jsonPath("$.results[*].origSeq", notNullValue()))
                .andExpect(jsonPath("$.results[*].proteinAccession", notNullValue()))
                .andExpect(jsonPath("$.results[*].altSeq", notNullValue()))
                .andExpect(jsonPath("$.results[*].featureId", notNullValue()))
                .andExpect(jsonPath("$.results[*].report", notNullValue()))
                .andExpect(jsonPath("$.results[*].featureStatus", notNullValue()))
                .andExpect(jsonPath("$.results[*].featureLocation", notNullValue()))
                .andExpect(jsonPath("$.results[*].featureLocation.startModifier", notNullValue()))
                .andExpect(jsonPath("$.results[*].featureLocation.endModifier", notNullValue()))
                .andExpect(jsonPath("$.results[*].featureLocation.startId", notNullValue()))
                .andExpect(jsonPath("$.results[*].featureLocation.endId", notNullValue()));
    }
}
