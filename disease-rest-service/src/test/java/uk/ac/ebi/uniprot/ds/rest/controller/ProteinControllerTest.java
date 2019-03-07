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
import uk.ac.ebi.uniprot.ds.rest.service.ProteinService;
import uk.ac.ebi.uniprot.ds.rest.utils.ModelCreationUtils;

import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@RunWith(SpringRunner.class)
@WebMvcTest(ProteinController.class)
@ContextConfiguration(classes={RestServiceSpringBootApplication.class})
public class ProteinControllerTest {
    private String uuid = UUID.randomUUID().toString();

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProteinService proteinService;

    @MockBean
    private DiseaseService diseaseService;

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
                .andExpect(jsonPath("$.result.xrefs", nullValue()))
                .andExpect(jsonPath("$.result.variants", nullValue()))
                .andExpect(jsonPath("$.result.interactions", nullValue()))
                .andExpect(jsonPath("$.result.geneCoordinates.length()", equalTo(0)));
    }

    @Test
    public void testGetProteinWithOtherDetails() throws Exception {

        String accession = "FULL_PROTEIN";
        Protein protein = ModelCreationUtils.createProteinObject(uuid);
        protein.setAccession(accession);

        Disease d1 = ModelCreationUtils.createDiseaseObject(uuid + 1);
        Disease d2 = ModelCreationUtils.createDiseaseObject(uuid + 2);
        protein.setDiseases(Arrays.asList(d1, d2));

        Variant v1 = ModelCreationUtils.createVariantObject(uuid + 1);
        Variant v2 = ModelCreationUtils.createVariantObject(uuid + 2);
        Variant v3 = ModelCreationUtils.createVariantObject(uuid + 3);
        protein.setVariants(Arrays.asList(v1, v2,v3));

        // protein xrefs
        ProteinCrossRef p1 = ModelCreationUtils.createProteinXRefObject(uuid + 1);
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
                .andExpect(jsonPath("$.result.xrefs.length()", equalTo(protein.getProteinCrossRefs().size())))
                .andExpect(jsonPath("$.result.interactions.length()", equalTo(protein.getInteractions().size())))
                .andExpect(jsonPath("$.result.variants.length()", equalTo(protein.getVariants().size())))
                .andExpect(jsonPath("$.result.diseases.length()", equalTo(protein.getDiseases().size())))
                .andExpect(jsonPath("$.result.geneCoordinates.length()", equalTo(protein.getGeneCoordinates().size())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.result.publications.length()", Matchers.equalTo(4)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.result.publications[*].type", Matchers.notNullValue()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.result.publications[*].id", Matchers.notNullValue()));
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
}
