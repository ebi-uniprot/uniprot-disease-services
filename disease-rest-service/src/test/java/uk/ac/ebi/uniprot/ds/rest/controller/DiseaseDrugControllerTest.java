/*
 * Created by sahmad on 07/02/19 15:02
 * UniProt Consortium.
 * Copyright (c) 2002-2019.
 *
 */

package uk.ac.ebi.uniprot.ds.rest.controller;

import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import uk.ac.ebi.uniprot.ds.common.model.*;
import uk.ac.ebi.uniprot.ds.rest.DataSourceTestConfig;
import uk.ac.ebi.uniprot.ds.rest.service.DiseaseService;
import uk.ac.ebi.uniprot.ds.rest.service.ProteinService;
import uk.ac.ebi.uniprot.ds.rest.service.VariantService;
import uk.ac.ebi.uniprot.ds.rest.utils.ModelCreationUtils;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@RunWith(SpringRunner.class)
@WebMvcTest(DiseaseController.class)
@Import({DataSourceTestConfig.class})
//@ComponentScan(basePackages = {"uk.ac.ebi.uniprot.ds.rest"})
public class DiseaseDrugControllerTest {
    private String uuid = UUID.randomUUID().toString();

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DiseaseService diseaseService;

    @MockBean
    private ProteinService proteinService;

    @MockBean
    private VariantService variantService;

    @Test
    public void testGetDrugsByDiseaseId() throws Exception {
        String diseaseId = "DISEASE_ID";

        Drug drug1 = ModelCreationUtils.createDrugObject(this.uuid + 1);
        Drug drug2 = ModelCreationUtils.createDrugObject(this.uuid + 2);
        Drug drug3 = ModelCreationUtils.createDrugObject(this.uuid + 3);

        List<Drug> drugs = Arrays.asList(drug1, drug2, drug3);


        Mockito.when(this.diseaseService.getDrugsByDiseaseId(diseaseId)).thenReturn(drugs);

        ResultActions res = this.mockMvc.perform
                (
                        MockMvcRequestBuilders.
                                get("/v1/ds/disease/" + diseaseId + "/drugs").
                                param("diseaseId", diseaseId)
                );

        res.andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.jsonPath("$.requestId", Matchers.notNullValue()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.hasError", Matchers.equalTo(false)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.warnings", Matchers.nullValue()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.results.length()", Matchers.equalTo(drugs.size())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.results[*].name", Matchers.notNullValue()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.results[*].sourceType", Matchers.notNullValue()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.results[*].sourceId", Matchers.notNullValue()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.results[*].moleculeType", Matchers.notNullValue()));
    }
}