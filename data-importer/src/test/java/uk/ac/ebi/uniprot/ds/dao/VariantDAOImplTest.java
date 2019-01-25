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
import uk.ac.ebi.uniprot.ds.dao.impl.*;
import uk.ac.ebi.uniprot.ds.model.*;

import java.util.Optional;
import java.util.UUID;

public class VariantDAOImplTest extends BaseTest {
    private VariantDAO variantDAO = new VariantDAOImpl(BaseTest.em);
    private FeatureLocationDAOImpl flDAO = new FeatureLocationDAOImpl(BaseTest.em);
    private EvidenceDAOImpl evidenceDAO = new EvidenceDAOImpl(BaseTest.em);
    private ProteinDAO proteinDAO = new ProteinDAOImpl(BaseTest.em);
    private DiseaseDAO diseaseDAO = new DiseaseDAOImpl(BaseTest.em);

    private Variant variant;
    private FeatureLocation featureLocation;
    private Evidence evidence;
    private Protein protein;
    private Disease disease;

    @AfterEach
    void cleanUp(){
        if(this.variant != null){
            executeInsideTransaction(dao -> dao.delete(this.variant), this.variantDAO);
            this.variant = null;
        }

        if(this.featureLocation != null){
            executeInsideTransaction(dao -> dao.delete(this.featureLocation), this.flDAO);
            this.featureLocation = null;
        }

        if(this.evidence != null){
            executeInsideTransaction(dao -> dao.delete(this.evidence), this.evidenceDAO);
            this.evidence = null;
        }

        if(this.protein != null){
            executeInsideTransaction(dao -> dao.delete(this.protein), this.proteinDAO);
            this.protein = null;
        }

        if(this.disease != null){
            executeInsideTransaction(dao -> dao.delete(this.disease), this.diseaseDAO);
            this.disease = null;
        }
    }

    @Test
    void testCreateVariant(){
        this.variant = VariantTest.createVariantObject(UUID.randomUUID().toString());
        executeInsideTransaction(dao -> dao.createOrUpdate(this.variant), this.variantDAO);
        assertNotNull(this.variant.getId(), "unable to create variant");
    }

    @Test
    void testCreateVariantWithFeatureLocation(){
        String uuid = UUID.randomUUID().toString();
        // create Feature Location
        this.featureLocation = FeatureLocationTest.createFeatureLocationObject(uuid);
        executeInsideTransaction(dao -> dao.createOrUpdate(this.featureLocation), this.flDAO);
        assertNotNull(this.featureLocation.getId(), "unable to create feature location");

        // create variant
        this.variant = VariantTest.createVariantObject(uuid);
        this.variant.setFeatureLocation(this.featureLocation);
        executeInsideTransaction(dao -> dao.createOrUpdate(this.variant), this.variantDAO);
        assertNotNull(this.variant.getId(), "unable to create variant with feature location");
        assertEquals(this.featureLocation.getId(), this.variant.getFeatureLocation().getId());

        // get the variant by the feature location
        Optional<Variant> storedVariant = this.variantDAO.getVariantByFeatureLocation(this.featureLocation);
        assertTrue(storedVariant.isPresent(), "unable to get variant by feature location");

        verifyVariant(this.variant, storedVariant.get());
        verifyFeatureLocation(this.featureLocation, storedVariant.get().getFeatureLocation());
    }

    @Test
    void testCreateVariantWithEvidence(){
        String uuid = UUID.randomUUID().toString();
        // create evidence
        this.evidence = EvidenceTest.createEvidenceObject(uuid);
        executeInsideTransaction(dao -> dao.createOrUpdate(this.evidence), this.evidenceDAO);
        assertNotNull(this.evidence.getId(), "unable to create evidence");

        // create variant with evidence
        this.variant = VariantTest.createVariantObject(uuid);
        this.variant.setEvidence(this.evidence);
        executeInsideTransaction(dao -> dao.createOrUpdate(this.variant), this.variantDAO);
        assertNotNull(this.variant.getId(), "unable to create variant with evidence");
        assertEquals(this.evidence.getId(), this.variant.getEvidence().getId());

        // get the variant by the evidence
        Optional<Variant> storedVariant = this.variantDAO.getVariantByEvidence(this.evidence);
        assertTrue(storedVariant.isPresent(), "unable to get variant by evidence");

        verifyVariant(this.variant, storedVariant.get());
        verifyEvidence(this.evidence, storedVariant.get().getEvidence());
    }

    @Test
    void testCreateVariantWithProtein(){
        String uuid = UUID.randomUUID().toString();
        // create protein
        this.protein = ProteinTest.createProteinObject(uuid);
        executeInsideTransaction(dao -> dao.createOrUpdate(this.protein), this.proteinDAO);
        assertNotNull(this.protein.getId(), "unable to create protein");

        // create variant with protein
        this.variant = VariantTest.createVariantObject(uuid);
        this.variant.setProtein(this.protein);
        executeInsideTransaction(dao -> dao.createOrUpdate(this.variant), this.variantDAO);
        assertNotNull(this.variant.getId(), "unable to create variant with protein");
        assertEquals(this.protein.getId(), this.variant.getProtein().getId());

        // get the variant by the protein
        Optional<Variant> storedVariant = this.variantDAO.getVariantByProtein(this.protein);
        assertTrue(storedVariant.isPresent(), "unable to get variant by protein");

        verifyVariant(this.variant, storedVariant.get());
        verifyProtein(this.protein, storedVariant.get().getProtein());
    }

    @Test
    void testCreateVariantWithDisease(){
        String uuid = UUID.randomUUID().toString();
        // create disease
        this.disease = DiseaseTest.createDiseaseObject(uuid);
        executeInsideTransaction(dao -> dao.createOrUpdate(this.disease), this.diseaseDAO);
        assertNotNull(this.disease.getId(), "unable to create disease");

        // create variant with disease
        this.variant = VariantTest.createVariantObject(uuid);
        this.variant.setDisease(this.disease);
        executeInsideTransaction(dao -> dao.createOrUpdate(this.variant), this.variantDAO);
        assertNotNull(this.variant.getId(), "unable to create variant with disease");
        assertEquals(this.disease.getId(), this.variant.getDisease().getId());

        // get the variant by the disease
        Optional<Variant> storedVariant = this.variantDAO.getVariantByDisease(this.disease);
        assertTrue(storedVariant.isPresent(), "unable to get variant by disease");

        verifyVariant(this.variant, storedVariant.get());
        verifyDisease(this.disease, storedVariant.get().getDisease());
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

    private void verifyEvidence(Evidence actual, Evidence expected) {
        assertEquals(actual.getId(), expected.getId());
        assertEquals(actual.getEvidenceId(), expected.getEvidenceId());
        assertEquals(actual.getType(), expected.getType());
        assertEquals(actual.getAttribute(), expected.getAttribute());
        assertEquals(actual.getUseECOCode(), expected.getUseECOCode());
        assertEquals(actual.getUseECOCode(), expected.getUseECOCode());
        assertEquals(actual.getTypeValue(), expected.getTypeValue());
        assertEquals(actual.getHasTypeValue(), expected.getHasTypeValue());
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
