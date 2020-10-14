package uk.ac.ebi.uniprot.ds.importer.config;

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
import java.util.List;
import java.util.stream.Collectors;

import uk.ac.ebi.uniprot.ds.common.dao.DiseaseDAO;
import uk.ac.ebi.uniprot.ds.common.dao.DiseaseProteinDAO;
import uk.ac.ebi.uniprot.ds.common.dao.EvidenceDAO;
import uk.ac.ebi.uniprot.ds.common.dao.FeatureLocationDAO;
import uk.ac.ebi.uniprot.ds.common.dao.GeneCoordinateDAO;
import uk.ac.ebi.uniprot.ds.common.dao.InteractionDAO;
import uk.ac.ebi.uniprot.ds.common.dao.ProteinCrossRefDAO;
import uk.ac.ebi.uniprot.ds.common.dao.ProteinDAO;
import uk.ac.ebi.uniprot.ds.common.dao.PublicationDAO;
import uk.ac.ebi.uniprot.ds.common.dao.VariantDAO;
import uk.ac.ebi.uniprot.ds.common.model.GeneCoordinate;
import uk.ac.ebi.uniprot.ds.importer.DataImporterSpringBootApplication;
import uk.ac.ebi.uniprot.ds.importer.util.Constants;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {DataImporterSpringBootApplication.class, BatchConfigurationDiseaseService.class, GeneCoordinateDataLoadStep.class})
class GeneCoordinateDataLoadStepTest extends AbstractBaseStepTest {

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
    @Autowired
    private GeneCoordinateDAO geneCoordinateDAO;

    @BeforeEach
    @AfterEach
    void cleanUp() {
        this.geneCoordinateDAO.deleteAll();
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
    void testGeneCoordDataLoadStep() throws Exception {
        // when
        // run protein data load first to create protein used by gene corod step
        runProteinDataLoadStep();
        // create proteins with diseases and other dependent objects by running the Step and then verify them
        JobParameters jobParameters = new JobParametersBuilder().addLong("time", System.currentTimeMillis()).toJobParameters();
        JobExecution jobExecution = jobLauncherTestUtils.launchStep(Constants.DS_GENE_COORD_LOADER_STEP, jobParameters);
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
        // and verify data
        verifyGeneCoords();
    }

    private void verifyGeneCoords(){
        List<GeneCoordinate> geneCoords = this.geneCoordinateDAO.findAll();
        Assertions.assertEquals(1, geneCoords.size());
        GeneCoordinate gc = geneCoords.get(0);
        Assertions.assertEquals("4", gc.getChromosomeNumber());
        Assertions.assertEquals(Long.valueOf(1793935L), gc.getStartPos());
        Assertions.assertEquals(Long.valueOf(1807259), gc.getEndPos());
        Assertions.assertEquals("ENSG00000068078", gc.getEnGeneId());
        Assertions.assertEquals("ENST00000440486", gc.getEnTranscriptId());
        Assertions.assertEquals("ENSP00000414914", gc.getEnTranslationId());
        Assertions.assertNotNull(gc.getProtein());
        verifyCommonFields(geneCoords);
    }

    private void runProteinDataLoadStep(){
        JobParameters jobParameters = new JobParametersBuilder().addLong("time", System.currentTimeMillis()).toJobParameters();
        JobExecution jobExecution = jobLauncherTestUtils.launchStep(Constants.DS_UNIPROT_DATA_LOADER_STEP, jobParameters);
        Collection<StepExecution> actualStepExecutions = jobExecution.getStepExecutions();
        ExitStatus actualJobExitStatus = jobExecution.getExitStatus();
        // verify job status
        Assertions.assertEquals(1, actualStepExecutions.size());
        Assertions.assertEquals("COMPLETED", actualJobExitStatus.getExitCode());
    }
}
