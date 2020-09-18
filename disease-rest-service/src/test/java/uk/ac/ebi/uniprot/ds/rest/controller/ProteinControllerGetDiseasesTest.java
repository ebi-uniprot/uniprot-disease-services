/*
 * Created by sahmad on 07/02/19 15:02
 * UniProt Consortium.
 * Copyright (c) 2002-2019.
 *
 */

package uk.ac.ebi.uniprot.ds.rest.controller;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import uk.ac.ebi.uniprot.ds.common.DiseaseCommonSpringBootApplication;
import uk.ac.ebi.uniprot.ds.common.dao.DrugDAO;
import uk.ac.ebi.uniprot.ds.common.model.Disease;
import uk.ac.ebi.uniprot.ds.common.model.DiseaseProtein;
import uk.ac.ebi.uniprot.ds.common.model.Protein;
import uk.ac.ebi.uniprot.ds.rest.DataSourceTestConfig;
import uk.ac.ebi.uniprot.ds.rest.RestServiceSpringBootApplication;
import uk.ac.ebi.uniprot.ds.rest.service.DiseaseService;
import uk.ac.ebi.uniprot.ds.rest.service.DrugService;
import uk.ac.ebi.uniprot.ds.rest.service.ProteinService;
import uk.ac.ebi.uniprot.ds.rest.service.UniProtSiteMapService;
import uk.ac.ebi.uniprot.ds.rest.service.VariantService;
import uk.ac.ebi.uniprot.ds.rest.utils.ModelCreationUtils;

import java.util.*;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@RunWith(SpringRunner.class)
@WebMvcTest(controllers = {ProteinController.class})
@Import(value = {DataSourceTestConfig.class, RestServiceSpringBootApplication.class,
        DiseaseCommonSpringBootApplication.class})
public class ProteinControllerGetDiseasesTest {
    private String uuid = UUID.randomUUID().toString();

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProteinService proteinService;

    @MockBean
    private DiseaseService diseaseService;

    @MockBean
    private VariantService variantService;

    @MockBean
    private DrugService drugService;

    @MockBean
    private UniProtSiteMapService uniProtSiteMapService;

    @MockBean
    private DrugDAO drugDAO;

    @Test
    public void testNonExistentAccessions() throws Exception {
        String accessions = "acc1,acc2,acc3,acc4";
        Mockito.when(this.proteinService.getAllProteinsByAccessions(ArgumentMatchers.anyList())).thenReturn(new ArrayList<>());
        ResultActions res = this.mockMvc.perform
                (
                        MockMvcRequestBuilders.
                                get("/v1/ds/proteins/" + accessions +"/diseases").
                                param("accessions", accessions)
                );

        res.andDo(MockMvcResultHandlers.print())
                .andExpect(jsonPath("$.requestId", notNullValue()))
                .andExpect(jsonPath("$.hasError", equalTo(false)))
                .andExpect(jsonPath("$.warnings", nullValue()))
                .andExpect(jsonPath("$.results", notNullValue()))
                .andExpect(jsonPath("$.results.length()", equalTo(0)));
    }

    @Test
    public void testGetDiseases() throws Exception {

        // create multiple proteins
        Protein p1 = ModelCreationUtils.createProteinObject(uuid + 1);
        String a1 = "ACC1-"+ uuid;
        p1.setAccession(a1);
        Disease d1 = ModelCreationUtils.createDiseaseObject(uuid + 1);
        Disease d2 = ModelCreationUtils.createDiseaseObject(uuid + 2);
        Disease d3 = ModelCreationUtils.createDiseaseObject(uuid + 3);

        DiseaseProtein dp1 = new DiseaseProtein(d1, p1, true);
        DiseaseProtein dp2 = new DiseaseProtein(d2, p1, true);
        DiseaseProtein dp3 = new DiseaseProtein(d3, p1, true);

        p1.setDiseaseProteins(new HashSet<>(Arrays.asList(dp1, dp2, dp3)));

        Protein p2 = ModelCreationUtils.createProteinObject(uuid + 2);
        String a2 = "ACC2-"+ uuid;
        p2.setAccession(a2);
        Disease d4 = ModelCreationUtils.createDiseaseObject(uuid + 4);
        DiseaseProtein dp4 = new DiseaseProtein(d4, p2, true);
        p2.setDiseaseProteins(new HashSet<>(Arrays.asList(dp4)));

        Protein p3 = ModelCreationUtils.createProteinObject(uuid + 3);
        String a3 = "ACC3-"+ uuid;
        p3.setAccession(a3);

        List<Protein> proteins = Arrays.asList(p1, p2, p3);

        Mockito.when(this.proteinService.getAllProteinsByAccessions(Arrays.asList(a1, a2, a3))).thenReturn(proteins);
        String accessions = a1 + "," + a2 + "," + a3;

        ResultActions res = this.mockMvc.
                perform(MockMvcRequestBuilders.get("/v1/ds/proteins/" + accessions + "/diseases").param("accessions", accessions));

        res.andDo(MockMvcResultHandlers.print())
                .andExpect(jsonPath("$.requestId", notNullValue()))
                .andExpect(jsonPath("$.hasError", equalTo(false)))
                .andExpect(jsonPath("$.warnings", nullValue()))
                .andExpect(jsonPath("$.results", notNullValue()))
                .andExpect(jsonPath("$.results.length()", equalTo(proteins.size())))
                .andExpect(jsonPath("$.results[*].accession", notNullValue()))
                .andExpect(jsonPath("$.results[*].proteinId", notNullValue()))
                .andExpect(jsonPath("$.results[*].proteinName", notNullValue()))
                .andExpect(jsonPath("$.results[*].gene", notNullValue()))
                .andExpect(jsonPath("$.results[0].diseases.length()", equalTo(p1.getDiseaseProteins().size())))
                .andExpect(jsonPath("$.results[0].diseases[*].diseaseId", notNullValue()))
                .andExpect(jsonPath("$.results[0].diseases[*].acronym", notNullValue()))
                .andExpect(jsonPath("$.results[1].diseases.length()", equalTo(p2.getDiseaseProteins().size())))
                .andExpect(jsonPath("$.results[2].diseases", nullValue()));
    }

    @Test
    public void testMoreThan200Accession() throws Exception {
        String accessions = "acc1,acc2,acc3,acc4,acc5,acc1,acc2,acc3,acc4,acc5,acc1,acc2,acc3,acc4,acc5,acc1,acc2,acc3,acc4,acc5,acc5," +
                "acc1,acc2,acc3,acc4,acc5,acc1,acc2,acc3,acc4,acc5,acc1,acc2,acc3,acc4,acc5,acc1,acc2,acc3,acc4,acc5,acc5," +
                "acc1,acc2,acc3,acc4,acc5,acc1,acc2,acc3,acc4,acc5,acc1,acc2,acc3,acc4,acc5,acc1,acc2,acc3,acc4,acc5,acc5," +
                "acc1,acc2,acc3,acc4,acc5,acc1,acc2,acc3,acc4,acc5,acc1,acc2,acc3,acc4,acc5,acc1,acc2,acc3,acc4,acc5,acc5," +
                "acc1,acc2,acc3,acc4,acc5,acc1,acc2,acc3,acc4,acc5,acc1,acc2,acc3,acc4,acc5,acc1,acc2,acc3,acc4,acc5,acc5," +
                "acc1,acc2,acc3,acc4,acc5,acc1,acc2,acc3,acc4,acc5,acc1,acc2,acc3,acc4,acc5,acc1,acc2,acc3,acc4,acc5,acc5," +
                "acc1,acc2,acc3,acc4,acc5,acc1,acc2,acc3,acc4,acc5,acc1,acc2,acc3,acc4,acc5,acc1,acc2,acc3,acc4,acc5,acc5," +
                "acc1,acc2,acc3,acc4,acc5,acc1,acc2,acc3,acc4,acc5,acc1,acc2,acc3,acc4,acc5,acc1,acc2,acc3,acc4,acc5,acc5," +
                "acc1,acc2,acc3,acc4,acc5,acc1,acc2,acc3,acc4,acc5,acc1,acc2,acc3,acc4,acc5,acc1,acc2,acc3,acc4,acc5,acc5," +
                "acc1,acc2,acc3,acc4,acc5,acc1,acc2,acc3,acc4,acc5,acc1,acc2,acc3,acc4,acc5,acc1,acc2,acc3,acc4,acc5,acc5," +
                "acc1,acc2,acc3,acc4,acc5,acc1,acc2,acc3,acc4,acc5,acc1,acc2,acc3,acc4,acc5,acc1,acc2,acc3,acc4,acc5,acc5,";

        Mockito.when(this.proteinService.getAllProteinsByAccessions(ArgumentMatchers.anyList())).thenReturn(new ArrayList<>());
        ResultActions res = this.mockMvc.perform
                (
                        MockMvcRequestBuilders.
                                get("/v1/ds/proteins/" + accessions +"/diseases").
                                param("accessions", accessions)
                );

        res.andDo(MockMvcResultHandlers.print())
                .andExpect(jsonPath("$.requestId", notNullValue()))
                .andExpect(jsonPath("$.hasError", equalTo(true)))
                .andExpect(jsonPath("$.warnings", nullValue()))
                .andExpect(jsonPath("$.errorCode", equalTo(HttpStatus.BAD_REQUEST.value())))
                .andExpect(jsonPath("$.errorMessage", equalTo("The total count of accessions passed must be between 1 and 200 both inclusive.")));
    }


}
