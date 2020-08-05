package uk.ac.ebi.uniprot.ds.graphql;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.web.client.RestTemplate;
import uk.ac.ebi.uniprot.ds.common.dao.DiseaseDAO;
import uk.ac.ebi.uniprot.ds.common.dao.ProteinDAO;
import uk.ac.ebi.uniprot.ds.common.model.*;
import uk.ac.ebi.uniprot.ds.graphql.model.DataServiceProtein;
import uk.ac.ebi.uniprot.ds.graphql.model.VariantSourceTypeEnum;
import uk.ac.ebi.uniprot.ds.graphql.model.Variation;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class BaseGraphQueryTest {
    @MockBean
    protected RestTemplate restTemplate;
    @MockBean
    protected DiseaseDAO diseaseDAO;
    @MockBean
    protected ProteinDAO proteinDAO;
    @Autowired
    protected ModelMapper modelMapper;

    public static Protein createProtein(String accession) {
        // create protein
        Protein protein = new Protein();
        String pId = "PID-" + accession;
        String pn = "PN-" + accession;
        String acc = accession;
        String gene = "GENE-" + accession;
        String pDesc = "PDESC-" + accession;
        boolean isExternallyMapped = false;

        protein.setProteinId(pId);
        protein.setName(pn);
        protein.setAccession(acc);
        protein.setGene(gene);
        protein.setDesc(pDesc);
        protein.setIsExternallyMapped(isExternallyMapped);
        return protein;
    }

    protected Disease createDiseaseObject(String diseaseId) {
        Disease disease = new Disease();
        String dId = diseaseId;
        String dn = "Disease Name" + diseaseId;
        String desc = "Description" + diseaseId;
        String acr = "ACRONYM-" + diseaseId;
        disease.setDiseaseId(dId);
        disease.setName(dn);
        disease.setDesc(desc);
        disease.setAcronym(acr);
        disease.setNote("Note" + diseaseId);
        // synonym
        disease.addSynonym(createSynonym(diseaseId, "1"));
        disease.addSynonym(createSynonym(diseaseId, "2"));
        disease.addSynonym(createSynonym(diseaseId, "3"));
        // publications
        disease.setPublications(Arrays.asList(createPublication("1"), createPublication("2")));
        // variant
        return disease;
    }

    protected DataServiceProtein createDataServiceProtein(){
        DataServiceProtein.DataServiceProteinBuilder builder = DataServiceProtein.builder();
        builder.accession("P1234").proteinName("randomProt");
        Variation var1 = createVariation("1");
        Variation var2 = createVariation("2");
        Variation var3 = createVariation("3");
        Variation var4 = createVariation("4");
        Variation var5 = createVariation("5");
        builder.features(Arrays.asList(var1, var2, var3, var4, var5));
        return builder.build();
    }

    protected Variation createVariation(String suffix){
        Variation.VariationBuilder builder = Variation.builder();
        builder.type("VARIANT").cvId("cvId" + suffix).ftId("VAR_1234" + suffix).description("sample description" + suffix);
        builder.alternativeSequence("M" + suffix).sourceType(VariantSourceTypeEnum.large_scale_study);
        return builder.build();
    }

    protected Synonym createSynonym(String diseaseId, String suffix){
        Synonym synonym = new Synonym();
        synonym.setName(diseaseId + suffix);
        synonym.setSource("source" + suffix);
        return synonym;
    }

    protected Publication createPublication(String suffix){
        Publication pub = new Publication();
        pub.setPubId("pubId" + suffix);
        pub.setPubType("pubType" + suffix);
        return pub;
    }

    protected GeneCoordinate createGeneCoordinate(String uuid){
        String chrom = String.valueOf(new Random().nextInt(1000));
        Integer start = new Random().nextInt();
        Integer end = new Random().nextInt();
        String eng = "ENG-" + uuid;
        String ent = "ENT-" + uuid;
        String enp = "ENP-" + uuid;
        GeneCoordinate.GeneCoordinateBuilder bl = GeneCoordinate.builder();
        bl.chromosomeNumber(chrom).startPos(Long.valueOf(start)).endPos(Long.valueOf(end));
        bl.enGeneId(eng).enTranscriptId(ent).enTranslationId(enp);
        return bl.build();
    }

    protected ProteinCrossRef createProteinCrossRef(String uuid) {
        ProteinCrossRef crossRef = new ProteinCrossRef();
        String pId = "PID-" + uuid;
        String desc = "DESC-" + uuid;
        String type = "TYPE-" + uuid;
        String iid = "IID-" + uuid;
        String t = "T-" + uuid;
        String f = "F-" + uuid;
        crossRef.setPrimaryId(pId);
        crossRef.setDescription(desc);
        crossRef.setDbType(type);
        crossRef.setIsoformId(iid);
        crossRef.setThird(t);
        crossRef.setFourth(f);
        return crossRef;
    }

    protected Drug createDrugObject(String rand){
        Drug.DrugBuilder bl = Drug.builder();
        String name = "Name-" + rand;
        String sourceType = "type-" + rand;
        String sourceid = "id-" + rand;
        String moleculeType = "mol-" + rand;
        Integer clinicalTrialPhase = 2;
        String moa = "This is sample moa";
        String trialLink = "sample clinical trial linkg";
        DrugEvidence ev1 = new DrugEvidence("ref_type1", "sample url1", null);
        DrugEvidence ev2 = new DrugEvidence("ref_type1", "sample url2", null);
        List<DrugEvidence> evidences = Arrays.asList(ev1, ev2);
        bl.name(name).sourceType(sourceType).sourceId(sourceid);
        bl.moleculeType(moleculeType);
        bl.clinicalTrialPhase(clinicalTrialPhase).clinicalTrialLink(trialLink).moleculeType(moa);
        bl.drugEvidences(evidences);
        return bl.build();
    }

    protected Interaction createInteraction(String suffix) {
        Interaction inter = new Interaction();
        String type = "TYPE-" + suffix;
        String gene = "G-" + suffix;
        int count = new Random().nextInt();
        String first = "F-" + suffix;
        String second = "S-" + suffix;
        inter.setType(type);
        inter.setGene(gene);
        inter.setAccession("ACC-" + suffix);
        inter.setExperimentCount(count);
        inter.setFirstInteractor(first);
        inter.setSecondInteractor(second);
        return inter;
    }

}
