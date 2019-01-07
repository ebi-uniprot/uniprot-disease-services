/*
 * Created by sahmad on 12/21/18 10:14 AM
 * UniProt Consortium.
 * Copyright (c) 2002-2018.
 *
 */

package uk.ac.ebi.uniprot.disease.service;

import org.junit.Assert;
import org.junit.Test;
import uk.ac.ebi.kraken.interfaces.uniprot.DatabaseCrossReference;
import uk.ac.ebi.kraken.interfaces.uniprot.UniProtEntry;
import uk.ac.ebi.kraken.interfaces.uniprot.comments.Interaction;
import uk.ac.ebi.kraken.interfaces.uniprot.features.VariantFeature;
import uk.ac.ebi.uniprot.disease.model.Disease;
import uk.ac.ebi.uniprot.disease.model.Protein;

import java.util.List;
import java.util.Set;

public class ProteinServiceTest extends BaseServiceTest{

    //@Test
    public void testConvertToProtein() {
        // convert the uniprot entry to disease service protein
        Protein protein = proteinService.convertToProtein(BaseServiceTest.uniProtEntry);
        Assert.assertNotNull("Entry cannot be converted to Protein", protein);
        Assert.assertEquals("id is not equal", "1433G_HUMAN", protein.getId());
        Assert.assertEquals("name is not equal", "14-3-3 protein gamma", protein.getName());
        Assert.assertEquals("accession is not equal", "P61981", protein.getAccession());
        Assert.assertEquals("gene name is not equal", "YWHAG", protein.getGene());
        Assert.assertEquals("publication count is equal", Integer.valueOf(30), protein.getPublicationCount());

        List<String> functions = protein.getFunctions();
        Assert.assertFalse("functions list is empty", functions.isEmpty());
        // concat the string
        StringBuilder strBuilder = new StringBuilder();
        functions.forEach(strBuilder::append);
        Assert.assertTrue("Function doesn't contain the sub string", strBuilder.toString().contains("a phosphoserine or phosphothreonine motif"));

        // verify interations
        List<Interaction> interactions = protein.getInteractions();
        Assert.assertFalse("interactions is empty", interactions.isEmpty());
        Assert.assertEquals("interactions count don't match", Integer.valueOf(interactions.size()), protein.getInteractionCount());

        // verify variant
        List<VariantFeature> variants = protein.getVariants();
        Assert.assertFalse("variants is empty", variants.isEmpty());
        Assert.assertEquals("variants size is not equal", Integer.valueOf(variants.size()), protein.getVariantCount());

        // verify pathways
        List<DatabaseCrossReference> pathways = protein.getPathways();
        Assert.assertFalse("pathways is empty", pathways.isEmpty());
        Assert.assertEquals("pathways count don't match", Integer.valueOf(pathways.size()), protein.getPathwayCount());

        // drug count
        Assert.assertNull("Drug count is not null", protein.getDrugCount());

        // verify disease
        Set<Disease> diseases = protein.getDiseases();
        Assert.assertNotNull("diseases is null", diseases);
        Assert.assertFalse("diseases is empty", diseases.isEmpty());
        Assert.assertEquals("disease count do not match", Integer.valueOf(diseases.size()), protein.getDiseaseCount());

    }

    //@Test
    public void testCreateProtein(){
        proteinService.createProtein(BaseServiceTest.uniProtEntry);
        // make DB call to get record from db and very
    }
}
