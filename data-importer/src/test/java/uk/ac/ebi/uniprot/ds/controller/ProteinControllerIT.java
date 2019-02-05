/*
 * Created by sahmad on 05/02/19 14:29
 * UniProt Consortium.
 * Copyright (c) 2002-2019.
 *
 */

package uk.ac.ebi.uniprot.ds.controller;

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
import uk.ac.ebi.uniprot.ds.EclipselinkSpringDataApplication;
import uk.ac.ebi.uniprot.ds.controller.mapper.EntityToDTOMapper;
import uk.ac.ebi.uniprot.ds.exception.AssetNotFoundException;
import uk.ac.ebi.uniprot.ds.model.*;
import uk.ac.ebi.uniprot.ds.service.ProteinService;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@RunWith(SpringRunner.class)
@WebMvcTest(ProteinController.class)
@ContextConfiguration(classes={EclipselinkSpringDataApplication.class, EntityToDTOMapper.class})
public class ProteinControllerIT {
    private String uuid = UUID.randomUUID().toString();

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProteinService proteinService;

    @Test
    public void testGetProtein() throws Exception {
        String accession = "ACCESSION_ID";
        Protein protein = ProteinTest.createProteinObject(uuid);
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
                .andExpect(jsonPath("$.result.interactions", nullValue()));
    }

    @Test
    public void testGetProteinWithOtherDetails() throws Exception {

        String accession = "FULL_PROTEIN";
        Protein protein = ProteinTest.createProteinObject(uuid);
        protein.setAccession(accession);

        Disease d1 = DiseaseTest.createDiseaseObject(uuid + 1);
        Disease d2 = DiseaseTest.createDiseaseObject(uuid + 2);
        protein.setDiseases(new HashSet<>(Arrays.asList(d1, d2)));

        Variant v1 = VariantTest.createVariantObject(uuid + 1);
        Variant v2 = VariantTest.createVariantObject(uuid + 2);
        Variant v3 = VariantTest.createVariantObject(uuid + 3);
        protein.setVariants(Arrays.asList(v1, v2,v3));

        // pathways
        Pathway p1 = PathwayTest.createPathwayObject(uuid + 1);
        Pathway p2 = PathwayTest.createPathwayObject(uuid + 2);
        Pathway p3 = PathwayTest.createPathwayObject(uuid + 3);
        protein.setPathways(Arrays.asList(p1, p2, p3));

        // interactions
        Interaction in1 = InteractionTest.createInteractionObject(uuid + 1);
        Interaction in2 = InteractionTest.createInteractionObject(uuid + 2);
        Interaction in3 = InteractionTest.createInteractionObject(uuid + 3);
        Interaction in4 = InteractionTest.createInteractionObject(uuid + 4);
        protein.setInteractions(Arrays.asList(in1, in2, in3, in4));

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
                .andExpect(jsonPath("$.result.pathways.length()", equalTo(protein.getPathways().size())))
                .andExpect(jsonPath("$.result.interactions.length()", equalTo(protein.getInteractions().size())))
                .andExpect(jsonPath("$.result.variants.length()", equalTo(protein.getVariants().size())))
                .andExpect(jsonPath("$.result.diseases.length()", equalTo(protein.getDiseases().size())));
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
