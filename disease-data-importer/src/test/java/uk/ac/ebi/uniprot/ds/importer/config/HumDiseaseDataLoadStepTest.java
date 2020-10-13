package uk.ac.ebi.uniprot.ds.importer.config;

import org.hamcrest.Matcher;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import uk.ac.ebi.uniprot.ds.common.common.SourceType;
import uk.ac.ebi.uniprot.ds.common.dao.CrossRefDAO;
import uk.ac.ebi.uniprot.ds.common.dao.DiseaseDAO;
import uk.ac.ebi.uniprot.ds.common.dao.KeywordDAO;
import uk.ac.ebi.uniprot.ds.common.dao.SynonymDAO;
import uk.ac.ebi.uniprot.ds.common.model.CrossRef;
import uk.ac.ebi.uniprot.ds.common.model.Disease;
import uk.ac.ebi.uniprot.ds.common.model.Keyword;
import uk.ac.ebi.uniprot.ds.common.model.Synonym;
import uk.ac.ebi.uniprot.ds.importer.DataImporterSpringBootApplication;
import uk.ac.ebi.uniprot.ds.importer.util.Constants;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsIterableContainingInAnyOrder.containsInAnyOrder;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {DataImporterSpringBootApplication.class, BatchConfigurationDiseaseService.class, HumDiseaseDataLoadStep.class})
class HumDiseaseDataLoadStepTest extends AbstractBaseStepTest{

    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;
    @Autowired
    private CrossRefDAO crossRefDAO;
    @Autowired
    private SynonymDAO synonymDAO;
    @Autowired
    private DiseaseDAO diseaseDAO;
    @Autowired
    private KeywordDAO keywordDAO;

    @BeforeEach
    @AfterEach
    void cleanUp() {
        this.synonymDAO.deleteAll();
        this.crossRefDAO.deleteAll();
        this.keywordDAO.deleteAll();
        this.diseaseDAO.deleteAll();
    }

    @Test
    void testHumDiseaseDataLoadStep() throws Exception {
        JobParameters jobParameters = new JobParametersBuilder().addLong("time", System.currentTimeMillis()).toJobParameters();
        // when
        JobExecution jobExecution = jobLauncherTestUtils.launchStep(Constants.DS_HUM_DISEASE_DATA_LOADER_STEP, jobParameters);
        Collection<StepExecution> actualStepExecutions = jobExecution.getStepExecutions();
        ExitStatus actualJobExitStatus = jobExecution.getExitStatus();
        // then
        // verify job status
        Assertions.assertEquals(1, actualStepExecutions.size());
        Assertions.assertEquals("COMPLETED", actualJobExitStatus.getExitCode());
        StepExecution step = actualStepExecutions.stream().collect(Collectors.toList()).get(0);
        Assertions.assertNotNull(step);
        Assertions.assertEquals(5, step.getReadCount());
        Assertions.assertEquals(5, step.getWriteCount());
        // verify data
        verifyDiseases();
        verifyDiseaseCrossReferences();
        verifyDiseaseSynonyms();
        verifyKeywords();
    }

    private void verifyDiseases() {
        // populate a map to keep disease id and its description to verify
        Map<String, String> disDesc = new HashMap<>();
        populateMap(disDesc);
        List<Disease> diseases = this.diseaseDAO.findAll();
        Assertions.assertFalse(diseases.isEmpty());
        Assertions.assertEquals(5, diseases.size());
        Matcher<Iterable<? extends String>> matcherDiseaseIds = containsInAnyOrder("3-hydroxy-3-methylglutaryl-CoA lyase deficiency", "ZTTK syndrome",
                "2,4-dienoyl-CoA reductase deficiency", "3-alpha-hydroxyacyl-CoA dehydrogenase deficiency",
                "2-aminoadipic 2-oxoadipic aciduria");
        // verify disease ids
        List<String> diseaseIds = diseases.stream().map(Disease::getDiseaseId).collect(Collectors.toList());
        assertThat(diseaseIds, matcherDiseaseIds);
        // verify disease names
        List<String> diseaseNames = diseases.stream().map(Disease::getName).collect(Collectors.toList());
        assertThat(diseaseNames, matcherDiseaseIds);
        // verify each description
        diseases.stream().forEach(disease -> Assertions.assertEquals(disDesc.get(disease.getDiseaseId()), disease.getDesc()));
        // verify acronyms
        Matcher<Iterable<? extends String>> matcherAcronums = containsInAnyOrder("DECRD", "AMOXAD", "HADH deficiency", "HMGCLD", "ZTTKS");
        List<String> actualAcronyms = diseases.stream().map(Disease::getAcronym).collect(Collectors.toList());
        assertThat(actualAcronyms, matcherAcronums);
        // verify source
        diseases.stream().map(Disease::getSource).forEach(src -> assertThat(src, is(SourceType.UniProt_HUM.name())));
        diseases.stream().map(Disease::getNote).forEach(src -> assertThat(src, nullValue()));
        verifyCommonFields(diseases);
    }

    private void populateMap(Map<String, String> disDesc) {
        disDesc.put("3-hydroxy-3-methylglutaryl-CoA lyase deficiency", "An autosomal recessive disease affecting ketogenesis and L-leucine catabolism. The disease usually appears in the first year of life after a fasting period and its clinical acute symptoms include vomiting, seizures, metabolic acidosis, hypoketotic hypoglycemia and lethargy. These symptoms sometimes progress to coma, with fatal outcome in some cases.");
        disDesc.put("ZTTK syndrome", "An autosomal dominant syndrome characterized by intellectual disability, developmental delay, malformations of the cerebral cortex, epilepsy, vision problems, musculo-skeletal abnormalities, and congenital malformations.");
        disDesc.put("2,4-dienoyl-CoA reductase deficiency", "A rare, autosomal recessive, inborn error of polyunsaturated fatty acids and lysine metabolism, resulting in mitochondrial dysfunction. Affected individuals have a severe encephalopathy with neurologic and metabolic abnormalities beginning in early infancy. Laboratory studies show increased C10:2 carnitine levels and hyperlysinemia.");
        disDesc.put("3-alpha-hydroxyacyl-CoA dehydrogenase deficiency", "An autosomal recessive, metabolic disorder with various clinical presentations including hypoglycemia, hepatoencephalopathy, myopathy or cardiomyopathy, and in some cases sudden death.");
        disDesc.put("2-aminoadipic 2-oxoadipic aciduria", "A metabolic disorder characterized by increased levels of 2-oxoadipate and 2-hydroxyadipate in the urine, and elevated 2-aminoadipate in the plasma. Patients can have mild to severe intellectual disability, muscular hypotonia, developmental delay, ataxia, and epilepsy. Most cases are asymptomatic.");
    }

    private void verifyDiseaseCrossReferences() {
        List<CrossRef> xrefs = this.crossRefDAO.findAll();
        Assertions.assertFalse(xrefs.isEmpty());
        Assertions.assertEquals(22, xrefs.size());
        Set<String> refTypes = xrefs.stream().map(CrossRef::getRefType).collect(Collectors.toSet());
        assertThat(refTypes, containsInAnyOrder("MIM", "MeSH", "MedGen", "UniProt"));
        List<String> refIds = xrefs.stream().map(CrossRef::getRefId).collect(Collectors.toList());
        assertThat(refIds, containsInAnyOrder("DI-04860", "C1291230", "204750", "C0268601", "D020167", "617140",
                "C1859817", "DI-00002", "D000592", "616034", "231530", "D028361", "CN238690", "D000015", "D008607",
                "CN037048", "DI-03673", "DI-00003", "246450", "DI-04240", "D008659", "D000592"));
        xrefs.stream().map(CrossRef::getDisease).forEach(disease -> assertThat(disease, notNullValue()));
        xrefs.stream().map(CrossRef::getSource).forEach(src -> assertThat(src, is(SourceType.UniProt_HUM.name())));
        verifyCommonFields(xrefs);
    }

    private void verifyDiseaseSynonyms() {
        List<Synonym> synonyms = this.synonymDAO.findAll();
        Assertions.assertFalse(synonyms.isEmpty());
        Assertions.assertEquals(10, synonyms.size());
        List<String> actualSynonyms = synonyms.stream().map(Synonym::getName).collect(Collectors.toList());
        assertThat(actualSynonyms, containsInAnyOrder("HMGCL deficiency",
                "ZTTK multiple congenital anomalies-mental retardation syndrome",
                "HAD deficiency", "HMG-CoA lyase deficiency", "Hydroxymethylglutaricaciduria",
                "Hydroxymethylglutaric aciduria", "SCHAD deficiency",
                "Hydroxyacyl-coenzyme A dehydrogenase deficiency",
                "HL deficiency", "Zhu-Tokita-Takenouchi-Kim syndrome"));
        synonyms.stream().map(Synonym::getSource).forEach(src -> assertThat(src, is(SourceType.UniProt_HUM.name())));
        synonyms.stream().map(Synonym::getDisease).forEach(disease -> assertThat(disease, notNullValue()));
    }

    private void verifyKeywords() {
        List<Keyword> kws = this.keywordDAO.findAll();
        Assertions.assertFalse(kws.isEmpty());
        Assertions.assertEquals(1, kws.size());
        Assertions.assertEquals("KW-0991", kws.get(0).getKeyId());
        Assertions.assertEquals("mental retardation", kws.get(0).getKeyValue());
        Assertions.assertNotNull(kws.get(0).getDisease());
        verifyCommonFields(kws);
    }
}
