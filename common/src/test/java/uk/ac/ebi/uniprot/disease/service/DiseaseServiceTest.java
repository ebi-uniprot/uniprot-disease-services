/*
 * Created by sahmad on 12/21/18 1:39 PM
 * UniProt Consortium.
 * Copyright (c) 2002-2018.
 *
 */

package uk.ac.ebi.uniprot.disease.service;

import org.junit.Assert;
import org.junit.Test;
import uk.ac.ebi.kraken.interfaces.uniprot.DatabaseCrossReference;
import uk.ac.ebi.kraken.interfaces.uniprot.comments.CommentType;
import uk.ac.ebi.kraken.interfaces.uniprot.comments.DiseaseCommentStructured;
import uk.ac.ebi.kraken.interfaces.uniprot.comments.Interaction;
import uk.ac.ebi.kraken.interfaces.uniprot.features.VariantFeature;
import uk.ac.ebi.uniprot.disease.model.Disease;
import uk.ac.ebi.uniprot.disease.model.Protein;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class DiseaseServiceTest extends BaseServiceTest {

    @Test
    public void testGetDiseases(){
        Protein protein = proteinService.convertToProtein(BaseServiceTest.uniProtEntry);
        Assert.assertNotNull("Protein is null", protein);
        Assert.assertNotNull("Accession is null", protein.getAccession());

        List<DiseaseCommentStructured> diseaseComments = BaseServiceTest.uniProtEntry.getComments(CommentType.DISEASE);
        Assert.assertNotNull("Disease comments is null", diseaseComments);
        Assert.assertFalse("Disease comments is empty", diseaseComments.isEmpty());

        Set<Disease> diseases = diseaseService.getDiseases(diseaseComments, protein);
        Assert.assertNotNull("diseases is null", diseases);
        Assert.assertFalse("diseases is empty", diseases.isEmpty());
        List<Disease> diseaseList = new ArrayList<>(diseases);
        diseaseList.parallelStream().forEach(disease -> verifyDisease(disease));

    }

    @Test
    public void testConvertToDisease(){
        Protein protein = proteinService.convertToProtein(BaseServiceTest.uniProtEntry);
        Assert.assertNotNull("Protein is null", protein);
        Assert.assertNotNull("Accession is null", protein.getAccession());

        List<DiseaseCommentStructured> diseaseComments = BaseServiceTest.uniProtEntry.getComments(CommentType.DISEASE);

        Disease disease = diseaseService.convertToDisease(diseaseComments.get(0), protein);
        verifyDisease(disease);

    }

    private void verifyDisease(Disease disease) {
        Assert.assertNotNull("Disease is null", disease);
        Assert.assertTrue("descriptions do not match",  disease.getDescription().contains("a heterogeneous group of severe childhood onset epilepsies characterized"));
        Assert.assertEquals("acronyms do not match", "EIEE56", disease.getAcronym());
        Assert.assertEquals("names do not match", "Epileptic encephalopathy, early infantile, 56", disease.getName());
        Assert.assertNull("id is not null", disease.getId());
        Assert.assertNull("Synonyms is not null", disease.getSynonyms());
        Assert.assertNull("Drug count is not null", disease.getDrugCount());
        // verify proteins
        Set<Protein> proteins = disease.getProteins();
        Assert.assertFalse("Proteins is empty", proteins.isEmpty());
        Assert.assertEquals("protein count doesnt match", Integer.valueOf(proteins.size()), disease.getProteinCount());

        // verify interations
        List<Interaction> interactions = disease.getInteractions();
        Assert.assertFalse("interactions is empty", interactions.isEmpty());
        Assert.assertEquals("interactions count don't match", Integer.valueOf(interactions.size()), disease.getInteractionCount());

        // verify variant
        List<VariantFeature> variants = disease.getVariants();
        Assert.assertFalse("variants is empty", variants.isEmpty());
        Assert.assertEquals("variants size is not equal", Integer.valueOf(variants.size()), disease.getVariantCount());

        // verify pathways
        List<DatabaseCrossReference> pathways = disease.getPathways();
        Assert.assertFalse("pathways is empty", pathways.isEmpty());
        Assert.assertEquals("pathways count don't match", Integer.valueOf(pathways.size()), disease.getPathwayCount());


    }



}
