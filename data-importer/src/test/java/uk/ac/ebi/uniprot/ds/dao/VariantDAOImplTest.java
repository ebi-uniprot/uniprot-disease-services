/*
 * Created by sahmad on 1/25/19 1:53 PM
 * UniProt Consortium.
 * Copyright (c) 2002-2019.
 *
 */

package uk.ac.ebi.uniprot.ds.dao;

import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.PropertySource;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.ac.ebi.uniprot.ds.model.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class VariantDAOImplTest{
    @Autowired
    private VariantDAO variantDAO;
    @Autowired
    private FeatureLocationDAO flDAO;
    @Autowired
    private ProteinDAO proteinDAO;
    @Autowired
    private DiseaseDAO diseaseDAO;

    private Variant variant;
    private FeatureLocation featureLocation;
    private Evidence evidence;
    private Protein protein;
    private Disease disease;

    @AfterEach
    void cleanUp(){
        if(this.variant != null){
            this.variantDAO.deleteById(this.variant.getId());
            this.variant = null;
        }

        if(this.featureLocation != null){
            this.flDAO.deleteById(this.featureLocation.getId());
            this.featureLocation = null;
        }

        if(this.protein != null){
            this.proteinDAO.delete(this.protein);
            this.protein = null;
        }

        if(this.disease != null){
            this.diseaseDAO.delete(this.disease);
            this.disease = null;
        }
    }

    @Test
    void testCreateVariant(){
        this.variant = VariantTest.createVariantObject(UUID.randomUUID().toString());
        this.variantDAO.save(this.variant);
        assertNotNull(this.variant.getId(), "unable to create variant");
    }

    @Test
    void testCreateVariantWithFeatureLocation(){
        String uuid = UUID.randomUUID().toString();
        // create Feature Location
        this.featureLocation = FeatureLocationTest.createFeatureLocationObject(uuid);
        this.flDAO.save(this.featureLocation);
        assertNotNull(this.featureLocation.getId(), "unable to create feature location");

        // create variant
        this.variant = VariantTest.createVariantObject(uuid);
        this.variant.setFeatureLocation(this.featureLocation);
        this.variantDAO.save(this.variant);
        assertNotNull(this.variant.getId(), "unable to create variant with feature location");
        assertEquals(this.featureLocation.getId(), this.variant.getFeatureLocation().getId());

        // get the variant by the feature location
        Optional<Variant> storedVariant = this.variantDAO.findByFeatureLocation(this.featureLocation);
        assertTrue(storedVariant.isPresent(), "unable to get variant by feature location");

        verifyVariant(this.variant, storedVariant.get());
        verifyFeatureLocation(this.featureLocation, storedVariant.get().getFeatureLocation());
    }

    @Test
    void testCreateVariantWithEvidences(){
        String uuid = UUID.randomUUID().toString();
        // create evidence
        Evidence e1 = EvidenceTest.createEvidenceObject(uuid);
        Evidence e2 = EvidenceTest.createEvidenceObject(uuid+2);

        // create variant with evidence
        this.variant = VariantTest.createVariantObject(uuid);
        this.variant.addEvidence(e1);
        this.variant.addEvidence(e2);
        this.variantDAO.save(this.variant);
        assertNotNull(this.variant.getId(), "unable to create variant with evidence");
        assertNotNull(e1.getId());
        assertNotNull(e2.getId());

        // get the variant by id
        Optional<Variant> var = this.variantDAO.findById(this.variant.getId());
        assertTrue(var.isPresent());
        Variant gVar = var.get();
        assertEquals(this.variant.getId(), gVar.getId());
        assertEquals(2, gVar.getEvidences().size());
    }

    @Test
    void testCreateVariantWithProtein(){
        String uuid = UUID.randomUUID().toString();
        // create protein
        this.protein = ProteinTest.createProteinObject(uuid);
        this.proteinDAO.save(this.protein);
        assertNotNull(this.protein.getId(), "unable to create protein");

        // create variant with protein
        this.variant = VariantTest.createVariantObject(uuid);
        this.variant.setProtein(this.protein);
        this.variantDAO.save(this.variant);
        assertNotNull(this.variant.getId(), "unable to create variant with protein");
        assertEquals(this.protein.getId(), this.variant.getProtein().getId());

        // get the variant by the protein
        List<Variant> storedVariant = this.variantDAO.findAllByProtein(this.protein);
        assertEquals(1, storedVariant.size(), "unable to get variant by protein");

        verifyVariant(this.variant, storedVariant.get(0));
        verifyProtein(this.protein, storedVariant.get(0).getProtein());
    }

    @Test
    void testCreateVariantWithDisease(){
        String uuid = UUID.randomUUID().toString();
        // create disease
        this.disease = DiseaseTest.createDiseaseObject(uuid);
        this.diseaseDAO.save(this.disease);
        assertNotNull(this.disease.getId(), "unable to create disease");

        // create variant with disease
        this.variant = VariantTest.createVariantObject(uuid);
        this.variant.setDisease(this.disease);
        this.variantDAO.save(this.variant);
        assertNotNull(this.variant.getId(), "unable to create variant with disease");
        assertEquals(this.disease.getId(), this.variant.getDisease().getId());

        // get the variant by the disease
        List<Variant> storedVariant = this.variantDAO.findAllByDisease(this.disease);
        assertEquals(1, storedVariant.size(), "unable to get variant by disease");

        verifyVariant(this.variant, storedVariant.get(0));
        verifyDisease(this.disease, storedVariant.get(0).getDisease());
    }

    @Test
    void testGetVariantsByDiseaseAcronymInIt(){
        String diseaseAcronym = "in DISEASE-123;";
        String uuid = UUID.randomUUID().toString();
        // create a variant with disease acronym and feature id
        Variant var1 = VariantTest.createVariantObject(uuid + 1);
        var1.setReport(var1.getReport() + diseaseAcronym);

        // create a variant without disease acronym but with feature id
        Variant var2 = VariantTest.createVariantObject(uuid + 2);
        var2.setReport(var2.getReport());

        // create a variant with disease acronym but without feature id
        Variant var3 = VariantTest.createVariantObject(uuid + 3);
        var3.setReport(diseaseAcronym + var3.getReport());
        var3.setFeatureId(null);

        // create a variant without disease acronym in it and without feature id
        Variant var4 = VariantTest.createVariantObject(uuid + 4);
        var4.setReport(diseaseAcronym + var4.getReport());
        var4.setFeatureId(null);

        this.variantDAO.saveAll(Arrays.asList(var1, var2, var3, var4));

        // get only one record
        List<Variant> result = this.variantDAO.findAllByReportContainingAndFeatureIdIsNotNull(diseaseAcronym);
        assertEquals(1, result.size());
        verifyVariant(var1, result.get(0));

        // delete all the above variants
        this.variantDAO.deleteById(var1.getId());
        this.variantDAO.deleteById(var2.getId());
        this.variantDAO.deleteById(var3.getId());
        this.variantDAO.deleteById(var4.getId());


    }

    private void verifyDisease(Disease actual, Disease expected) {
        assertEquals(actual.getId(), expected.getId());
        assertEquals(actual.getDiseaseId(), expected.getDiseaseId());
        assertEquals(actual.getName(), expected.getName());
        assertEquals(actual.getDesc(), expected.getDesc());
        assertEquals(actual.getAcronym(), expected.getAcronym());
        assertEquals(actual.getCreatedAt(), expected.getCreatedAt());
        assertEquals(actual.getUpdatedAt(), expected.getUpdatedAt());
    }

    private void verifyFeatureLocation(FeatureLocation actual, FeatureLocation expected) {
        assertEquals(actual.getId(), expected.getId());
        assertEquals(actual.getStartModifier(), expected.getStartModifier());
        assertEquals(actual.getEndModifier(), expected.getEndModifier());
        assertEquals(actual.getStartId(), expected.getStartId());
        assertEquals(actual.getEndId(), expected.getEndId());
        assertEquals(actual.getCreatedAt(), expected.getCreatedAt());
        assertEquals(actual.getUpdatedAt(), expected.getUpdatedAt());
    }

    private void verifyVariant(Variant actual, Variant expected) {
        assertEquals(actual.getId(), expected.getId());
        assertEquals(actual.getReport(), expected.getReport());
        assertEquals(actual.getOrigSeq(), expected.getOrigSeq());
        assertEquals(actual.getAltSeq(), expected.getAltSeq());
        assertEquals(actual.getFeatureId(), expected.getFeatureId());
        assertEquals(actual.getFeatureStatus(), expected.getFeatureStatus());
        assertEquals(actual.getCreatedAt(), expected.getCreatedAt());
        assertEquals(actual.getUpdatedAt(), expected.getUpdatedAt());
    }

    private void verifyProtein(Protein actual, Protein expected) {
        assertEquals(actual.getId(), expected.getId());
        assertEquals(actual.getProteinId(), expected.getProteinId());
        assertEquals(actual.getName(), expected.getName());
        assertEquals(actual.getAccession(), expected.getAccession());
        assertEquals(actual.getDesc(), expected.getDesc());
        assertEquals(actual.getGene(), expected.getGene());
        assertEquals(actual.getCreatedAt(), expected.getCreatedAt());
        assertEquals(actual.getUpdatedAt(), expected.getUpdatedAt());
    }
}
