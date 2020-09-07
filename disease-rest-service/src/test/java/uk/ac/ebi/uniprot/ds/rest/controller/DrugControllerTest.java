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
import uk.ac.ebi.uniprot.ds.rest.service.DrugService;
import uk.ac.ebi.uniprot.ds.rest.service.ProteinService;
import uk.ac.ebi.uniprot.ds.rest.service.UniProtSiteMapService;
import uk.ac.ebi.uniprot.ds.rest.service.VariantService;
import uk.ac.ebi.uniprot.ds.rest.utils.ModelCreationUtils;

import java.util.*;

import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.equalTo;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@RunWith(SpringRunner.class)
@WebMvcTest(DiseaseController.class)
@Import({DataSourceTestConfig.class})
public class DrugControllerTest {
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

    @Test
    public void testGetProteinsByDrugName() throws Exception {

        // create a drug
        Drug drug = ModelCreationUtils.createDrugObject(uuid);
        // create protein cross ref
        ProteinCrossRef pt1 = ModelCreationUtils.createProteinXRefObject(uuid );
        pt1.setDbType("Reactome");
        drug.setProteinCrossRef(pt1);

        // create protein
        Protein p1 = ModelCreationUtils.createProteinObject(uuid);
        p1.setProteinCrossRefs(Arrays.asList(pt1));
        p1.setIsExternallyMapped(false);



        String drugName = uuid;
        Mockito.when(this.proteinService.getProteinsByDrugName(drugName)).thenReturn(Arrays.asList(p1));

        ResultActions res = this.mockMvc.perform
                (
                        MockMvcRequestBuilders.
                                get("/v1/ds/drug/" + drugName + "/proteins").
                                param("drugName", drugName)
                );

        res.andDo(MockMvcResultHandlers.print())
                .andExpect(jsonPath("$.requestId", notNullValue()))
                .andExpect(jsonPath("$.hasError", equalTo(false)))
                .andExpect(jsonPath("$.warnings", nullValue()))
                .andExpect(jsonPath("$.results.length()", equalTo(1)))
                .andExpect(jsonPath("$.results[0].proteinId", equalTo(p1.getProteinId())))
                .andExpect(jsonPath("$.results[0].proteinName", equalTo(p1.getName())))
                .andExpect(jsonPath("$.results[0].accession", equalTo(p1.getAccession())))
                .andExpect(jsonPath("$.results[0].gene", equalTo(p1.getGene())))
                .andExpect(jsonPath("$.results[0].description", equalTo(p1.getDesc())))
                .andExpect(jsonPath("$.results[0].pathways.length()", equalTo(p1.getProteinCrossRefs().size())))
                .andExpect(jsonPath("$.results[0].interactions", Matchers.nullValue()))
                .andExpect(jsonPath("$.results[0].variants", Matchers.nullValue()))
                .andExpect(jsonPath("$.results[0].diseases", Matchers.nullValue()))
                .andExpect(jsonPath("$.results[0].isExternallyMapped", equalTo(false)))
                .andExpect(jsonPath("$.results[0].geneCoordinates.length()", equalTo(p1.getGeneCoordinates().size())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.results[0].publications", Matchers.nullValue()));
    }

    @Test
    public void testGetDiseasesByDrugName() throws Exception {

        // create a drug
        Drug drug = ModelCreationUtils.createDrugObject(uuid);
        // create protein cross ref
        ProteinCrossRef pt1 = ModelCreationUtils.createProteinXRefObject(uuid );
        pt1.setDbType("Reactome");
        drug.setProteinCrossRef(pt1);

        // create protein
        Protein p1 = ModelCreationUtils.createProteinObject(uuid);
        p1.setProteinCrossRefs(Arrays.asList(pt1));
        p1.setIsExternallyMapped(false);

        // create disease
        Disease disease = ModelCreationUtils.createDiseaseObject(uuid);
        DiseaseProtein dp = new DiseaseProtein(disease, p1, false);
        disease.setDiseaseProteins(new HashSet<>(Arrays.asList(dp)));


        String drugName = uuid;
        Mockito.when(this.diseaseService.getDiseasesByDrugName(drugName)).thenReturn(Arrays.asList(disease));

        ResultActions res = this.mockMvc.perform
                (
                        MockMvcRequestBuilders.
                                get("/v1/ds/drug/" + drugName + "/diseases").
                                param("drugName", drugName)
                );

        res.andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.jsonPath("$.requestId", Matchers.notNullValue()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.hasError", Matchers.equalTo(false)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.warnings", Matchers.nullValue()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.results", Matchers.notNullValue()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.results.length()", Matchers.equalTo(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.results[*].diseaseId", Matchers.notNullValue()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.results[*].diseaseName", Matchers.notNullValue()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.results[*].acronym", Matchers.notNullValue()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.results[*].description", Matchers.notNullValue()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.results[*].proteins").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.results[*].variants").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.results[*].synonyms").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.results[*].drugs").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.results[*].publications").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.results[*].children").exists());
    }
}
