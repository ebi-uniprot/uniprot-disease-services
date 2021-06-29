/*
 * Created by sahmad on 1/31/19 1:51 PM
 * UniProt Consortium.
 * Copyright (c) 2002-2019.
 *
 */

package uk.ac.ebi.uniprot.ds.importer.config;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import uk.ac.ebi.uniprot.ds.common.dao.CrossRefDAO;
import uk.ac.ebi.uniprot.ds.common.dao.DiseaseDAO;
import uk.ac.ebi.uniprot.ds.common.dao.DrugDAO;
import uk.ac.ebi.uniprot.ds.common.dao.EvidenceDAO;
import uk.ac.ebi.uniprot.ds.common.dao.FeatureLocationDAO;
import uk.ac.ebi.uniprot.ds.common.dao.GeneCoordinateDAO;
import uk.ac.ebi.uniprot.ds.common.dao.InteractionDAO;
import uk.ac.ebi.uniprot.ds.common.dao.ProteinCrossRefDAO;
import uk.ac.ebi.uniprot.ds.common.dao.ProteinDAO;
import uk.ac.ebi.uniprot.ds.common.dao.SiteMappingDAO;
import uk.ac.ebi.uniprot.ds.common.dao.SynonymDAO;
import uk.ac.ebi.uniprot.ds.common.dao.VariantDAO;
import uk.ac.ebi.uniprot.ds.importer.DataImporterSpringBootApplication;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {DataImporterSpringBootApplication.class, BatchConfigurationDiseaseService.class,
        HumDiseaseDataLoadStep.class, UniProtDataLoadStep.class, GeneCoordinateDataLoadStep.class, MondoDiseaseLoadStep.class,
        DiseaseParentChildLoadStep.class, ChEMBLDrugLoadStep.class, SiteMappingLoadStep.class, DiseaseDescendentsLoadStep.class})
class BatchConfigurationDiseaseServiceTest {

    @Autowired
    private Job importUniProtDataJob;

    @Autowired
    private JobLauncher jobLauncher;

    @AfterEach
    void cleanUp(){
            this.drugDAO.deleteAll();
            this.synonymDAO.deleteAll();
            this.variantDAO.deleteAll();
            this.proteinCrossRefDAO.deleteAll();
            this.interactionDAO.deleteAll();
            this.featureLocationDAO.deleteAll();
            this.evidenceDAO.deleteAll();
            this.diseaseDAO.deleteAll();
            this.proteinDAO.deleteAll();
            this.siteMappingDAO.deleteAll();
            this.geneCoordinateDAO.deleteAll();
            this.crossRefDAO.deleteAll();
    }

    @Test
    void testDiseaseServiceJob() throws Exception {
        JobParameters jobParameters = new JobParametersBuilder().addLong("time",System.currentTimeMillis()).toJobParameters();

        JobExecution jobExecution = jobLauncher.run(importUniProtDataJob, jobParameters);
        BatchStatus status = jobExecution.getStatus();
        Assertions.assertEquals(BatchStatus.COMPLETED, status);

//        // verify the data
//        // hum disease first
//        List<Disease> hds = this.diseaseDAO.findAll();
//        Assert.assertTrue(hds.size() > 5000);
//        List<Synonym> hsn = this.synonymDAO.findAll();
//        Assert.assertTrue(hsn.size() > 9000);
//        // protein
//        Optional<Protein> optPr = this.proteinDAO.findProteinByAccession("P22607");
//        Assert.assertTrue(optPr.isPresent());
//        Protein pr = optPr.get();
//        Assert.assertNotNull(pr.getId());
//        Assert.assertEquals("FGFR3_HUMAN", pr.getProteinId());
//        Assert.assertEquals("Fibroblast growth factor receptor 3", pr.getName());
//        Assert.assertEquals("FGFR3", pr.getGene());
//
//        //get the diseases by protein
//        Set<Disease> diseases = this.proteinDAO.findProteinByAccession(pr.getAccession())
//                .map(
//                        prot -> prot.getDiseaseProteins()
//                                .stream()
//                                .map(dp -> dp.getDisease())
//                                .collect(Collectors.toSet())
//                )
//                .orElse(null);
//        Assert.assertNotNull(diseases);
//        Assert.assertEquals(15, diseases.size());
//
//        // get synonyms
//        List<Synonym> syns = new ArrayList<>();
//        for(Disease d : diseases) {
//            syns.addAll(this.synonymDAO.findAllByDisease(d));
//        }
//        Assert.assertEquals(30, syns.size());
//
//        // get interaction
//        List<Interaction> ints = this.interactionDAO.findAllByProtein(pr);
//        Assert.assertEquals(3, ints.size());
//
//        // get protein cross ref
//        List<ProteinCrossRef> paths = this.proteinCrossRefDAO.findAllByProtein(pr);
//        Assert.assertEquals(17, paths.size());
//
//        // get protein variants
//        List<Variant> prVars = this.variantDAO.findAllByProtein(pr);
//        Assert.assertEquals(28, prVars.size());
//
//        // get all disease variants
//        List<Variant> disVars = new ArrayList<>();
//        for(Disease dis : diseases){
//            disVars.addAll(this.variantDAO.findAllByDisease(dis));
//        }
//        Assert.assertEquals(5, disVars.size());
//
//        // Get evidence for each variant
//        List<Evidence> prEvidences = new ArrayList<>();
//        for(Variant v : prVars){
//            prEvidences.addAll(this.evidenceDAO.findAllByVariant(v));
//        }
//        Assert.assertEquals(78, prEvidences.size());
//
//        // verify cross refs
//        List<CrossRef> crossRefs = this.crossRefDAO.findAll();
//        Assert.assertTrue(!crossRefs.isEmpty());

    }


    @Autowired
    private GeneCoordinateDAO geneCoordinateDAO;

    @Autowired
    private CrossRefDAO crossRefDAO;

    @Autowired
    private SynonymDAO synonymDAO;

    @Autowired
    private VariantDAO variantDAO;

    @Autowired
    private ProteinCrossRefDAO proteinCrossRefDAO;

    @Autowired
    private InteractionDAO interactionDAO;

    @Autowired
    private FeatureLocationDAO featureLocationDAO;

    @Autowired
    private EvidenceDAO evidenceDAO;

    @Autowired
    private DiseaseDAO diseaseDAO;

    @Autowired
    private ProteinDAO proteinDAO;

    @Autowired
    private SiteMappingDAO siteMappingDAO;

    @Autowired
    private DrugDAO drugDAO;

}
