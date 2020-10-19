package uk.ac.ebi.uniprot.ds.importer.reader.graph;

import org.hamcrest.Matcher;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsIterableContainingInAnyOrder.containsInAnyOrder;

/**
 * @author sahmad
 * @created 15/10/2020
 */
class AdjacencyListTest {
    /* case 1 test with below data, OBOTerm(OT)
        Parent(s) of OT1 -> OT2 and OT3
        Parent(s) of OT2  -> OT4
        Parent(s) of OT4 -> OT5 and OT6
        Parent(s) of OT5 -> OT2
        in other words
        Child(ren) of OT1 -> None
        Child(ren) of OT2 -> OT1 and OT5
        Child(ren) of OT3 -> OT1
        Child(ren) of OT4 -> OT2
        Child(ren) of OT5 -> OT4
        Child(ren) of OT6 -> OT4
     */
    @Test
    void testBuildAdjacencyList(){
        // when
        OBOTerm ot6 = new OBOTerm("OT6", "OT6", Collections.emptyList());
        OBOTerm ot4 = new OBOTerm("OT4", "OT4", Arrays.asList("OT5", "OT6"));
        OBOTerm ot3 = new OBOTerm("OT3", "OT3", Collections.emptyList());
        OBOTerm ot2 = new OBOTerm("OT2", "OT2", Arrays.asList("OT4"));
        OBOTerm ot5 = new OBOTerm("OT5", "OT5", Arrays.asList("OT2"));
        OBOTerm ot1 = new OBOTerm("OT1", "OT1", Arrays.asList("OT2", "OT3"));
        List<OBOTerm> oboTerms = Arrays.asList(ot1, ot2, ot3, ot4, ot5, ot6);
        AdjacencyList adjacencyList = new AdjacencyList();
        Map<String, Node> map = adjacencyList.buildAdjacencyList(oboTerms);
        Assertions.assertFalse(map.isEmpty());
        Assertions.assertEquals(6, map.size());
        Matcher<Iterable<? extends String>> expectedOtIds = containsInAnyOrder("OT1", "OT2", "OT3",
                "OT4", "OT5", "OT6");
        Set<String> actualOtIds = map.keySet();
        assertThat(actualOtIds, expectedOtIds);
        // then verify each node and its children now
        // OT1
        Node ot11 = map.get("OT1");
        Assertions.assertNotNull(ot11);
        Assertions.assertNotNull(ot11.getTerm());
        Assertions.assertEquals("OT1", ot11.getTerm().getId());
        Assertions.assertEquals("OT1", ot11.getTerm().getName());
        Assertions.assertTrue(ot11.getChildren().isEmpty());
        // OT2
        Node ot22 = map.get("OT2");
        Assertions.assertNotNull(ot22);
        Assertions.assertNotNull(ot22.getTerm());
        Assertions.assertEquals("OT2", ot22.getTerm().getId());
        Assertions.assertEquals("OT2", ot22.getTerm().getName());
        Assertions.assertFalse(ot22.getChildren().isEmpty());
        Assertions.assertEquals(2, ot22.getChildren().size());
        Matcher<Iterable<? extends String>> expectedChildren2 = containsInAnyOrder("OT1", "OT5");
        List<String> actualChildren2 = ot22.getChildren().stream().map(Node::getTerm).map(OBOTerm::getId)
                .collect(Collectors.toList());
        assertThat(actualChildren2, expectedChildren2);

        // OT3
        Node ot33 = map.get("OT3");
        Assertions.assertNotNull(ot33);
        Assertions.assertNotNull(ot33.getTerm());
        Assertions.assertEquals("OT3", ot33.getTerm().getId());
        Assertions.assertEquals("OT3", ot33.getTerm().getName());
        Assertions.assertFalse(ot33.getChildren().isEmpty());
        Assertions.assertEquals(1, ot33.getChildren().size());
        Matcher<Iterable<? extends String>> expectedChildren3 = containsInAnyOrder("OT1");
        List<String> actualChildren3 = ot33.getChildren().stream().map(Node::getTerm).map(OBOTerm::getId)
                .collect(Collectors.toList());
        assertThat(actualChildren3, expectedChildren3);

        // OT4
        Node ot44 = map.get("OT4");
        Assertions.assertNotNull(ot44);
        Assertions.assertNotNull(ot44.getTerm());
        Assertions.assertEquals("OT4", ot44.getTerm().getId());
        Assertions.assertEquals("OT4", ot44.getTerm().getName());
        Assertions.assertFalse(ot44.getChildren().isEmpty());
        Assertions.assertEquals(1, ot44.getChildren().size());
        Matcher<Iterable<? extends String>> expectedChildren4 = containsInAnyOrder("OT2");
        List<String> actualChildren4 = ot44.getChildren().stream().map(Node::getTerm).map(OBOTerm::getId)
                .collect(Collectors.toList());
        assertThat(actualChildren4, expectedChildren4);

        // OT5
        Node ot55 = map.get("OT5");
        Assertions.assertNotNull(ot55);
        Assertions.assertNotNull(ot55.getTerm());
        Assertions.assertEquals("OT5", ot55.getTerm().getId());
        Assertions.assertEquals("OT5", ot55.getTerm().getName());
        Assertions.assertFalse(ot55.getChildren().isEmpty());
        Assertions.assertEquals(1, ot55.getChildren().size());
        Matcher<Iterable<? extends String>> expectedChildren5 = containsInAnyOrder("OT4");
        List<String> actualChildren5 = ot55.getChildren().stream().map(Node::getTerm).map(OBOTerm::getId)
                .collect(Collectors.toList());
        assertThat(actualChildren5, expectedChildren5);

        // OT6
        Node ot66 = map.get("OT6");
        Assertions.assertNotNull(ot66);
        Assertions.assertNotNull(ot66.getTerm());
        Assertions.assertEquals("OT6", ot66.getTerm().getId());
        Assertions.assertEquals("OT6", ot66.getTerm().getName());
        Assertions.assertFalse(ot44.getChildren().isEmpty());
        Assertions.assertEquals(1, ot66.getChildren().size());
        Matcher<Iterable<? extends String>> expectedChildren6 = containsInAnyOrder("OT4");
        List<String> actualChildren6 = ot66.getChildren().stream().map(Node::getTerm).map(OBOTerm::getId)
                .collect(Collectors.toList());
        assertThat(actualChildren6, expectedChildren6);
    }

    // test add Alzheimer Disease 9(MONDO:0012153) as a child of Alzheimer disease(id: MONDO:0004975) manually
    @Test
    void testBuildAdjacencyListForManualMapping(){
        // create two independent mondo terms
        OBOTerm ad = new OBOTerm("MONDO:0004975", "AD", new ArrayList<>());
        OBOTerm ad9 = new OBOTerm("MONDO:0012153", "AD9", new ArrayList<>());
        List<OBOTerm> oboTerms = Arrays.asList(ad9, ad);
        AdjacencyList adjacencyList = new AdjacencyList();
        // process method should make ad9 a child of ad
        Map<String, Node> map = adjacencyList.buildAdjacencyList(oboTerms);
        // verify
        Assertions.assertFalse(map.isEmpty());
        Assertions.assertEquals(2, map.size());
        assertThat(map.keySet(), containsInAnyOrder("MONDO:0004975", "MONDO:0012153"));

        Node ad1 = map.get("MONDO:0004975");
        Assertions.assertNotNull(ad1.getTerm());
        Assertions.assertEquals("AD", ad1.getTerm().getName());
        Assertions.assertEquals("MONDO:0004975", ad1.getTerm().getId());
        List<Node> children = ad1.getChildren();
        Assertions.assertEquals(1, children.size());
        Assertions.assertNotNull(children.get(0).getTerm());
        Assertions.assertEquals("MONDO:0012153", children.get(0).getTerm().getId());
        Assertions.assertEquals("AD9", children.get(0).getTerm().getName());
    }
}
