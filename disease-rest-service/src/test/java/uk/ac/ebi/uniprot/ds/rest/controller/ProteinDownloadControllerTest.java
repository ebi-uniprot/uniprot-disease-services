/*
 * Created by sahmad on 07/02/19 15:02
 * UniProt Consortium.
 * Copyright (c) 2002-2019.
 *
 */

package uk.ac.ebi.uniprot.ds.rest.controller;

import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import uk.ac.ebi.uniprot.ds.common.model.*;
import uk.ac.ebi.uniprot.ds.rest.DataSourceTestConfig;
import uk.ac.ebi.uniprot.ds.rest.service.DiseaseService;
import uk.ac.ebi.uniprot.ds.rest.service.DrugService;
import uk.ac.ebi.uniprot.ds.rest.service.ProteinService;
import uk.ac.ebi.uniprot.ds.rest.service.VariantService;
import uk.ac.ebi.uniprot.ds.rest.utils.ModelCreationUtils;

import java.util.*;

@RunWith(SpringRunner.class)
@WebMvcTest(DiseaseController.class)
@Import({DataSourceTestConfig.class})
//@ComponentScan(basePackages = {"uk.ac.ebi.uniprot.ds.rest"})
public class ProteinDownloadControllerTest {
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

    @Test
    public void testDownloadProteinsByAccessions() throws Exception {
        String accessionsStr = "acc1,acc2,acc3";
        List<String> accessions = Arrays.asList(accessionsStr.split(","));
        Protein p1 = getProtein(accessions.get(0));
        Protein p2 = getProtein(accessions.get(1));
        Protein p3 = getProtein(accessions.get(2));
        List<Protein> proteins = new ArrayList<>();
        proteins.add(p1);proteins.add(p2);proteins.add(p3);



        Mockito.when(this.proteinService.getAllProteinsByAccessions(accessions)).thenReturn(proteins);

        ResultActions res = this.mockMvc.perform
                (
                        MockMvcRequestBuilders.
                                get("/v1/ds/proteins/" + accessionsStr + "/download").
                                param("accessions", accessionsStr)
                );

        MvcResult result = res.andDo(MockMvcResultHandlers.print()).andReturn();
        String responseStr = result.getResponse().getContentAsString();
        Assertions.assertTrue(responseStr.contains("Protein Accession\tProtein Name\tFunction\tGene Info\tInteractions\tPathways\tVariants\tDiseases\tDrugs"));
        Assertions.assertTrue(responseStr.contains(accessions.get(0)));
        Assertions.assertTrue(responseStr.contains(accessions.get(1)));
        Assertions.assertTrue(responseStr.contains(accessions.get(2)));
    }

    private Protein getProtein(String accession) {
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

        return protein;
    }
}
