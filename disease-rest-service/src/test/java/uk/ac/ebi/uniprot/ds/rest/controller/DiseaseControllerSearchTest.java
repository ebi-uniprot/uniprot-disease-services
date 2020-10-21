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
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import uk.ac.ebi.uniprot.ds.common.dao.DrugDAO;
import uk.ac.ebi.uniprot.ds.common.model.Disease;
import uk.ac.ebi.uniprot.ds.rest.DataSourceTestConfig;
import uk.ac.ebi.uniprot.ds.rest.service.DiseaseService;
import uk.ac.ebi.uniprot.ds.rest.service.DrugService;
import uk.ac.ebi.uniprot.ds.rest.service.ProteinService;
import uk.ac.ebi.uniprot.ds.rest.service.UniProtSiteMapService;
import uk.ac.ebi.uniprot.ds.rest.service.VariantService;
import uk.ac.ebi.uniprot.ds.rest.utils.ModelCreationUtils;

import java.util.*;
import java.util.stream.IntStream;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@RunWith(SpringRunner.class)
@WebMvcTest(DiseaseController.class)
@Import({DataSourceTestConfig.class})
//@ComponentScan(basePackages = {"uk.ac.ebi.uniprot.ds.rest"})
public class DiseaseControllerSearchTest {
    private String uuid = UUID.randomUUID().toString();

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DiseaseService diseaseService;

    @MockBean
    private ProteinService proteinService;

    @MockBean
    private VariantService variantService;

    @MockBean
    private DrugService drugService;

    @MockBean
    private UniProtSiteMapService uniProtSiteMapService;

    @MockBean
    private DrugDAO drugDAO;

    @Test
    public void testSearchDisease() throws Exception {
        String keyword = "syndrome";
        List<Disease> diseases = new ArrayList<>();
        // create 10 disease objects

        IntStream.range(0, 10).forEach(i -> diseases.add(ModelCreationUtils.createDiseaseObject(uuid + i)));

        Mockito.when(this.diseaseService.searchDiseases(keyword, 0, 10)).thenReturn(diseases);

        ResultActions res = this.mockMvc.perform
                (
                        MockMvcRequestBuilders.
                                get("/diseaseservice/api/diseases/search/" + keyword).
                                param("keyword", keyword).
                                param("offset", "0").
                                param("size", "10")
                );

        res.andDo(MockMvcResultHandlers.print())
                .andExpect(jsonPath("$.requestId", notNullValue()))
                .andExpect(jsonPath("$.hasError", equalTo(false)))
                .andExpect(jsonPath("$.warnings", nullValue()))
                .andExpect(jsonPath("$.total", nullValue()))
                .andExpect(jsonPath("$.offset", equalTo(0)))
                .andExpect(jsonPath("$.maxReturn", equalTo(diseases.size())))
                .andExpect(jsonPath("$.results", notNullValue()))
                .andExpect(jsonPath("$.results.length()", equalTo(diseases.size())))
                .andExpect(jsonPath("$.results[*].diseaseId", notNullValue()))
                .andExpect(jsonPath("$.results[*].diseaseName", notNullValue()))
                .andExpect(jsonPath("$.results[*].acronym", notNullValue()))
                .andExpect(jsonPath("$.results[*].description", notNullValue()));
    }

    @Test
    public void testSearchDiseaseWithNegativeOffset() throws Exception {
        String keyword = "syndrome";
        ResultActions res = this.mockMvc.perform
                (
                        MockMvcRequestBuilders.
                                get("/diseaseservice/api/diseases/search/" + keyword).
                                param("keyword", keyword).
                                param("offset", "-1").
                                param("size", "10")
                );

        res.andDo(MockMvcResultHandlers.print())
                .andExpect(jsonPath("$.requestId", notNullValue()))
                .andExpect(jsonPath("$.hasError", equalTo(true)))
                .andExpect(jsonPath("$.warnings", nullValue()))
                .andExpect(jsonPath("$.errorCode", equalTo(HttpStatus.BAD_REQUEST.value())))
                .andExpect(jsonPath("$.errorMessage", equalTo("The offset cannot be negative.")));
    }

    @Test
    public void testSearchDiseaseSizeMoreThan200() throws Exception {
        String keyword = "syndrome";
        ResultActions res = this.mockMvc.perform
                (
                        MockMvcRequestBuilders.
                                get("/diseaseservice/api/diseases/search/" + keyword).
                                param("keyword", keyword).
                                param("offset", "0").
                                param("size", "210")
                );

        res.andDo(MockMvcResultHandlers.print())
                .andExpect(jsonPath("$.requestId", notNullValue()))
                .andExpect(jsonPath("$.hasError", equalTo(true)))
                .andExpect(jsonPath("$.warnings", nullValue()))
                .andExpect(jsonPath("$.errorCode", equalTo(HttpStatus.BAD_REQUEST.value())))
                .andExpect(jsonPath("$.errorMessage", equalTo("The size must be between 1 and 200 both inclusive.")));
    }
}
