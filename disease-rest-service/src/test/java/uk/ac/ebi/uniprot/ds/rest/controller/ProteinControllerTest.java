/*
 * Created by sahmad on 07/02/19 15:02
 * UniProt Consortium.
 * Copyright (c) 2002-2019.
 *
 */

package uk.ac.ebi.uniprot.ds.rest.controller;

import org.hamcrest.Matchers;
import org.hamcrest.collection.IsArrayContainingInOrder;
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
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import uk.ac.ebi.uniprot.ds.common.model.*;
import uk.ac.ebi.uniprot.ds.rest.RestServiceSpringBootApplication;
import uk.ac.ebi.uniprot.ds.rest.exception.AssetNotFoundException;
import uk.ac.ebi.uniprot.ds.rest.service.DiseaseService;
import uk.ac.ebi.uniprot.ds.rest.service.DrugService;
import uk.ac.ebi.uniprot.ds.rest.service.ProteinService;
import uk.ac.ebi.uniprot.ds.rest.service.VariantService;
import uk.ac.ebi.uniprot.ds.rest.utils.ModelCreationUtils;

import java.util.*;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@RunWith(SpringRunner.class)
@WebMvcTest(ProteinController.class)
@ContextConfiguration(classes = {RestServiceSpringBootApplication.class})
public class ProteinControllerTest {
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

    @Test
    public void testGetProtein() throws Exception {
        String accession = "ACCESSION_ID";
        Protein protein = ModelCreationUtils.createProteinObject(uuid);
        protein.setAccession(accession);

        Mockito.when(this.proteinService.getProteinByAccession(accession)).thenReturn(Optional.of(protein));

        ResultActions res = this.mockMvc.perform
                (
                        MockMvcRequestBuilders.
                                get("/v1/ds/proteins/" + accession).
                                param("accession", accession)
                );

        res.andDo(MockMvcResultHandlers.print())
                .andExpect(jsonPath("$.requestId", notNullValue()))
                .andExpect(jsonPath("$.hasError", equalTo(false)))
                .andExpect(jsonPath("$.warnings", nullValue()))
                .andExpect(jsonPath("$.result", notNullValue()))
                .andExpect(jsonPath("$.result.proteinId", equalTo(protein.getProteinId())))
                .andExpect(jsonPath("$.result.proteinName", equalTo(protein.getName())))
                .andExpect(jsonPath("$.result.accession", equalTo(protein.getAccession())))
                .andExpect(jsonPath("$.result.gene", equalTo(protein.getGene())))
                .andExpect(jsonPath("$.result.description", equalTo(protein.getDesc())))
                .andExpect(jsonPath("$.result.pathways", nullValue()))
                .andExpect(jsonPath("$.result.variants", nullValue()))
                .andExpect(jsonPath("$.result.interactions", nullValue()))
                .andExpect(jsonPath("$.result.drugs", nullValue()))
                .andExpect(jsonPath("$.result.geneCoordinates.length()", equalTo(0)));
    }

    @Test
    public void testGetProteinWithOtherDetails() throws Exception {

        String accession = "FULL_PROTEIN";
        Protein protein = ModelCreationUtils.createProteinObject(uuid);
        protein.setAccession(accession);

        Disease d1 = ModelCreationUtils.createDiseaseObject(uuid + 1);
        Disease d2 = ModelCreationUtils.createDiseaseObject(uuid + 2);

        DiseaseProtein dp1 = new DiseaseProtein(d1, protein, true);
        DiseaseProtein dp2 = new DiseaseProtein(d2, protein, true);

        protein.setDiseaseProteins(new HashSet<>(Arrays.asList(dp1, dp2)));

        Variant v1 = ModelCreationUtils.createVariantObject(uuid + 1);
        Variant v2 = ModelCreationUtils.createVariantObject(uuid + 2);
        Variant v3 = ModelCreationUtils.createVariantObject(uuid + 3);
        protein.setVariants(Arrays.asList(v1, v2, v3));

        // create few drugs
        Drug drug1 = ModelCreationUtils.createDrugObject(uuid + 1);
        Drug drug2 = ModelCreationUtils.createDrugObject(uuid + 2);
        Drug drug3 = ModelCreationUtils.createDrugObject(uuid + 3);

        // protein xrefs
        ProteinCrossRef p1 = ModelCreationUtils.createProteinXRefObject(uuid + 1);
        p1.setDrugs(Arrays.asList(drug1, drug2, drug3));
        ProteinCrossRef p2 = ModelCreationUtils.createProteinXRefObject(uuid + 2);
        ProteinCrossRef p3 = ModelCreationUtils.createProteinXRefObject(uuid + 3);
        protein.setProteinCrossRefs(Arrays.asList(p1, p2, p3));

        // interactions
        Interaction in1 = ModelCreationUtils.createInteractionObject(uuid + 1);
        Interaction in2 = ModelCreationUtils.createInteractionObject(uuid + 2);
        Interaction in3 = ModelCreationUtils.createInteractionObject(uuid + 3);
        Interaction in4 = ModelCreationUtils.createInteractionObject(uuid + 4);
        protein.setInteractions(Arrays.asList(in1, in2, in3, in4));

        // create GeneCoordinates
        GeneCoordinate gc1 = ModelCreationUtils.createGeneCoordinateObject(uuid + 1);
        GeneCoordinate gc2 = ModelCreationUtils.createGeneCoordinateObject(uuid + 2);
        GeneCoordinate gc3 = ModelCreationUtils.createGeneCoordinateObject(uuid + 3);
        GeneCoordinate gc4 = ModelCreationUtils.createGeneCoordinateObject(uuid + 4);
        GeneCoordinate gc5 = ModelCreationUtils.createGeneCoordinateObject(uuid + 5);
        protein.setGeneCoordinates(Arrays.asList(gc1, gc2, gc3, gc4, gc5));

        // create few publications
        Publication pb1 = ModelCreationUtils.createPublicationObject(this.uuid + 1);
        Publication pb2 = ModelCreationUtils.createPublicationObject(this.uuid + 2);
        Publication pb3 = ModelCreationUtils.createPublicationObject(this.uuid + 3);
        Publication pb4 = ModelCreationUtils.createPublicationObject(this.uuid + 4);
        protein.setPublications(Arrays.asList(pb1, pb2, pb3, pb4));

        Mockito.when(this.proteinService.getProteinByAccession(accession)).thenReturn(Optional.of(protein));

        ResultActions res = this.mockMvc.
                perform(MockMvcRequestBuilders.get("/v1/ds/proteins/" + accession).param("accession", accession));

        res.andDo(MockMvcResultHandlers.print())
                .andExpect(jsonPath("$.requestId", notNullValue()))
                .andExpect(jsonPath("$.hasError", equalTo(false)))
                .andExpect(jsonPath("$.warnings", nullValue()))
                .andExpect(jsonPath("$.result", notNullValue()))
                .andExpect(jsonPath("$.result.proteinId", equalTo(protein.getProteinId())))
                .andExpect(jsonPath("$.result.proteinName", equalTo(protein.getName())))
                .andExpect(jsonPath("$.result.accession", equalTo(protein.getAccession())))
                .andExpect(jsonPath("$.result.gene", equalTo(protein.getGene())))
                .andExpect(jsonPath("$.result.description", equalTo(protein.getDesc())))
          //      .andExpect(jsonPath("$.result.pathways.length()", equalTo(protein.getProteinCrossRefs().size())))
                .andExpect(jsonPath("$.result.interactions.length()", equalTo(protein.getInteractions().size())))
                .andExpect(jsonPath("$.result.variants.length()", equalTo(protein.getVariants().size())))
                .andExpect(jsonPath("$.result.diseases.length()", equalTo(protein.getDiseaseProteins().size())))
                .andExpect(jsonPath("$.result.geneCoordinates.length()", equalTo(protein.getGeneCoordinates().size())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.result.publications.length()", Matchers.equalTo(4)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.result.publications[*].type", Matchers.notNullValue()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.result.publications[*].id", Matchers.notNullValue()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.result.drugs.length()", Matchers.equalTo(3)));
    }

    @Test
    public void testNonExistentProtein() throws Exception {

        String accession = "randomProtein";
        Mockito.when(this.proteinService.getProteinByAccession(accession)).thenThrow(new AssetNotFoundException("Unable to find the accession '" + accession + "'."));

        ResultActions res = this.mockMvc.
                perform(MockMvcRequestBuilders.get("/v1/ds/proteins/" + accession).param("accession", accession));
        res.andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.jsonPath("$.requestId", Matchers.notNullValue()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.hasError", Matchers.equalTo(true)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errorMessage", Matchers.startsWith("Unable to find the accession")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errorCode", Matchers.equalTo(404)));
    }

    @Test
    public void testGetProteinInteractions() throws Exception {

        String a1 = "ACC1-" + uuid;
        Interaction int1 = ModelCreationUtils.createInteractionObject(uuid + 1);
        Interaction int2 = ModelCreationUtils.createInteractionObject(uuid + 2);
        Interaction int3 = ModelCreationUtils.createInteractionObject(uuid + 3);
        List<Interaction> ints = Arrays.asList(int1, int2, int3);

        Mockito.when(this.proteinService.getProteinInteractions(a1)).thenReturn(ints);

        ResultActions res = this.mockMvc.
                perform(MockMvcRequestBuilders.get("/v1/ds/protein/" + a1 + "/interactions").param("accession", a1));

        res.andDo(MockMvcResultHandlers.print())
                .andExpect(jsonPath("$.requestId", notNullValue()))
                .andExpect(jsonPath("$.hasError", equalTo(false)))
                .andExpect(jsonPath("$.warnings", nullValue()))
                .andExpect(jsonPath("$.results", notNullValue()))
                .andExpect(jsonPath("$.results.length()", equalTo(ints.size())))
                .andExpect(jsonPath("$.results[*].type", notNullValue()))
                .andExpect(jsonPath("$.results[*].accession", notNullValue()))
                .andExpect(jsonPath("$.results[*].gene", notNullValue()))
                .andExpect(jsonPath("$.results[*].experimentCount", notNullValue()))
                .andExpect(jsonPath("$.results[*].firstInteractor", notNullValue()))
                .andExpect(jsonPath("$.results[*].secondInteractor", notNullValue()));
    }

    @Test
    public void testGetProteinsByDiseaseId() throws Exception {

        String diseaseId = "diseaseId";
        Protein p1 = ModelCreationUtils.createProteinObject(uuid+1);

        Disease d1 = ModelCreationUtils.createDiseaseObject(uuid + 1);
        Disease d2 = ModelCreationUtils.createDiseaseObject(uuid + 2);
        DiseaseProtein dp1 = new DiseaseProtein(d1, p1, true);
        DiseaseProtein dp2 = new DiseaseProtein(d2, p1, true);
        p1.setDiseaseProteins(new HashSet<>(Arrays.asList(dp1, dp2)));
        p1.setIsExternallyMapped(true);

        Variant v1 = ModelCreationUtils.createVariantObject(uuid + 1);
        Variant v2 = ModelCreationUtils.createVariantObject(uuid + 2);
        Variant v3 = ModelCreationUtils.createVariantObject(uuid + 3);
        p1.setVariants(Arrays.asList(v1, v2, v3));

        // protein xrefs
        ProteinCrossRef pcr1 = ModelCreationUtils.createProteinXRefObject(uuid + 1);
        ProteinCrossRef pcr2 = ModelCreationUtils.createProteinXRefObject(uuid + 2);
        ProteinCrossRef pcr3 = ModelCreationUtils.createProteinXRefObject(uuid + 3);
        p1.setProteinCrossRefs(Arrays.asList(pcr1, pcr2, pcr3));

        // interactions
        Interaction in1 = ModelCreationUtils.createInteractionObject(uuid + 1);
        Interaction in2 = ModelCreationUtils.createInteractionObject(uuid + 2);
        Interaction in3 = ModelCreationUtils.createInteractionObject(uuid + 3);
        Interaction in4 = ModelCreationUtils.createInteractionObject(uuid + 4);
        p1.setInteractions(Arrays.asList(in1, in2, in3, in4));

        // create GeneCoordinates
        GeneCoordinate gc1 = ModelCreationUtils.createGeneCoordinateObject(uuid + 1);
        GeneCoordinate gc2 = ModelCreationUtils.createGeneCoordinateObject(uuid + 2);
        GeneCoordinate gc3 = ModelCreationUtils.createGeneCoordinateObject(uuid + 3);
        GeneCoordinate gc4 = ModelCreationUtils.createGeneCoordinateObject(uuid + 4);
        GeneCoordinate gc5 = ModelCreationUtils.createGeneCoordinateObject(uuid + 5);
        p1.setGeneCoordinates(Arrays.asList(gc1, gc2, gc3, gc4, gc5));

        // create few publications
        Publication pb1 = ModelCreationUtils.createPublicationObject(this.uuid + 1);
        Publication pb2 = ModelCreationUtils.createPublicationObject(this.uuid + 2);
        Publication pb3 = ModelCreationUtils.createPublicationObject(this.uuid + 3);
        Publication pb4 = ModelCreationUtils.createPublicationObject(this.uuid + 4);
        p1.setPublications(Arrays.asList(pb1, pb2, pb3, pb4));

        Mockito.when(this.proteinService.getProteinsByDiseaseId(diseaseId)).thenReturn(Arrays.asList(p1));

        ResultActions res = this.mockMvc
                .perform(MockMvcRequestBuilders
                        .get("/v1/ds/disease/" + diseaseId + "/proteins")
                        .param("diseaseId", diseaseId));

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
                .andExpect(jsonPath("$.results[0].isExternallyMapped", equalTo(true)))
         //       .andExpect(jsonPath("$.results[0].xrefs.length()", equalTo(p1.getProteinCrossRefs().size())))
                .andExpect(jsonPath("$.results[0].interactions.length()", equalTo(p1.getInteractions().size())))
                .andExpect(jsonPath("$.results[0].variants.length()", equalTo(p1.getVariants().size())))
                .andExpect(jsonPath("$.results[0].diseases.length()", equalTo(p1.getDiseaseProteins().size())))
                .andExpect(jsonPath("$.results[0].diseases[*].note", Matchers.notNullValue()))
                .andExpect(jsonPath("$.results[0].isExternallyMapped", equalTo(true)))
                .andExpect(jsonPath("$.results[0].geneCoordinates.length()", equalTo(p1.getGeneCoordinates().size())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.results[0].publications.length()", Matchers.equalTo(4)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.results[0].publications[*].type", Matchers.notNullValue()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.results[0].publications[*].id", Matchers.notNullValue()));
    }
}
