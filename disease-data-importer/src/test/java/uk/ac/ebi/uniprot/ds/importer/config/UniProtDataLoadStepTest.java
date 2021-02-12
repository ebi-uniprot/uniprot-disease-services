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

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import uk.ac.ebi.kraken.interfaces.uniprot.DatabaseType;
import uk.ac.ebi.kraken.interfaces.uniprot.comments.InteractionType;
import uk.ac.ebi.kraken.interfaces.uniprot.evidences.EvidenceType;
import uk.ac.ebi.kraken.interfaces.uniprot.features.FeatureStatus;
import uk.ac.ebi.uniprot.ds.common.common.PublicationType;
import uk.ac.ebi.uniprot.ds.common.common.SourceType;
import uk.ac.ebi.uniprot.ds.common.dao.DiseaseDAO;
import uk.ac.ebi.uniprot.ds.common.dao.DiseaseProteinDAO;
import uk.ac.ebi.uniprot.ds.common.dao.EvidenceDAO;
import uk.ac.ebi.uniprot.ds.common.dao.FeatureLocationDAO;
import uk.ac.ebi.uniprot.ds.common.dao.InteractionDAO;
import uk.ac.ebi.uniprot.ds.common.dao.ProteinCrossRefDAO;
import uk.ac.ebi.uniprot.ds.common.dao.ProteinDAO;
import uk.ac.ebi.uniprot.ds.common.dao.PublicationDAO;
import uk.ac.ebi.uniprot.ds.common.dao.VariantDAO;
import uk.ac.ebi.uniprot.ds.common.model.Disease;
import uk.ac.ebi.uniprot.ds.common.model.DiseaseProtein;
import uk.ac.ebi.uniprot.ds.common.model.Evidence;
import uk.ac.ebi.uniprot.ds.common.model.FeatureLocation;
import uk.ac.ebi.uniprot.ds.common.model.Interaction;
import uk.ac.ebi.uniprot.ds.common.model.Protein;
import uk.ac.ebi.uniprot.ds.common.model.ProteinCrossRef;
import uk.ac.ebi.uniprot.ds.common.model.Publication;
import uk.ac.ebi.uniprot.ds.common.model.Variant;
import uk.ac.ebi.uniprot.ds.importer.DataImporterSpringBootApplication;
import uk.ac.ebi.uniprot.ds.importer.util.Constants;

import static org.hamcrest.collection.IsIterableContainingInAnyOrder.containsInAnyOrder;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {DataImporterSpringBootApplication.class, BatchConfigurationDiseaseService.class, UniProtDataLoadStep.class})
class UniProtDataLoadStepTest extends AbstractBaseStepTest {

    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;
    @Autowired
    private ProteinDAO proteinDAO;
    @Autowired
    private InteractionDAO interactionDAO;
    @Autowired
    private VariantDAO variantDAO;
    @Autowired
    private EvidenceDAO evidenceDAO;
    @Autowired
    private FeatureLocationDAO featureLocationDAO;
    @Autowired
    private PublicationDAO publicationDAO;
    @Autowired
    private ProteinCrossRefDAO proteinCrossRefDAO;
    @Autowired
    private DiseaseDAO diseaseDAO;
    @Autowired
    private DiseaseProteinDAO diseaseProteinDAO;

    @BeforeEach
    @AfterEach
    void cleanUp() {
        this.evidenceDAO.deleteAll();
        this.variantDAO.deleteAll();
        this.featureLocationDAO.deleteAll();
        this.publicationDAO.deleteAll();
        this.interactionDAO.deleteAll();
        this.proteinCrossRefDAO.deleteAll();
        this.diseaseProteinDAO.deleteAll();
        this.proteinDAO.deleteAll();
        this.diseaseDAO.deleteAll();
    }

    @Test
    void testUniProtDataLoadStep() throws Exception {
        // create proteins with diseases and other dependent objects by running the Step and then verify them
        JobParameters jobParameters = new JobParametersBuilder().addLong("time", System.currentTimeMillis()).toJobParameters();
        // when
        JobExecution jobExecution = jobLauncherTestUtils.launchStep(Constants.DS_UNIPROT_DATA_LOADER_STEP, jobParameters);
        Collection<StepExecution> actualStepExecutions = jobExecution.getStepExecutions();
        ExitStatus actualJobExitStatus = jobExecution.getExitStatus();
        // then
        // verify job status
        Assertions.assertEquals(1, actualStepExecutions.size());
        Assertions.assertEquals("COMPLETED", actualJobExitStatus.getExitCode());
        StepExecution step = actualStepExecutions.stream().collect(Collectors.toList()).get(0);
        Assertions.assertNotNull(step);
        Assertions.assertEquals(1, step.getReadCount());
        Assertions.assertEquals(1, step.getWriteCount());
        // and verify data of different kinds
        verifyProteins();
        verifyProteinCrossRef();
        verifyPublications();
        verifyFeatureLocations();
        verifyVariantEvidences();
        verifyVariants();
        verifyInteractions();
        verifyDiseases();
        verifyDiseaseProteins();
    }

    private void verifyProteins() {
        List<Protein> proteins = this.proteinDAO.findAll();
        assertFalse(proteins.isEmpty());
        assertEquals(1, proteins.size());
        Protein protein = proteins.get(0);
        assertEquals("FGFR3_HUMAN", protein.getProteinId());
        assertEquals("P22607", protein.getAccession());
        assertEquals("Fibroblast growth factor receptor 3", protein.getName());
        assertEquals("FGFR3", protein.getGene());
        assertEquals("Tyrosine-protein kinase that acts as cell-surface receptor for fibroblast growth factors " +
                "and plays an essential role in the regulation of cell proliferation, differentiation and apoptosis. " +
                "Plays an essential role in the regulation of chondrocyte differentiation, proliferation and apoptosis, " +
                "and is required for normal skeleton development. Regulates both osteogenesis and postnatal bone mineralization " +
                "by osteoblasts. Promotes apoptosis in chondrocytes, but can also promote cancer cell proliferation. " +
                "Required for normal development of the inner ear. Phosphorylates PLCG1, CBL and FRS2. " +
                "Ligand binding leads to the activation of several signaling cascades. Activation of PLCG1 leads to the " +
                "production of the cellular signaling molecules diacylglycerol and inositol 1,4,5-trisphosphate. " +
                "Phosphorylation of FRS2 triggers recruitment of GRB2, GAB1, PIK3R1 and SOS1, and mediates activation of RAS, " +
                "MAPK1/ERK2, MAPK3/ERK1 and the MAP kinase signaling pathway, as well as of the AKT1 signaling pathway. " +
                "Plays a role in the regulation of vitamin D metabolism. Mutations that lead to constitutive kinase activation " +
                "or impair normal FGFR3 maturation, internalization and degradation lead to aberrant signaling. Over-expressed or " +
                "constitutively activated FGFR3 promotes activation of PTPN11/SHP2, STAT1, STAT5A and STAT5B. Secreted isoform " +
                "3 retains its capacity to bind FGF1 and FGF2 and hence may interfere with FGF signaling", protein.getDesc());
        verifyCommonFields(proteins);
    }

    private void verifyProteinCrossRef() {
        List<ProteinCrossRef> xrefs = this.proteinCrossRefDAO.findAll();
        Assertions.assertFalse(xrefs.isEmpty());
        Assertions.assertEquals(20, xrefs.size());
        Set<String> allowedDBTypes = new HashSet<>(Arrays.asList(DatabaseType.REACTOME.getName(), DatabaseType.CHEMBL.getName(),
                DatabaseType.OPENTARGETS.getName(), DatabaseType.DISGENET.getName()));
        xrefs.stream().map(ProteinCrossRef::getDbType).forEach(dbType -> assertTrue(allowedDBTypes.contains(dbType)));
        xrefs.stream().map(ProteinCrossRef::getProtein).forEach(protein -> assertNotNull(protein));
        Matcher<Iterable<? extends String>> expectedPrimaryIds = containsInAnyOrder("2261", "CHEMBL2742", "ENSG00000068078",
                "R-HSA-109704", "R-HSA-1257604", "R-HSA-1839130", "R-HSA-190371", "R-HSA-190372", "R-HSA-2033514",
                "R-HSA-2033515", "R-HSA-2219530", "R-HSA-5654227", "R-HSA-5654704", "R-HSA-5654706", "R-HSA-5654710",
                "R-HSA-5654732", "R-HSA-5673001", "R-HSA-6811558", "R-HSA-8853334", "R-HSA-8853338");
        List<String> actualPrimaryIds = xrefs.stream().map(ProteinCrossRef::getPrimaryId).collect(Collectors.toList());
        assertThat(actualPrimaryIds, expectedPrimaryIds);
        Matcher<Iterable<? extends String>> expectedDescription = containsInAnyOrder("-", "-", "-", "PI3K Cascade",
                "PIP3 activates AKT signaling", "Signaling by activated point mutants of FGFR3",
                "FGFR3b ligand binding and activation", "FGFR3c ligand binding and activation",
                "FGFR3 mutant receptor activation", "t(4;14) translocations of FGFR3",
                "Constitutive Signaling by Aberrant PI3K in Cancer", "Phospholipase C-mediated cascade, FGFR3",
                "SHC-mediated cascade:FGFR3", "FRS-mediated FGFR3 signaling", "PI-3K cascade:FGFR3",
                "Negative regulation of FGFR3 signaling", "RAF/MAP kinase cascade",
                "PI5P, PP2A and IER3 Regulate PI3K/AKT Signaling", "Signaling by FGFR3 fusions in cancer",
                "Signaling by FGFR3 point mutants in cancer");
        List<String> actualDesc = xrefs.stream().map(ProteinCrossRef::getDescription).collect(Collectors.toList());
        assertThat(actualDesc, expectedDescription);
        List<String> isoformIds = xrefs.stream().map(ProteinCrossRef::getIsoformId)
                .filter(iso -> !iso.isEmpty())
                .collect(Collectors.toList());
        assertThat(isoformIds, containsInAnyOrder("P22607-2", "P22607-1"));
        xrefs.stream().map(ProteinCrossRef::getThird).forEach(t -> assertThat(t, nullValue()));
        xrefs.stream().map(ProteinCrossRef::getFourth).forEach(f -> assertThat(f, nullValue()));
        verifyCommonFields(xrefs);
    }

    private void verifyPublications() {
        List<Publication> allPublications = this.publicationDAO.findAll();
        Assertions.assertFalse(allPublications.isEmpty());
        Assertions.assertEquals(100, allPublications.size());
        allPublications.stream().map(Publication::getPubType).forEach(type -> assertThat(type, is(PublicationType.PubMed.name())));
        allPublications.stream().map(Publication::getPubId).forEach(id -> assertThat(id, notNullValue()));

        List<Publication> proteinPublications = allPublications.stream()
                .filter(pub -> Objects.nonNull(pub.getProtein())).collect(Collectors.toList());
        Assertions.assertEquals(63, proteinPublications.size());

        List<Publication> diseasePublications = allPublications.stream()
                .filter(pub -> Objects.nonNull(pub.getDisease())).collect(Collectors.toList());
        Assertions.assertEquals(37, diseasePublications.size());
        verifyCommonFields(allPublications);
    }

    private void verifyFeatureLocations() {
        List<FeatureLocation> fls = this.featureLocationDAO.findAll();
        Assertions.assertFalse(fls.isEmpty());
        for (FeatureLocation fl : fls) {
            Assertions.assertEquals("EXACT", fl.getStartModifier());
            Assertions.assertEquals("EXACT", fl.getEndModifier());
            Assertions.assertNotNull(fl.getStartId());
            Assertions.assertNotNull(fl.getEndId());
        }
        verifyCommonFields(fls);
    }

    private void verifyVariants() {
        List<Variant> variants = this.variantDAO.findAll();
        Assertions.assertEquals(28, variants.size());
        variants.stream().map(Variant::getFeatureId).forEach(fl -> Assertions.assertNotNull(fl));
        variants.stream().map(Variant::getProtein).forEach(pr -> Assertions.assertNotNull(pr));
        variants.stream().map(Variant::getFeatureId).forEach(fid -> Assertions.assertTrue(fid.startsWith("VAR_")));
        variants.stream().map(Variant::getFeatureStatus)
                .forEach(fs -> Assertions.assertEquals(FeatureStatus.EXPERIMENTAL.getName(), fs));
        variants.stream().map(Variant::getReport)
                .forEach(rep -> Assertions.assertNotNull(rep));
        List<Variant> varWithDiseases = variants.stream().filter(variant -> Objects.nonNull(variant.getDisease())).collect(Collectors.toList());
        Assertions.assertEquals(5, varWithDiseases.size());
        List<Variant> varWithoutDiseases = variants.stream().filter(variant -> Objects.isNull(variant.getDisease())).collect(Collectors.toList());
        Assertions.assertEquals(23, varWithoutDiseases.size());
        Matcher<Iterable<? extends String>> expectedOriginalSeq = containsInAnyOrder("R", "G", "S", "G", "F", "A",
                "K", "C", "Y", "N", "G", "K", "N", "D", "A", "I", "S", "I", "K", "T", "E", "N", "T", "G", "A", "R", "D",
                "P");
        List<String> actualOriginalSeq = variants.stream().map(Variant::getOrigSeq).collect(Collectors.toList());
        assertThat(actualOriginalSeq, expectedOriginalSeq);

        Matcher<Iterable<? extends String>> expectedAlternateSeq = containsInAnyOrder("C", "R", "C", "C", "L", "T", "Q", "R",
                "C", "S", "R", "M", "K", "N", "T", "F", "C", "V", "E", "S", "K", "T", "M", "C", "E", "H", "N", "R");
        List<String> actualAlternateSeq = variants.stream().map(Variant::getAltSeq).collect(Collectors.toList());
        assertThat(actualAlternateSeq, expectedAlternateSeq);

        Matcher<Iterable<? extends String>> expectedVariantReports = containsInAnyOrder("in KERSEB, BLC, " +
                        "keratinocytic non-epidermolytic nevus and TD1; severe and lethal; also found as somatic " +
                        "mutation in one patient with multiple myeloma; constitutive dimerization and kinase activation; " +
                        "dbSNP:rs121913482", "in keratinocytic non-epidermolytic nevus and ACH; very common mutation; " +
                        "constitutively activated kinase with impaired internalization and degradation, resulting in prolonged " +
                        "FGFR3 signaling; dbSNP:rs28931614", "in KERSEB, BLC, cervical cancer and TD1; dbSNP:rs121913483",
                "in KERSEB, BLC, keratinocytic non-epidermolytic nevus and TD1; dbSNP:rs121913479", "in dbSNP:rs17881656",
                "in dbSNP:rs17884368", "in hypochondroplasia and BLC; in hypochondroplasia the form is milder than that seen in " +
                        "individuals with the K-540 or M-650 mutations; constitutively activated kinase; dbSNP:rs78311289",
                "in a colorectal adenocarcinoma sample; somatic mutation", "in KERSEB and TD1; disulfide-linked dimer with " +
                        "constitutive kinase activation; dbSNP:rs121913485", "in hypochondroplasia; mild; dbSNP:rs77722678",
                "in dbSNP:rs2305178", "in KERSEB, ACH, TD1 and SADDAN; constitutively activated kinase with impaired " +
                        "internalization and degradation, resulting in prolonged FGFR3 signaling; dbSNP:rs121913105",
                "in hypochondroplasia; dbSNP:rs28933068", "", "in dbSNP:rs17882190", "in dbSNP:rs17880763",
                "in KERSEB and TD1; dbSNP:rs121913484", "in hypochondroplasia; dbSNP:rs80053154",
                "in KERSEB, TD2, TGCT and BLC; bladder transitional cell carcinoma; somatic mutation; " +
                        "constitutively activated kinase with impaired internalization and degradation, resulting in " +
                        "prolonged FGFR3 signaling; dbSNP:rs78311289", "in a lung adenocarcinoma sample; somatic mutation",
                "in colorectal cancer; dbSNP:rs121913111", "in hypochondroplasia; dbSNP:rs77722678", "",
                "in ACH; dbSNP:rs75790268", "in CAN; dbSNP:rs28931615", "in CATSHLS; dbSNP:rs121913113",
                "in LADDS; dbSNP:rs121913112", "in MNKS; also some individuals with autosomal dominant congenital " +
                        "sensorineural deafness without craniosynostosis; dbSNP:rs4647924");

        List<String> actualVariantReports = variants.stream().map(Variant::getReport).collect(Collectors.toList());
        assertThat(actualVariantReports, expectedVariantReports);
        verifyCommonFields(variants);
    }

    private void verifyInteractions() {
        List<Interaction> interactions = this.interactionDAO.findAll();
        Assertions.assertEquals(309, interactions.size());
        interactions.stream().map(Interaction::getType).forEach(it -> assertEquals(InteractionType.BINARY.name(), it));
//        Matcher<Iterable<? extends String>> expectedAccessions = containsInAnyOrder("P22607", "P08238", "P05230");
//        List<String> actualAccessions = interactions.stream().map(Interaction::getAccession).collect(Collectors.toList());
//        assertThat(actualAccessions, expectedAccessions);
//        Matcher<Iterable<? extends String>> expectedGenes = containsInAnyOrder("FGFR3", "HSP90AB1", "FGF1");
//        List<String> actualGenes = interactions.stream().map(Interaction::getGene).collect(Collectors.toList());
//        assertThat(actualGenes, expectedGenes);
//        assertThat(interactions.stream().map(Interaction::getExperimentCount).collect(Collectors.toList()),
//                containsInAnyOrder(4, 2, 3));
//        assertThat(interactions.stream().map(Interaction::getFirstInteractor).collect(Collectors.toList()),
//                containsInAnyOrder("EBI-348399", "EBI-348399", "EBI-348399"));
//        assertThat(interactions.stream().map(Interaction::getSecondInteractor).collect(Collectors.toList()),
//                containsInAnyOrder("EBI-348399", "EBI-352572", "EBI-698068"));
//        interactions.stream().map(Interaction::getProtein).forEach(pr -> assertNotNull(pr));
        verifyCommonFields(interactions);
    }

    private void verifyVariantEvidences() {
        List<Evidence> evidences = this.evidenceDAO.findAll();
        Assertions.assertEquals(78, evidences.size());
        evidences.stream().map(Evidence::getEvidenceId).forEach(id -> assertTrue(id.startsWith("ECO:")));
        evidences.stream().map(Evidence::getEvidenceId).forEach(id -> assertTrue(id.contains("|")));
        evidences.stream().map(Evidence::getType).forEach(type -> assertEquals(EvidenceType.EXPERIMENTAL.getValue(), type));
        evidences.stream().map(Evidence::getAttribute).forEach(attr -> assertNotNull(attr));
        evidences.stream().map(Evidence::getCode).forEach(code -> assertEquals("experimental evidence", code));
        evidences.stream().map(Evidence::getUseECOCode).forEach(useCode -> assertTrue(useCode));
        evidences.stream().map(Evidence::getTypeValue).forEach(typeVal -> assertTrue("PubMed".equals(typeVal) || typeVal.isEmpty()));
        evidences.stream().filter(ev -> "PubMed".equals(ev.getTypeValue())).map(Evidence::getHasTypeValue).forEach(htv -> assertTrue(htv));
        evidences.stream().filter(ev -> ev.getTypeValue().isEmpty()).map(Evidence::getHasTypeValue).forEach(htv -> assertFalse(htv));
        evidences.stream().map(Evidence::getVariant).forEach(variant -> assertNotNull(variant));
        Matcher<Iterable<? extends String>> evIds = containsInAnyOrder("ECO:0000269|PubMed:15772091",
                "ECO:0000269|PubMed:10215410", "ECO:0000269|PubMed:10471491", "ECO:0000269|PubMed:15772091",
                "ECO:0000269|PubMed:17561467", "ECO:0000269|PubMed:17344846", "ECO:0000269|Ref.4", "ECO:0000269|PubMed:15772091",
                "ECO:0000269|PubMed:17344846", "ECO:0000269|PubMed:17509076", "ECO:0000269|Ref.4", "ECO:0000269|PubMed:9207791",
                "ECO:0000269|PubMed:15772091", "ECO:0000269|PubMed:12297284", "ECO:0000269|PubMed:10360402",
                "ECO:0000269|PubMed:17344846", "ECO:0000269|PubMed:8754806", "ECO:0000269|PubMed:14534538",
                "ECO:0000269|PubMed:7773297", "ECO:0000269|PubMed:11746040", "ECO:0000269|PubMed:9207791",
                "ECO:0000269|PubMed:17344846", "ECO:0000269|PubMed:7773297", "ECO:0000269|PubMed:9042914",
                "ECO:0000269|PubMed:7773297", "ECO:0000269|PubMed:9525367", "ECO:0000269|PubMed:7847369",
                "ECO:0000269|PubMed:8845844", "ECO:0000269|PubMed:9950359", "ECO:0000269|PubMed:9452043",
                "ECO:0000269|PubMed:17561467", "ECO:0000269|PubMed:17561467", "ECO:0000269|PubMed:10360402",
                "ECO:0000269|PubMed:12707965", "ECO:0000269|PubMed:17344846", "ECO:0000269|PubMed:11325814",
                "ECO:0000269|PubMed:17935505", "ECO:0000269|PubMed:11294897", "ECO:0000269|PubMed:11529856",
                "ECO:0000269|PubMed:16841094", "ECO:0000269|PubMed:17344846", "ECO:0000269|PubMed:10777366",
                "ECO:0000269|PubMed:15772091", "ECO:0000269|PubMed:8845844", "ECO:0000269|PubMed:8589699",
                "ECO:0000269|PubMed:9207791", "ECO:0000269|PubMed:7493034", "ECO:0000269|PubMed:10471491",
                "ECO:0000269|PubMed:17509076", "ECO:0000269|PubMed:17509076", "ECO:0000269|Ref.4",
                "ECO:0000269|PubMed:12297284", "ECO:0000269|PubMed:19855393", "ECO:0000269|Ref.4",
                "ECO:0000269|PubMed:10471491", "ECO:0000269|PubMed:8845844", "ECO:0000269|PubMed:10471491",
                "ECO:0000269|PubMed:15772091", "ECO:0000269|PubMed:10611230", "ECO:0000269|PubMed:8754806",
                "ECO:0000269|PubMed:17033969", "ECO:0000269|PubMed:7670477", "ECO:0000269|PubMed:17145761",
                "ECO:0000269|PubMed:10360402", "ECO:0000269|PubMed:8845844", "ECO:0000269|PubMed:9790257",
                "ECO:0000269|PubMed:8599935", "ECO:0000269|PubMed:16501574", "ECO:0000269|PubMed:16841094",
                "ECO:0000269|PubMed:16841094", "ECO:0000269|PubMed:10053006", "ECO:0000269|Ref.4",
                "ECO:0000269|PubMed:11314002", "ECO:0000269|PubMed:15772091", "ECO:0000269|PubMed:8078586",
                "ECO:0000269|PubMed:11055896", "ECO:0000269|PubMed:10671061", "ECO:0000269|PubMed:7758520");
        List<String> actualEvIds = evidences.stream().map(Evidence::getEvidenceId).collect(Collectors.toList());
        assertThat(actualEvIds, evIds);

        Matcher<Iterable<? extends String>> expectedEvAttribs = containsInAnyOrder("15772091", "10215410", "10471491",
                "15772091", "17561467", "17344846", "Ref.4", "15772091", "17344846", "17509076", "Ref.4", "9207791",
                "15772091", "12297284", "10360402", "17344846", "8754806", "14534538", "7773297", "11746040", "9207791",
                "17344846", "7773297", "9042914", "7773297", "9525367", "7847369", "8845844", "9950359", "9452043",
                "17561467", "17561467", "10360402", "12707965", "17344846", "11325814", "17935505", "11294897", "11529856",
                "16841094", "17344846", "10777366", "15772091", "8845844", "8589699", "9207791", "7493034", "10471491",
                "17509076", "17509076", "Ref.4", "12297284", "19855393", "Ref.4", "10471491", "8845844", "10471491",
                "15772091", "10611230", "8754806", "17033969", "7670477", "17145761", "10360402", "8845844", "9790257",
                "8599935", "16501574", "16841094", "16841094", "10053006", "Ref.4", "11314002", "15772091", "8078586",
                "11055896", "10671061", "7758520");
        List<String> actualEvAttribs = evidences.stream().map(Evidence::getAttribute).collect(Collectors.toList());
        assertThat(actualEvAttribs, expectedEvAttribs);
        verifyCommonFields(evidences);
    }

    private void verifyDiseases(){
        List<Disease> diseases = this.diseaseDAO.findAll();
        Assertions.assertEquals(15, diseases.size());
        diseases.stream().map(Disease::getSource).forEach(src -> assertEquals(SourceType.UniProt.name(), src));
        Matcher<Iterable<? extends String>> expectedDiseaseIds = containsInAnyOrder("Achondroplasia",
                "Crouzon syndrome with acanthosis nigricans", "Thanatophoric dysplasia 1", "Thanatophoric dysplasia 2",
                "Hypochondroplasia", "Bladder cancer", "Cervical cancer", "Camptodactyly, tall stature, and hearing loss syndrome",
                "Multiple myeloma", "Lacrimo-auriculo-dento-digital syndrome", "Keratinocytic non-epidermolytic nevus",
                "Muenke syndrome", "Keratosis, seborrheic", "Testicular germ cell tumor",
                "Achondroplasia, severe, with developmental delay and acanthosis nigricans");
        List<String> actualDiseaseIds = diseases.stream().map(Disease::getDiseaseId).collect(Collectors.toList());
        assertThat(actualDiseaseIds, expectedDiseaseIds);
        List<String> actualDiseaseName = diseases.stream().map(Disease::getName).collect(Collectors.toList());
        assertThat(actualDiseaseName, expectedDiseaseIds);
        Matcher<Iterable<? extends String>> expectedAcronyms = containsInAnyOrder("ACH", "CAN", "TD1", "TD2",
                "HCH", "BLC", "CERCA", "CATSHLS", "MM", "LADDS", "KNEN", "MNKS", "KERSEB", "TGCT", "SADDAN");
        List<String> actualAcronyms = diseases.stream().map(Disease::getAcronym).collect(Collectors.toList());
        assertThat(actualAcronyms, expectedAcronyms);
        Matcher<Iterable<? extends String>> expectedNotes = containsInAnyOrder(
                "The disease is caused by variants affecting the gene represented in this entry.",
                "The disease is caused by variants affecting the gene represented in this entry.",
                "The disease is caused by variants affecting the gene represented in this entry.",
                "The disease is caused by variants affecting the gene represented in this entry.",
                "The disease is caused by variants affecting the gene represented in this entry.",
                "Disease susceptibility is associated with variants affecting the gene represented in this entry. Somatic mutations can constitutively activate FGFR3.",
                "The gene represented in this entry is involved in disease pathogenesis.",
                "The disease is caused by variants affecting the gene represented in this entry.",
                "The gene represented in this entry may be involved in disease pathogenesis. A chromosomal aberration involving FGFR3 is found in multiple myeloma. Translocation t(4;14)(p16.3;q32.3) with the IgH locus.",
                "The disease is caused by variants affecting the gene represented in this entry.",
                "The disease is caused by variants affecting the gene represented in this entry.",
                "The disease is caused by variants affecting the gene represented in this entry.",
                "The disease is caused by variants affecting the gene represented in this entry.",
                "The gene represented in this entry may be involved in disease pathogenesis.",
                "The disease is caused by variants affecting the gene represented in this entry.");
        List<String> actualNotes = diseases.stream().map(Disease::getNote).collect(Collectors.toList());
        assertThat(actualNotes, expectedNotes);
        Matcher<Iterable<? extends String>> expectedDescriptions = containsInAnyOrder("A frequent form of short-limb dwarfism. It is characterized by a long, narrow trunk, short extremities, particularly in the proximal (rhizomelic) segments, a large head with frontal bossing, hypoplasia of the midface and a trident configuration of the hands. ACH is an autosomal dominant disease.", "Classic Crouzon disease which is caused by mutations in the FGFR2 gene is characterized by craniosynostosis (premature fusion of the skull sutures), and facial hypoplasia. Crouzon syndrome with acanthosis nigricans (a skin disorder characterized by pigmentation anomalies), CAN, is considered to be an independent disorder from classic Crouzon syndrome. CAN is characterized by additional more severe physical manifestation, such as Chiari malformation, hydrocephalus, and atresia or stenosis of the choanas, and is caused by a specific mutation (Ala-391 to Glu) in the transmembrane domain of FGFR3. It is proposed to have an autosomal dominant mode of inheritance.", "A neonatal lethal skeletal dysplasia. Affected individuals manifest severe shortening of the limbs with macrocephaly, narrow thorax, short ribs, and curved femurs.", "A neonatal lethal skeletal dysplasia causing severe shortening of the limbs, narrow thorax and short ribs. Patients with thanatophoric dysplasia type 2 have straight femurs and cloverleaf skull.", "Autosomal dominant disease and is characterized by disproportionate short stature. It resembles achondroplasia, but with a less severe phenotype.", "A malignancy originating in tissues of the urinary bladder. It often presents with multiple tumors appearing at different times and at different sites in the bladder. Most bladder cancers are transitional cell carcinomas that begin in cells that normally make up the inner lining of the bladder. Other types of bladder cancer include squamous cell carcinoma (cancer that begins in thin, flat cells) and adenocarcinoma (cancer that begins in cells that make and release mucus and other fluids). Bladder cancer is a complex disorder with both genetic and environmental influences.", "A malignant neoplasm of the cervix, typically originating from a dysplastic or premalignant lesion previously present at the active squamocolumnar junction. The transformation from mild dysplastic to invasive carcinoma generally occurs slowly within several years, although the rate of this process varies widely. Carcinoma in situ is particularly known to precede invasive cervical cancer in most cases. Cervical cancer is strongly associated with infection by oncogenic types of human papillomavirus.", "An autosomal dominant syndrome characterized by permanent and irreducible flexion of one or more fingers of the hand and/or feet, tall stature, scoliosis and/or a pectus excavatum, and hearing loss. Affected individuals have developmental delay and/or mental retardation, and several of these have microcephaly. Radiographic findings included tall vertebral bodies with irregular borders and broad femoral metaphyses with long tubular shafts. On audiological exam, each tested member have bilateral sensorineural hearing loss and absent otoacoustic emissions. The hearing loss was congenital or developed in early infancy, progressed variably in early childhood, and range from mild to severe. Computed tomography and magnetic resonance imaging reveal that the brain, middle ear, and inner ear are structurally normal.", "A malignant tumor of plasma cells usually arising in the bone marrow and characterized by diffuse involvement of the skeletal system, hyperglobulinemia, Bence-Jones proteinuria and anemia. Complications of multiple myeloma are bone pain, hypercalcemia, renal failure and spinal cord compression. The aberrant antibodies that are produced lead to impaired humoral immunity and patients have a high prevalence of infection. Amyloidosis may develop in some patients. Multiple myeloma is part of a spectrum of diseases ranging from monoclonal gammopathy of unknown significance (MGUS) to plasma cell leukemia.", "An autosomal dominant ectodermal dysplasia, a heterogeneous group of disorders due to abnormal development of two or more ectodermal structures. Lacrimo-auriculo-dento-digital syndrome is characterized by aplastic/hypoplastic lacrimal and salivary glands and ducts, cup-shaped ears, hearing loss, hypodontia and enamel hypoplasia, and distal limb segments anomalies. In addition to these cardinal features, facial dysmorphism, malformations of the kidney and respiratory system and abnormal genitalia have been reported. Craniosynostosis and severe syndactyly are not observed.", "Epidermal nevi of the common, non-organoid and non-epidermolytic type are benign skin lesions and may vary in their extent from a single (usually linear) lesion to widespread and systematized involvement. They may be present at birth or develop early during childhood.", "A condition characterized by premature closure of coronal suture of skull during development (coronal craniosynostosis), which affects the shape of the head and face. It may be uni- or bilateral. When bilateral, it is characterized by a skull with a small antero-posterior diameter (brachycephaly), often with a decrease in the depth of the orbits and hypoplasia of the maxillae. Unilateral closure of the coronal sutures leads to flattening of the orbit on the involved side (plagiocephaly). The intellect is normal. In addition to coronal craniosynostosis some affected individuals show skeletal abnormalities of hands and feet, sensorineural hearing loss, mental retardation and respiratory insufficiency.", "A common benign skin tumor. Seborrheic keratoses usually begin with the appearance of one or more sharply defined, light brown, flat macules. The lesions may be sparse or numerous. As they initially grow, they develop a velvety to finely verrucous surface, followed by an uneven warty surface with multiple plugged follicles and a dull or lackluster appearance.", "A common malignancy in males representing 95% of all testicular neoplasms. TGCTs have various pathologic subtypes including: unclassified intratubular germ cell neoplasia, seminoma (including cases with syncytiotrophoblastic cells), spermatocytic seminoma, embryonal carcinoma, yolk sac tumor, choriocarcinoma, and teratoma.", "A severe form of achondroplasia associated with developmental delay and acanthosis nigricans. Patients manifest short-limb dwarfism, with a long, narrow trunk, short extremities, particularly in the proximal (rhizomelic) segments, a large head with frontal bossing, hypoplasia of the midface and a trident configuration of the hands. Acanthosis nigricans is a skin condition characterized by brown-pigmented, velvety verrucosities in body folds and creases.");
        List<String> actualDescriptions = diseases.stream().map(Disease::getDesc).collect(Collectors.toList());
        assertThat(actualDescriptions, expectedDescriptions);
        verifyCommonFields(diseases);
    }

    private void verifyDiseaseProteins(){
        List<DiseaseProtein> disProts = this.diseaseProteinDAO.findAll();
        assertEquals(15, disProts.size());
    }
}
