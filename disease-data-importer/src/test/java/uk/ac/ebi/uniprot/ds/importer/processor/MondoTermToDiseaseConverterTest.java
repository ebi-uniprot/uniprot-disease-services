package uk.ac.ebi.uniprot.ds.importer.processor;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import uk.ac.ebi.uniprot.ds.common.common.SourceType;
import uk.ac.ebi.uniprot.ds.common.dao.CrossRefDAO;
import uk.ac.ebi.uniprot.ds.common.dao.DiseaseDAO;
import uk.ac.ebi.uniprot.ds.common.dao.SynonymDAO;
import uk.ac.ebi.uniprot.ds.common.model.CrossRef;
import uk.ac.ebi.uniprot.ds.common.model.Disease;
import uk.ac.ebi.uniprot.ds.common.model.Synonym;
import uk.ac.ebi.uniprot.ds.importer.reader.graph.OBOTerm;
import uk.ac.ebi.uniprot.ds.importer.writer.DiseaseWriterTest;

/**
 * @author sahmad
 * @created 14/10/2020
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class MondoTermToDiseaseConverterTest {
    @Mock
    private StepExecution stepExecution;
    @Autowired
    private DiseaseDAO diseaseDAO;
    @Autowired
    private SynonymDAO synonymDAO;
    @Autowired
    private CrossRefDAO crossRefDAO;
    private Disease humDisease;
    private Disease humDisease2;

    MondoTermToDiseaseConverter converter;

    @AfterEach
    void cleanUp() {
        List<Synonym> synonyms = this.synonymDAO.findAllByDisease(humDisease);
        this.synonymDAO.deleteAll(synonyms);
        List<CrossRef> xrefs = this.crossRefDAO.findAllByDisease(humDisease);
        this.crossRefDAO.deleteAll(xrefs);
        this.diseaseDAO.delete(humDisease);
        if (Objects.nonNull(humDisease2)) {
            this.diseaseDAO.delete(humDisease2);
        }
    }

    // case 1 - Mondo Term name matches with hum disease name
    @Test
    void testConvertWhenNameMatchesHumDisease() {
        // create a hum disease with given name
        String uuid = UUID.randomUUID().toString();
        String name = "DISEASE-NAME-MATCH-" + uuid;
        humDisease = DiseaseWriterTest.createDiseaseByDiseaseName(name);
        this.diseaseDAO.save(humDisease);
        Assertions.assertEquals(humDisease, this.diseaseDAO.findDiseaseByNameIgnoreCase(name).orElse(null));
        // load cache
        initialize();
        // create Mondo term with the same name
        OBOTerm.OBOTermBuilder termBuilder = OBOTerm.builder();
        termBuilder.name(name);
        termBuilder.id("MONDO:1234");
        termBuilder.xrefs(Collections.emptyList());
        // then
        Disease convertedDisease = converter.process(termBuilder.build());
        Assertions.assertEquals(humDisease, convertedDisease);
        Assertions.assertEquals(SourceType.UniProt_HUM.name(), convertedDisease.getSource());
        Assertions.assertEquals(humDisease.getId(), convertedDisease.getId());
        Assertions.assertEquals(humDisease.getAcronym(), convertedDisease.getAcronym());
        Assertions.assertEquals(humDisease.getName(), convertedDisease.getName());
        Assertions.assertEquals(humDisease.getDesc(), convertedDisease.getDesc());
        Assertions.assertEquals(humDisease.getNote(), convertedDisease.getNote());
        Assertions.assertEquals(humDisease.getCrossRefs(), convertedDisease.getCrossRefs());
        Assertions.assertEquals(humDisease.getSynonyms(), convertedDisease.getSynonyms());
        Assertions.assertEquals(humDisease.getKeywords(), convertedDisease.getKeywords());
    }

    // case 2 - Mondo Term name matches with OMIM id, add a synonym with source Mondo
    @Test
    void testConvertWhenOMIMMatches() {
        // given
        String uuid = UUID.randomUUID().toString();
        String name = "DISEASE-OMIM-MATCH-" + uuid;
        humDisease = DiseaseWriterTest.createDiseaseByDiseaseName(name);
        CrossRef xref1 = new CrossRef("MIM", "616033", SourceType.UniProt_HUM.name(), humDisease);
        CrossRef xref2 = new CrossRef("SOMETYPE", "SOME_VAL", SourceType.UniProt_HUM.name(), humDisease);
        humDisease.setCrossRefs(Arrays.asList(xref1, xref2));
        this.diseaseDAO.save(humDisease);
        Assertions.assertEquals(humDisease, this.diseaseDAO.findDiseaseByNameIgnoreCase(name).orElse(null));
        List<CrossRef> xrefs = this.crossRefDAO.findAllByDisease(humDisease);
        Assertions.assertEquals(2, xrefs.size());
        Assertions.assertTrue(humDisease.getSynonyms().isEmpty());
        // load cache
        initialize();

        // create Mondo term with same OMIM id as hum disease but different name. It should create one synonym
        OBOTerm.OBOTermBuilder termBuilder = OBOTerm.builder();
        termBuilder.name(name + "-extra");
        termBuilder.id("MONDO:12345");
        termBuilder.xrefs(Arrays.asList("OMIM:616033", "EFO:1001266"));
        // then
        Disease convertedDisease = converter.process(termBuilder.build());
        Assertions.assertEquals(humDisease, convertedDisease);
        Assertions.assertEquals(SourceType.UniProt_HUM.name(), convertedDisease.getSource());
        Assertions.assertEquals(1, convertedDisease.getSynonyms().size());
        Assertions.assertEquals(name + "-extra", convertedDisease.getSynonyms().get(0).getName());
        Assertions.assertEquals(2, convertedDisease.getCrossRefs().size());
    }

    // case 3 - Mondo Term name doesn't match with name, synonym or OMIM then create one disease entry with Source Mondo
    @Test
    void testMondoTermDoesNotMatchWithHumDisease() {
        // load cache
        initialize();
        String name = "NO-MATCH-" + UUID.randomUUID().toString();
        // create Mondo term no hum disease match
        OBOTerm.OBOTermBuilder termBuilder = OBOTerm.builder();
        termBuilder.name(name);
        String mondoId = "MONDO:123456";
        String diseaseId = "DI-M123456";
        termBuilder.id(mondoId);
        termBuilder.definition("Sample description");
        termBuilder.xrefs(Arrays.asList("OMIM:616033", "UMLS:C4014997"));
        // then
        Disease convertedDisease = converter.process(termBuilder.build());
        Assertions.assertNotNull(convertedDisease);
        Assertions.assertEquals(diseaseId, convertedDisease.getDiseaseId());
        Assertions.assertEquals(name, convertedDisease.getName());
        Assertions.assertEquals("Sample description", convertedDisease.getDesc());
        Assertions.assertEquals(SourceType.MONDO.name(), convertedDisease.getSource());
        Assertions.assertNull(convertedDisease.getCrossRefs());
        humDisease = convertedDisease;
    }

    // case 4 - Mondo Term name matches with OMIM id and the mondo name exists as a  synonym then do nothing
    @Test
    void testConvertWhenOMIMAndNameMatches() {
        // given
        String uuid = UUID.randomUUID().toString();
        String name = "DISEASE-OMIM-MATCH-" + uuid;
        humDisease = DiseaseWriterTest.createDiseaseByDiseaseName(name);
        String otherName = "MATCHED_SYNONYM";
        Synonym synonym = new Synonym(otherName, humDisease, SourceType.UniProt_HUM.name());
        CrossRef xref1 = new CrossRef("MIM", "616033", SourceType.UniProt_HUM.name(), humDisease);
        CrossRef xref2 = new CrossRef("SOMETYPE", "SOME_VAL", SourceType.UniProt_HUM.name(), humDisease);
        humDisease.setCrossRefs(Arrays.asList(xref1, xref2));
        humDisease.setSynonyms(Collections.singletonList(synonym));
        this.diseaseDAO.save(humDisease);
        Assertions.assertEquals(humDisease, this.diseaseDAO.findDiseaseByNameIgnoreCase(name).orElse(null));
        List<CrossRef> xrefs = this.crossRefDAO.findAllByDisease(humDisease);
        Assertions.assertEquals(2, xrefs.size());
        Assertions.assertEquals(1, humDisease.getSynonyms().size());
        Assertions.assertEquals(otherName, humDisease.getSynonyms().get(0).getName());
        Assertions.assertEquals(SourceType.UniProt_HUM.name(), humDisease.getSynonyms().get(0).getSource());
        // load cache
        initialize();

        // create Mondo term with same OMIM id as hum disease but different name. It should create one synonym
        OBOTerm.OBOTermBuilder termBuilder = OBOTerm.builder();
        termBuilder.name(otherName);
        termBuilder.id("MONDO:12345");
        termBuilder.xrefs(Arrays.asList("OMIM:616033", "UMLS:C4014997"));
        // then
        Disease convertedDisease = converter.process(termBuilder.build());
        Assertions.assertEquals(humDisease, convertedDisease);
        Assertions.assertEquals(SourceType.UniProt_HUM.name(), convertedDisease.getSource());
        Assertions.assertEquals(1, convertedDisease.getSynonyms().size());
        Assertions.assertEquals(otherName, convertedDisease.getSynonyms().get(0).getName());
        Assertions.assertEquals(SourceType.UniProt_HUM.name(), convertedDisease.getSynonyms().get(0).getSource());
        Assertions.assertEquals(2, convertedDisease.getCrossRefs().size());
    }

    // case 5 - Test ambiguous case - when same omim of mondo term is mapped to more than one hum disease,
    // ignore such mondo term
    @Test
    void testAmbiguousOMIMIds() {
        // given
        String uuid = UUID.randomUUID().toString();
        String name = "DISEASE-OMIM-MATCH-" + uuid;
        humDisease = DiseaseWriterTest.createDiseaseByDiseaseName(name);
        CrossRef xref1 = new CrossRef("MIM", "616033", SourceType.UniProt_HUM.name(), humDisease);
        CrossRef xref2 = new CrossRef("SOMETYPE", "SOME_VAL", SourceType.UniProt_HUM.name(), humDisease);
        humDisease.setCrossRefs(Arrays.asList(xref1, xref2));
        this.diseaseDAO.save(humDisease);
        Assertions.assertEquals(humDisease, this.diseaseDAO.findDiseaseByNameIgnoreCase(name).orElse(null));
        List<CrossRef> xrefs = this.crossRefDAO.findAllByDisease(humDisease);
        Assertions.assertEquals(2, xrefs.size());
        Assertions.assertTrue(humDisease.getSynonyms().isEmpty());
        //Another disease
        String name2 = "DISEASE-OMIM-MATCH-1" + uuid;
        humDisease2 = DiseaseWriterTest.createDiseaseByDiseaseName(name2);
        CrossRef xref21 = new CrossRef("MIM", "616033", SourceType.UniProt_HUM.name(), humDisease2);
        CrossRef xref22 = new CrossRef("SOMETYPE-1", "SOME_VAL-2", SourceType.UniProt_HUM.name(), humDisease2);
        CrossRef xref23 = new CrossRef("SOMETYPE-12", "SOME_VAL-22", SourceType.UniProt_HUM.name(), humDisease2);
        humDisease2.setCrossRefs(Arrays.asList(xref21, xref22, xref23));
        this.diseaseDAO.save(humDisease2);
        Assertions.assertEquals(humDisease2, this.diseaseDAO.findDiseaseByNameIgnoreCase(name2).orElse(null));
        List<CrossRef> xrefs2 = this.crossRefDAO.findAllByDisease(humDisease2);
        Assertions.assertEquals(3, xrefs2.size());
        Assertions.assertTrue(humDisease2.getSynonyms().isEmpty());
        // load cache
        initialize();

        // create Mondo term with same OMIM id as hum disease but the OMIM is mapped to two hum disease,
        // so we ignore such mondo ter
        OBOTerm.OBOTermBuilder termBuilder = OBOTerm.builder();
        termBuilder.name(name + "-extra12");
        termBuilder.id("MONDO:12345");
        termBuilder.xrefs(Arrays.asList("OMIM:616033", "UMLS:C4014997"));
        // then
        Disease convertedDisease = converter.process(termBuilder.build());
        Assertions.assertNull(convertedDisease);
    }

    // case 6 - Test ambiguous case - when same name of mondo term is mapped to more than one hum disease's through synonym,
    // ignore such mondo term
    @Test
    void testAmbiguousSynonyms() {
        // given
        String uuid = UUID.randomUUID().toString();
        String name = "DISEASE-OMIM-MATCH-" + uuid;
        humDisease = DiseaseWriterTest.createDiseaseByDiseaseName(name);
        String otherName = "SAME-SYNONYM-" + uuid;
        Synonym synonym = new Synonym(otherName, humDisease, SourceType.UniProt_HUM.name());
        humDisease.setSynonyms(Collections.singletonList(synonym));
        this.diseaseDAO.save(humDisease);
        Assertions.assertEquals(humDisease, this.diseaseDAO.findDiseaseByNameIgnoreCase(name).orElse(null));
        List<Synonym> syns = this.synonymDAO.findAllByDisease(humDisease);
        Assertions.assertEquals(1, syns.size());
        //Another disease with same synonym
        String name2 = "DISEASE-OMIM-MATCH-1" + uuid;
        humDisease2 = DiseaseWriterTest.createDiseaseByDiseaseName(name2);
        Synonym synonym2 = new Synonym(otherName, humDisease2, SourceType.UniProt_HUM.name());
        humDisease2.setSynonyms(Collections.singletonList(synonym2));
        this.diseaseDAO.save(humDisease2);
        Assertions.assertEquals(humDisease2, this.diseaseDAO.findDiseaseByNameIgnoreCase(name2).orElse(null));
        List<Synonym> syns2 = this.synonymDAO.findAllByDisease(humDisease2);
        Assertions.assertEquals(1, syns2.size());
        // load cache
        initialize();

        // create Mondo term with same name as synonyms in hum disease which is mapped to two disease
        // so we ignore such mondo term
        OBOTerm.OBOTermBuilder termBuilder = OBOTerm.builder();
        termBuilder.name(otherName);
        termBuilder.id("MONDO:12345");
        termBuilder.xrefs(Arrays.asList("OMIM:616033", "UMLS:C4014997"));
        // then
        Disease convertedDisease = converter.process(termBuilder.build());
        Assertions.assertNull(convertedDisease);
    }

    private void initialize() {
        ExecutionContext context = new ExecutionContext();
        Mockito.when(this.stepExecution.getExecutionContext()).thenReturn(context);
        this.converter = new MondoTermToDiseaseConverter(diseaseDAO, synonymDAO, crossRefDAO);
        this.converter.init(this.stepExecution);
    }
}
