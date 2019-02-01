/*
 * Created by sahmad on 1/24/19 8:41 PM
 * UniProt Consortium.
 * Copyright (c) 2002-2019.
 *
 */

package uk.ac.ebi.uniprot.ds.dao;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.ac.ebi.uniprot.ds.model.*;

import java.util.*;
import java.util.stream.IntStream;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class ProteinDAOImplTest{
    @Autowired
    private ProteinDAO proteinDAO;

    @Autowired
    private DiseaseDAO diseaseDAO;

    private Protein protein;
    private Set<Disease> diseases;
    private String randomUUID = UUID.randomUUID().toString();

    @AfterEach
    public void cleanUp(){
        if(this.protein != null){
            this.proteinDAO.delete(this.protein);
            this.protein = null;
        }

        if(this.diseases != null && !this.diseases.isEmpty()){
            this.diseases.forEach(disease -> this.diseaseDAO.delete(disease));
            this.diseases = null;
        }
    }



    @Test
    void createProteinWithPathwayInteractionVariant(){
        String uuid = UUID.randomUUID().toString();
        this.protein = ProteinTest.createProteinObject(uuid);
        // 3 interactions
        Interaction int1 = InteractionTest.createInteractionObject(UUID.randomUUID().toString());
        Interaction int2 = InteractionTest.createInteractionObject(UUID.randomUUID().toString());
        Interaction int3 = InteractionTest.createInteractionObject(UUID.randomUUID().toString());
        int1.setProtein(this.protein);
        int2.setProtein(this.protein);
        int3.setProtein(this.protein);
        List<Interaction> ints = new ArrayList<>();
        ints.add(int1);ints.add(int2);ints.add(int3);
        this.protein.addInteractions(ints);

        // 3 pathways
        Pathway p1 = PathwayTest.createPathwayObject(UUID.randomUUID().toString());
        Pathway p2 = PathwayTest.createPathwayObject(UUID.randomUUID().toString());
        Pathway p3 = PathwayTest.createPathwayObject(UUID.randomUUID().toString());
        p1.setProtein(this.protein);
        p2.setProtein(this.protein);
        p3.setProtein(this.protein);
        List<Pathway> ps = new ArrayList<>();
        ps.add(p1);ps.add(p2);ps.add(p3);
        this.protein.addPathways(ps);

        // 3 variants
        Variant v1 = VariantTest.createVariantObject(UUID.randomUUID().toString());
        FeatureLocation f1 = FeatureLocationTest.createFeatureLocationObject(UUID.randomUUID().toString());
        v1.setFeatureLocation(f1);

        Variant v2 = VariantTest.createVariantObject(UUID.randomUUID().toString());
        FeatureLocation f2 = FeatureLocationTest.createFeatureLocationObject(UUID.randomUUID().toString());
        v2.setFeatureLocation(f2);

        Variant v3 = VariantTest.createVariantObject(UUID.randomUUID().toString());
        FeatureLocation f3 = FeatureLocationTest.createFeatureLocationObject(UUID.randomUUID().toString());
        v3.setFeatureLocation(f3);

        List<Variant> vars = new ArrayList<>();
        v1.setProtein(this.protein);
        v2.setProtein(this.protein);
        v3.setProtein(this.protein);
        vars.add(v1);vars.add(v2);vars.add(v3);
        this.protein.addVariants(vars);


        //save now
        this.proteinDAO.save(this.protein);
    }

    @Test
    void createProteinWithDiseases(){
        this.protein = ProteinTest.createProteinObject(UUID.randomUUID().toString());
        // create 5 diseases
        this.diseases = new HashSet<>();
        IntStream.range(1, 6).forEach(i -> this.diseases.add(createDisease(new Random().nextInt())));

        this.protein.setDiseases(this.diseases);
        this.proteinDAO.save(this.protein);
        assertNotNull(this.protein.getId(), "unable to create protein");

        // get the disease
        Optional<Protein> storedProtein = this.proteinDAO.findById(this.protein.getId());
        assertTrue(storedProtein.isPresent(), "unable to get the protein");
        verifyProtein(this.protein, storedProtein.get());
        verifyDiseases(storedProtein.get().getDiseases());
    }

    @Test
    void testGetProteinById(){
        this.protein = ProteinTest.createProteinObject(UUID.randomUUID().toString());
        this.protein = this.proteinDAO.save(this.protein);
        assertNotNull(this.protein.getId());

        Optional<Protein> savedProtein = this.proteinDAO.findByProteinId(this.protein.getProteinId());
        assertTrue(savedProtein.isPresent());
        verifyProtein(this.protein, savedProtein.get());
    }

    @Test
    void testGetProteinByAccession(){
        this.protein = ProteinTest.createProteinObject(UUID.randomUUID().toString());
        this.protein = this.proteinDAO.save(this.protein);
        assertNotNull(this.protein.getId());

        Optional<Protein> savedProtein = this.proteinDAO.findByAccession(this.protein.getAccession());
        assertTrue(savedProtein.isPresent());
        verifyProtein(this.protein, savedProtein.get());
    }

    @Test
    void testGetNonExistentProteinById(){
        String randomPID = "pid-" + this.randomUUID;
        Optional<Protein> savedProtein = this.proteinDAO.findByProteinId(randomPID);
        assertFalse(savedProtein.isPresent());
    }

    @Test
    void testGetNonExistentProteinByAccession(){
        String randomAcc = "Acc-" + this.randomUUID;
        Optional<Protein> savedProtein = this.proteinDAO.findByAccession(randomAcc);
        assertFalse(savedProtein.isPresent());
    }


    private void verifyDiseases(Set<Disease> diseases) {
        assertEquals(5, diseases.size());
        diseases.forEach(disease -> assertNotNull(disease.getId()));
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

    private Disease createDisease(int nextInt) {
        Disease dis = DiseaseTest.createDiseaseObject(String.valueOf(nextInt));
        this.diseaseDAO.save(dis);
        return dis;
    }
}
