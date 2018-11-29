package uk.ac.ebi.uniprot.disease.model;

import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import uk.ac.ebi.uniprot.disease.model.disgenet.UniProtGene;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

public class UniProtGeneTest {
    //TODO enable and write test when the JPA is enabled
    /*
    private static EntityManager em;
    private int random = (int) (Math.random()*100000);
    private String uniProtId = "P" + random;
    private int geneId = random;
    private UniProtGene cug = null;

    @After
    public void cleanUp(){
        EntityTransaction et = em.getTransaction();
        et.begin();
        em.remove(this.cug);
        et.commit();
    }

    @Test
    public void testCreateUniProtGene(){
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("disgenet_eclipselink");
        em = emf.createEntityManager();
        // create a uniprot gene
        UniProtGene cug = createUniProtGene(uniProtId, geneId);
        this.cug = cug;
        Assert.assertNotNull(cug);
        // get the entry and verify it
        UniProtGene gug = getUniProtGene(cug.getId());
        Assert.assertNotNull(gug);
        verifyValues(gug, cug);
    }

    private void verifyValues(UniProtGene gug, UniProtGene cug) {
        Assert.assertEquals("Ids do not match", cug.getId(), gug.getId());
        Assert.assertEquals("UniProtIds do not match", cug.getUniProtId(), gug.getUniProtId());
        Assert.assertEquals("GeneIds do not match", cug.getGeneId(), gug.getGeneId());
    }

    private UniProtGene getUniProtGene(int id) {
        return em.find(UniProtGene.class, id);
    }

    private UniProtGene createUniProtGene(String uniProtId, int geneId){
        EntityTransaction et = em.getTransaction();
        et.begin();
        UniProtGene ug = new UniProtGene();
        ug.setUniProtId(uniProtId);
        ug.setGeneId(geneId);
        em.persist(ug);
        et.commit();
        return ug;
    }*/
}
