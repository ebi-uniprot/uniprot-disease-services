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
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import uk.ac.ebi.uniprot.ds.common.model.SiteMapping;
import uk.ac.ebi.uniprot.ds.rest.RestServiceSpringBootApplication;
import uk.ac.ebi.uniprot.ds.rest.dto.FeatureType;
import uk.ac.ebi.uniprot.ds.rest.service.DiseaseService;
import uk.ac.ebi.uniprot.ds.rest.service.DrugService;
import uk.ac.ebi.uniprot.ds.rest.service.ProteinService;
import uk.ac.ebi.uniprot.ds.rest.service.UniProtSiteMapService;
import uk.ac.ebi.uniprot.ds.rest.service.VariantService;
import uk.ac.ebi.uniprot.ds.rest.utils.ModelCreationUtils;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(UniProtSiteMapController.class)
@ContextConfiguration(classes = {RestServiceSpringBootApplication.class})
public class UniProtSiteMapControllerTest {
    private String uuid = UUID.randomUUID().toString();

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UniProtSiteMapService siteMapService;

    @MockBean
    private ProteinService proteinService;

    @MockBean
    private DiseaseService diseaseService;

    @MockBean
    private VariantService variantService;

    @MockBean
    private DrugService drugService;

    @Test
    public void testGetByAccession() throws Exception {
        String accession = "P12345";
        SiteMapping sm1 = ModelCreationUtils.createSiteMappingObject(uuid + "-0");
        sm1.setAccession(accession);
        SiteMapping sm2 = ModelCreationUtils.createSiteMappingObject(uuid + "-1");
        sm2.setAccession(accession);
        SiteMapping sm3 = ModelCreationUtils.createSiteMappingObject(uuid + "-2");
        sm3.setAccession(accession);

        List<SiteMapping> siteMappings = Arrays.asList(sm1, sm2, sm3);

        Mockito.when(this.siteMapService.getSiteMappings(accession)).thenReturn(siteMappings);

        ResultActions res = this.mockMvc.perform
                (
                        MockMvcRequestBuilders.
                                get("/v1/ds/ortholog-mappings/" + accession).
                                param("accession", accession)
                );

        res.andDo(MockMvcResultHandlers.print())
                .andExpect(jsonPath("$.requestId", notNullValue()))
                .andExpect(jsonPath("$.hasError", equalTo(false)))
                .andExpect(jsonPath("$.warnings", nullValue()))
                .andExpect(jsonPath("$.results", notNullValue()))
                .andExpect(jsonPath("$.results.length()", equalTo(3)))
                .andExpect(jsonPath("$.results[*].accession", contains(accession, accession, accession)))
                .andExpect(jsonPath("$.results[*].uniProtId", notNullValue()))
                .andExpect(jsonPath("$.results[*].unirefId", notNullValue()))
                .andExpect(jsonPath("$.results[*].sitePosition", notNullValue()))
                .andExpect(jsonPath("$.results[*].positionInAlignment", notNullValue()))
                .andExpect(jsonPath("$.results[0].featureTypes.length()", equalTo(2)))
                .andExpect(jsonPath("$.results[0].featureTypes", containsInAnyOrder(FeatureType.MUTAGENESIS.toString(), FeatureType.VARIANT.toString())))
                .andExpect(jsonPath("$.results[0].dbSnps.length()", equalTo(1)))
                .andExpect(jsonPath("$.results[0].dbSnps", contains("rs397507523")))
                .andExpect(jsonPath("$.results[0].mappedSites.length()", equalTo(5)))
                .andExpect(jsonPath("$.results[0].mappedSites[*].accession").exists())
                .andExpect(jsonPath("$.results[0].mappedSites[*].accession", notNullValue()))
                .andExpect(jsonPath("$.results[0].mappedSites[*].uniProtId").exists())
                .andExpect(jsonPath("$.results[0].mappedSites[*].uniProtId", notNullValue()))
                .andExpect(jsonPath("$.results[0].mappedSites[*].position").exists())
                .andExpect(jsonPath("$.results[0].mappedSites[*].position", notNullValue()))
                .andExpect(jsonPath("$.results[0].mappedSites[*].new").exists())
                .andExpect(jsonPath("$.results[0].mappedSites[*].new", notNullValue()));
    }

    @Test
    public void testGetByAccessionNotFound() throws Exception {
        String accession = "P12345";
        List<SiteMapping> siteMappings = new ArrayList<>();

        Mockito.when(this.siteMapService.getSiteMappings(accession)).thenReturn(siteMappings);

        ResultActions res = this.mockMvc.perform
                (
                        MockMvcRequestBuilders.
                                get("/v1/ds/ortholog-mappings/" + accession).
                                param("accession", accession)
                );

        res.andDo(MockMvcResultHandlers.print())
                .andExpect(jsonPath("$.requestId", notNullValue()))
                .andExpect(jsonPath("$.hasError", equalTo(false)))
                .andExpect(jsonPath("$.warnings", nullValue()))
                .andExpect(jsonPath("$.results", notNullValue()))
                .andExpect(jsonPath("$.results.length()", equalTo(0)));
    }

    @Test
    public void testGetByEmptyAccession() throws Exception {
        String accession = "";
        List<SiteMapping> siteMappings = new ArrayList<>();

        Mockito.when(this.siteMapService.getSiteMappings(accession)).thenReturn(siteMappings);

        ResultActions res = this.mockMvc.perform
                (
                        MockMvcRequestBuilders.
                                get("/v1/ds/ortholog-mappings/" + accession).
                                param("accession", accession)
                );

        res.andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(HttpStatus.NOT_FOUND.value()));
    }
}
