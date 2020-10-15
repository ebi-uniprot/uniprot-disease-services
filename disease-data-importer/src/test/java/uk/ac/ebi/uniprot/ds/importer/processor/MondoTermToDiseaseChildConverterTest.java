package uk.ac.ebi.uniprot.ds.importer.processor;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import uk.ac.ebi.uniprot.ds.common.dao.DiseaseDAO;
import uk.ac.ebi.uniprot.ds.common.model.Disease;
import uk.ac.ebi.uniprot.ds.importer.model.DiseaseRelationDTO;
import uk.ac.ebi.uniprot.ds.importer.reader.graph.OBOTerm;
import uk.ac.ebi.uniprot.ds.importer.writer.DiseaseWriterTest;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsIterableContainingInAnyOrder.containsInAnyOrder;

/**
 * @author sahmad
 * @created 15/10/2020
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class MondoTermToDiseaseChildConverterTest {
    @Autowired
    private DiseaseDAO diseaseDAO;
    @AfterEach
    void cleanUp(){
        this.diseaseDAO.deleteAll();
    }
    /* Acronyms -> OT = OBOTerm, HD = Hum Disease
        OT1(HD1) has children HD2(OT2) and HD3(OT3)
        OT2(HD2) has children HD4(OT4)
        OT4(HD4) has children HD5(OT5), HD6(OT6) and HD7(OT7)
        HD5(OT5) has no children
        HD6(OT6) has no children
        HD7(OT7) has no children
        in other words
        Parent of OT1 --> NA, Parent of OT2 --> OT1, Parent of OT3 --> OT1,
        Parent of OT4 is OT2,Parent of OT5 is OT4, Parent of OT6 is OT4, Parent of OT7 is OT4
     */
    @Test
    void testCreateRelations() {
        // given
        List<Disease> diseases = createHumDiseases();
        Map<String, Disease> nameDiseaseMap = diseases.stream().collect(Collectors.toMap(dis -> dis.getName().toLowerCase(), Function.identity()));
        List<OBOTerm> oboTerms = createOBOTerms();
        MondoTermToDiseaseChildConverter converter = new MondoTermToDiseaseChildConverter(nameDiseaseMap, oboTerms);
        // get relations
        Map<String, List<DiseaseRelationDTO>> otRelations = new HashMap<>();
        for(OBOTerm oboTerm : oboTerms) {
            List<DiseaseRelationDTO> relations = converter.process(oboTerm);
            otRelations.put(oboTerm.getId(), relations);
        }
        // verify the children
        Assertions.assertEquals(7, otRelations.size());
        Assertions.assertEquals(2, otRelations.get("OT1").size());
        List<DiseaseRelationDTO> ot1r = otRelations.get("OT1");
        Long hd1Id = nameDiseaseMap.get("hd1").getId();
        Long hd2Id = nameDiseaseMap.get("hd2").getId();
        Long hd3Id = nameDiseaseMap.get("hd3").getId();
        // verify parent id
        assertThat(ot1r.stream().map(DiseaseRelationDTO::getParentId).collect(Collectors.toList()), containsInAnyOrder(hd1Id, hd1Id));
        // verify child ids
        assertThat(ot1r.stream().map(DiseaseRelationDTO::getChildId).collect(Collectors.toList()), containsInAnyOrder(hd2Id, hd3Id));
        Assertions.assertEquals(1, otRelations.get("OT2").size());
        Assertions.assertEquals(3, otRelations.get("OT4").size());
        Assertions.assertTrue(otRelations.get("OT3").isEmpty());
        Assertions.assertTrue(otRelations.get("OT5").isEmpty());
        Assertions.assertTrue(otRelations.get("OT6").isEmpty());
        Assertions.assertTrue(otRelations.get("OT7").isEmpty());
    }


    /*
     Test with cycle, it should be ignored. Loop => [OT1 --> (OT2, OT3) --> OT2 --> (OT4, OT5) --> OT5 --> OT6 --> OT2]
     OT1(HD1) has children OT2(HD2) and OT3(HD3)
     OT2(HD2) has children OT4(HD4) and OT5(HD5)
     OT5(HD5) has children OT6(HD6)
     OT6(HD6) has children OT2(HD2) (causes the loop)
     */
    @Test
    void testCreateRelationsWithLoop() {
        // given
        List<Disease> diseases = createHumDiseases();
        Map<String, Disease> nameDiseaseMap = diseases.stream().collect(Collectors.toMap(dis -> dis.getName().toLowerCase(), Function.identity()));
        List<OBOTerm> oboTerms = createOBOTermsWithLoop();
        MondoTermToDiseaseChildConverter converter = new MondoTermToDiseaseChildConverter(nameDiseaseMap, oboTerms);
        // get relations, it should be empty because of loop
        for(OBOTerm oboTerm : oboTerms) {
            List<DiseaseRelationDTO> relations = converter.process(oboTerm);
            Assertions.assertTrue(relations.isEmpty());
        }
    }

    private List<Disease> createHumDiseases() {
        List<Disease> diseases = new ArrayList<>();
        Disease hd1 = DiseaseWriterTest.createDiseaseByDiseaseName("HD1");
        diseases.add(hd1);
        Disease hd2 = DiseaseWriterTest.createDiseaseByDiseaseName("HD2");
        diseases.add(hd2);
        Disease hd3 = DiseaseWriterTest.createDiseaseByDiseaseName("HD3");
        diseases.add(hd3);
        Disease hd4 = DiseaseWriterTest.createDiseaseByDiseaseName("HD4");
        diseases.add(hd4);
        Disease hd5 = DiseaseWriterTest.createDiseaseByDiseaseName("HD5");
        diseases.add(hd5);
        Disease hd6 = DiseaseWriterTest.createDiseaseByDiseaseName("HD6");
        diseases.add(hd6);
        Disease hd7 = DiseaseWriterTest.createDiseaseByDiseaseName("HD7");
        diseases.add(hd7);
        this.diseaseDAO.saveAll(diseases);
        return diseases;
    }

    private List<OBOTerm> createOBOTerms() {
        List<OBOTerm> oboTerms = new ArrayList<>();
        OBOTerm ot1 = new OBOTerm("OT1", "HD1", Collections.emptyList());
        oboTerms.add(ot1);
        OBOTerm ot2 = new OBOTerm("OT2", "HD2", Arrays.asList("OT1"));
        oboTerms.add(ot2);
        OBOTerm ot3 = new OBOTerm("OT3", "HD3", Arrays.asList("OT1"));
        oboTerms.add(ot3);
        OBOTerm ot4 = new OBOTerm("OT4", "HD4", Arrays.asList("OT2"));
        oboTerms.add(ot4);
        OBOTerm ot5 = new OBOTerm("OT5", "HD5", Arrays.asList("OT4"));
        oboTerms.add(ot5);
        OBOTerm ot6 = new OBOTerm("OT6", "HD6", Arrays.asList("OT4"));
        oboTerms.add(ot6);
        OBOTerm ot7 = new OBOTerm("OT7", "HD7", Arrays.asList("OT4"));
        oboTerms.add(ot7);
        return oboTerms;
    }

    private List<OBOTerm> createOBOTermsWithLoop() {
        List<OBOTerm> oboTerms = new ArrayList<>();
        OBOTerm ot1 = new OBOTerm("OT1", "HD1", Collections.emptyList());
        oboTerms.add(ot1);
        OBOTerm ot2 = new OBOTerm("OT2", "HD2", Arrays.asList("OT1", "OT6"));
        oboTerms.add(ot2);
        OBOTerm ot3 = new OBOTerm("OT3", "HD3", Arrays.asList("OT1"));
        oboTerms.add(ot3);
        OBOTerm ot4 = new OBOTerm("OT4", "HD4", Arrays.asList("OT2"));
        oboTerms.add(ot4);
        OBOTerm ot5 = new OBOTerm("OT5", "HD5", Arrays.asList("OT2"));
        oboTerms.add(ot5);
        OBOTerm ot6 = new OBOTerm("OT6", "HD6", Arrays.asList("OT5"));
        oboTerms.add(ot6);
        return oboTerms;
    }

}
