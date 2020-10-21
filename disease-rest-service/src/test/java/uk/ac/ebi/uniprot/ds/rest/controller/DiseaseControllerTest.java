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
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import uk.ac.ebi.uniprot.ds.common.dao.DrugDAO;
import uk.ac.ebi.uniprot.ds.common.model.*;
import uk.ac.ebi.uniprot.ds.rest.DataSourceTestConfig;
import uk.ac.ebi.uniprot.ds.rest.exception.AssetNotFoundException;
import uk.ac.ebi.uniprot.ds.rest.service.DiseaseService;
import uk.ac.ebi.uniprot.ds.rest.service.DrugService;
import uk.ac.ebi.uniprot.ds.rest.service.ProteinService;
import uk.ac.ebi.uniprot.ds.rest.service.UniProtSiteMapService;
import uk.ac.ebi.uniprot.ds.rest.service.VariantService;
import uk.ac.ebi.uniprot.ds.rest.utils.ModelCreationUtils;

import java.util.*;

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

    @MockBean
    private VariantService variantService;

    @MockBean
    private DrugService drugService;

    @MockBean
    private UniProtSiteMapService uniProtSiteMapService;

    @MockBean
    private DrugDAO drugDAO;

    @Test
    public void testGetDisease() throws Exception {
        String diseaseId = "DISEASE_ID";
        Disease disease = ModelCreationUtils.createDiseaseObject(uuid);
        disease.setDiseaseId(diseaseId);

        Mockito.when(this.diseaseService.findByDiseaseId(diseaseId)).thenReturn(Optional.of(disease));

        ResultActions res = this.mockMvc.perform
                (
                        MockMvcRequestBuilders.
                                get("/diseaseservice/api/diseases/" + diseaseId).
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
                .andExpect(MockMvcResultMatchers.jsonPath("$.result.synonyms", Matchers.nullValue()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.result.isGroup").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.result.isGroup", Matchers.equalTo(false)));
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

        DiseaseProtein dp1 = new DiseaseProtein(disease, p1, true);
        DiseaseProtein dp2 = new DiseaseProtein(disease, p2, true);
        DiseaseProtein dp3 = new DiseaseProtein(disease, p3, false);

        disease.setDiseaseProteins(new HashSet<>(Arrays.asList(dp1, dp2, dp3)));

        // variants
        Variant v1 = ModelCreationUtils.createVariantObject(uuid + 1);
        Variant v2 = ModelCreationUtils.createVariantObject(uuid + 2);
        Variant v3 = ModelCreationUtils.createVariantObject(uuid + 3);
        Variant v4 = ModelCreationUtils.createVariantObject(uuid + 4);
        disease.setVariants(Arrays.asList(v1, v2, v3, v4));

        // create few publications
        Publication pb1 = ModelCreationUtils.createPublicationObject(this.uuid + 1);
        Publication pb2 = ModelCreationUtils.createPublicationObject(this.uuid + 2);
        Publication pb3 = ModelCreationUtils.createPublicationObject(this.uuid + 3);
        Publication pb4 = ModelCreationUtils.createPublicationObject(this.uuid + 4);
        disease.setPublications(Arrays.asList(pb1, pb2, pb3, pb4));

        // create few parent diseases
        Disease pd1 = ModelCreationUtils.createDiseaseObject(this.uuid + 1);
        Disease pd2 = ModelCreationUtils.createDiseaseObject(this.uuid + 2);
        Disease pd3 = ModelCreationUtils.createDiseaseObject(this.uuid + 3);
        disease.setParents(Arrays.asList(pd1, pd2, pd3));

        // create few child diseases
        Disease cd1 = ModelCreationUtils.createDiseaseObject(this.uuid + 4);
        Disease cd2 = ModelCreationUtils.createDiseaseObject(this.uuid + 5);
        disease.setChildren(Arrays.asList(cd1, cd2));

        Mockito.when(this.diseaseService.findByDiseaseId(diseaseId)).thenReturn(Optional.of(disease));

        ResultActions res = this.mockMvc.
                perform(MockMvcRequestBuilders.get("/diseaseservice/api/diseases/" + diseaseId).param("diseaseId", diseaseId));

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
                .andExpect(MockMvcResultMatchers.jsonPath("$.result.proteins[*].isExternallyMapped", Matchers.notNullValue()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.result.proteins[*].accession", Matchers.notNullValue()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.result.variants.length()", Matchers.equalTo(4)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.result.synonyms.length()", Matchers.equalTo(2)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.result.publications.length()", Matchers.equalTo(4)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.result.children.length()", Matchers.equalTo(disease.getChildren().size())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.result.publications[*].type", Matchers.notNullValue()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.result.publications[*].id", Matchers.notNullValue()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.result.isGroup").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.result.isGroup", Matchers.equalTo(false)));
    }

    @Test
    public void testNonExistentDisease() throws Exception {

        String diseaseId = "randomDisease";
        Mockito.when(this.diseaseService.findByDiseaseId(diseaseId)).thenThrow(new AssetNotFoundException("Unable to find the diseaseId '" + diseaseId + "'."));
        ResultActions res = this.mockMvc.
                perform(MockMvcRequestBuilders.get("/diseaseservice/api/diseases/" + diseaseId).param("diseaseId", diseaseId));
        res.andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.jsonPath("$.requestId", Matchers.notNullValue()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.hasError", Matchers.equalTo(true)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errorMessage", Matchers.startsWith("Unable to find the diseaseId")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errorCode", Matchers.equalTo(404)));
    }

    @Test
    public void testGetDiseasesByAccession() throws Exception {

        String a1 = "ACC1-"+ uuid;
        Disease d1 = ModelCreationUtils.createDiseaseObject(uuid + 1);
        Disease d2 = ModelCreationUtils.createDiseaseObject(uuid + 2);
        Disease d3 = ModelCreationUtils.createDiseaseObject(uuid + 3);
        List<Disease> diseases = Arrays.asList(d1, d2, d3);

        Mockito.when(this.diseaseService.getDiseasesByProteinAccession(a1)).thenReturn(diseases);
        ResultActions res = this.mockMvc.
                perform(MockMvcRequestBuilders.get("/diseaseservice/api/protein/" + a1 + "/diseases").param("accession", a1));

        res.andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.jsonPath("$.requestId", Matchers.notNullValue()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.hasError", Matchers.equalTo(false)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.warnings", Matchers.nullValue()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.results", Matchers.notNullValue()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.results.length()", Matchers.equalTo(diseases.size())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.results[*].diseaseId", Matchers.notNullValue()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.results[*].diseaseName", Matchers.notNullValue()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.results[*].acronym", Matchers.notNullValue()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.results[*].description", Matchers.notNullValue()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.results[*].proteins").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.results[*].variants").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.results[*].synonyms").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.results[*].drugs").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.results[*].publications").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.results[*].children").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.results[*].isGroup").exists());
    }
}
