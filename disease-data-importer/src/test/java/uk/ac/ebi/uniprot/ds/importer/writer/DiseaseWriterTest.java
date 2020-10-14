package uk.ac.ebi.uniprot.ds.importer.writer;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import uk.ac.ebi.kraken.interfaces.uniprot.UniProtEntry;
import uk.ac.ebi.kraken.interfaces.uniprot.comments.CommentType;
import uk.ac.ebi.kraken.interfaces.uniprot.comments.DiseaseAcronym;
import uk.ac.ebi.kraken.interfaces.uniprot.comments.DiseaseCommentStructured;
import uk.ac.ebi.kraken.interfaces.uniprot.comments.DiseaseId;
import uk.ac.ebi.kraken.model.uniprot.comments.DiseaseAcronymImpl;
import uk.ac.ebi.kraken.model.uniprot.comments.DiseaseIdImpl;
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
import uk.ac.ebi.uniprot.ds.common.model.Protein;
import uk.ac.ebi.uniprot.ds.importer.reader.UniProtReader;

/**
 * @author sahmad
 * @created 14/10/2020
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class DiseaseWriterTest {
    @Autowired
    private DiseaseDAO diseaseDAO;
    @Autowired
    private ProteinDAO proteinDAO;
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
    private DiseaseProteinDAO diseaseProteinDAO;
    @Autowired
    private InteractionDAO interactionDAO;

    String uuid = UUID.randomUUID().toString();
    String diseaseId = "id-" + uuid;// this id should be there in Protein
    String diseaseName = "name-" + uuid;// this name should be there in Protein
    String acronym = "acr-" + uuid;// this acronym should be there in Protein
    Map<String, Protein> proteinIdProteinMap = new HashMap<>();

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
    void testWrite() throws Exception {
        // given
        Disease disease1 = this.diseaseDAO.save(createDiseaseByDiseaseId(diseaseId));
        Assertions.assertNotNull(disease1);
        Disease disease2 = this.diseaseDAO.save(createDiseaseByDiseaseName(diseaseName));
        Assertions.assertNotNull(disease2);
        Disease disease3 = this.diseaseDAO.save(createDiseaseByAcronym(acronym));
        Assertions.assertNotNull(disease3);
        String anotherDisease = "rand-" + uuid;// this disease is not there in Protein
        Disease disease4 = this.diseaseDAO.save(createDiseaseByDiseaseId(anotherDisease));
        Assertions.assertNotNull(disease4);
        Assertions.assertEquals(4, this.diseaseDAO.findAll().size());
        // read uniprot file
        UniProtReader proteinReader = new UniProtReader("src/test/resources/sample_uniprot_sprot.dat");
        UniProtEntry uniProtEntry = proteinReader.read();
        updateUniProtDiseases(uniProtEntry);
        // write the protein
        ProteinWriter proteinWriter = new ProteinWriter(proteinIdProteinMap, this.proteinDAO);
        proteinWriter.write(Arrays.asList(uniProtEntry));
        // write the diseases
        DiseaseWriter diseaseWriter = new DiseaseWriter(proteinIdProteinMap, this.diseaseDAO, this.variantDAO);
        diseaseWriter.write(Arrays.asList(uniProtEntry));
        List<Disease> diseases = this.diseaseDAO.findAll();
        // 3 disease out of 4 should be updated and there are 15 diseases in uniprot. so total 16 disease should exist
        Assertions.assertEquals(16, diseases.size());
        List<Disease> humDiseases = diseases.stream().filter(dis -> SourceType.UniProt_HUM.name().equals(dis.getSource())).collect(Collectors.toList());
        Assertions.assertEquals(4, humDiseases.size());
        List<Disease> uniProtDiseases = diseases.stream().filter(dis -> SourceType.UniProt.name().equals(dis.getSource())).collect(Collectors.toList());
        Assertions.assertEquals(12, uniProtDiseases.size());
    }

    private void updateUniProtDiseases(UniProtEntry uniProtEntry) {
        List<DiseaseCommentStructured> dcs = uniProtEntry.getComments(CommentType.DISEASE);
        List<DiseaseCommentStructured> uniProtDiseases = dcs.stream().filter(dc -> dc.hasDefinedDisease())
                .collect(Collectors.toList());
        // update the disease id to be updated
        uk.ac.ebi.kraken.interfaces.uniprot.comments.Disease upDisease1 = uniProtDiseases.get(0).getDisease();
        DiseaseId disId = new DiseaseIdImpl();
        disId.setValue(this.diseaseId);
        upDisease1.setDiseaseId(disId);

        // update the disease name to be updated
        uk.ac.ebi.kraken.interfaces.uniprot.comments.Disease upDisease2 = uniProtDiseases.get(1).getDisease();
        DiseaseId disName = new DiseaseIdImpl();
        disName.setValue(this.diseaseName);
        upDisease2.setDiseaseId(disName);

        // update the disease acronym to be updated
        uk.ac.ebi.kraken.interfaces.uniprot.comments.Disease upDisease3 = uniProtDiseases.get(2).getDisease();
        DiseaseAcronym disAcr = new DiseaseAcronymImpl();
        disAcr.setValue(this.acronym);
        upDisease3.setDiseaseAcronym(disAcr);
        Assertions.assertEquals(15, dcs.size());
    }

    private Disease createDiseaseByDiseaseId(String diseaseId) {
        Disease.DiseaseBuilder builder = createDisease();
        builder.diseaseId(diseaseId);
        return builder.build();
    }

    private Disease createDiseaseByDiseaseName(String name) {
        Disease.DiseaseBuilder builder = createDisease();
        builder.name(name);
        return builder.build();
    }

    private Disease createDiseaseByAcronym(String acr) {
        Disease.DiseaseBuilder builder = createDisease();
        builder.acronym(acr);
        return builder.build();
    }

    private Disease.DiseaseBuilder createDisease() {
        String uuid = UUID.randomUUID().toString();
        Disease.DiseaseBuilder builder = Disease.builder();
        builder.diseaseId("DID" + uuid).name("NAME" + uuid).acronym("ACR" + uuid);
        builder.source(SourceType.UniProt_HUM.name()).desc("DESC" + uuid);
        return builder;
    }
}
