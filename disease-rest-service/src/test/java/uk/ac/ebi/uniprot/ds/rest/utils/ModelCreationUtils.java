/*
 * Created by sahmad on 07/02/19 15:13
 * UniProt Consortium.
 * Copyright (c) 2002-2019.
 *
 */

package uk.ac.ebi.uniprot.ds.rest.utils;

import uk.ac.ebi.uniprot.ds.common.model.*;
import uk.ac.ebi.uniprot.ds.rest.dto.FeatureType;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class ModelCreationUtils {
    public static Disease createDiseaseObject(String random) {
        Disease disease = new Disease();
        String dId = "DI-" + random;
        String dn = "DN-" + random;
        String desc = "DESC-" + random;
        String acr = "ACRONYM-" + random;
        disease.setDiseaseId(dId);
        disease.setName(dn);
        disease.setDesc(desc);
        disease.setAcronym(acr);
        disease.setNote("Note-" + random);
        return disease;
    }

    public static Evidence createEvidenceObject(String uuid){
        String eId = "EID-" + uuid;
        String eType = "TYPE-" + uuid;
        String eCode = "ECODE-" + uuid;
        Boolean useCode = new Random().nextInt() % 2 == 0 ? true : false;
        String tVal = "TVAL-" + uuid;
        Boolean hasTVal = new Random().nextInt() % 2 == 0 ? true : false;
        String attrib = "ATTRIB-" + uuid;

        Evidence eObj = new Evidence();
        eObj.setEvidenceId(eId);
        eObj.setType(eType);
        eObj.setCode(eCode);
        eObj.setUseECOCode(useCode);
        eObj.setTypeValue(tVal);
        eObj.setHasTypeValue(hasTVal);
        eObj.setAttribute(attrib);

        return eObj;
    }

    public static FeatureLocation createFeatureLocationObject(String uuid) {
        String sm = "SM-" + uuid;
        String em = "EM-" + uuid;
        int si = new Random().nextInt();
        int ei = si + 5;
        FeatureLocation fl = new FeatureLocation();
        fl.setStartModifier(sm);
        fl.setEndModifier(em);
        fl.setStartId(si);
        fl.setEndId(ei);
        return fl;
    }

    public static Interaction createInteractionObject(String random) {
        Interaction inter = new Interaction();
        String type = "TYPE-" + random;
        String gene = "G-" + random;
        int count = new Random().nextInt();
        String first = "F-" + random;
        String second = "S-" + random;
        inter.setType(type);
        inter.setGene(gene);
        inter.setAccession("ACC-" + random);
        inter.setExperimentCount(count);
        inter.setFirstInteractor(first);
        inter.setSecondInteractor(second);
        return inter;
    }


    public  static ProteinCrossRef createProteinXRefObject(String uuid) {
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

    public static Protein createProteinObject(String random) {

        // create protein
        Protein protein = new Protein();
        String pId = "PID-" + random;
        String pn = "PN-" + random;
        String acc = "ACC-" + random;
        String gene = "GENE-" + random;
        String pDesc = "PDESC-" + random;

        protein.setProteinId(pId);
        protein.setName(pn);
        protein.setAccession(acc);
        protein.setGene(gene);
        protein.setDesc(pDesc);
        return protein;
    }
    public static Synonym createSynonymObject(String uuid){
        Synonym synonym = new Synonym();
        synonym.setName("Name-" + uuid);
        return synonym;
    }

    public static Variant createVariantObject(String uuid){
        String os = "OS-" + uuid;
        String as = "AS-" + uuid;
        String fid = "FID-" + uuid;
        String fs = "FS-" + uuid;
        String vr = "VR-" + uuid;

        Variant variant = new Variant();
        variant.setOrigSeq(os);
        variant.setAltSeq(as);
        variant.setFeatureId(fid);
        variant.setFeatureStatus(fs);
        variant.setReport(vr);
        return variant;
    }

    public static GeneCoordinate createGeneCoordinateObject(String uuid){
        String chrom = String.valueOf(new Random().nextInt(1000));
        Long start = new Random().nextLong();
        Long end = new Random().nextLong();
        String eng = "ENG-" + uuid;
        String ent = "ENT-" + uuid;
        String enp = "ENP-" + uuid;
        GeneCoordinate.GeneCoordinateBuilder bl = GeneCoordinate.builder();
        bl.chromosomeNumber(chrom).startPos(start).endPos(end);
        bl.enGeneId(eng).enTranscriptId(ent).enTranslationId(enp);
        return bl.build();
    }

    public static Publication createPublicationObject(String rand){
        Publication.PublicationBuilder bldr = Publication.builder();
        bldr.pubType("type-" + rand);
        bldr.pubId("id-" + rand);
        return bldr.build();
    }

    public static Drug createDrugObject(String rand){
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

    public static SiteMapping createSiteMappingObject(String uuid) {
        SiteMapping.SiteMappingBuilder builder = SiteMapping.builder();
        builder.accession("accession-" + uuid);
        builder.proteinId("pid-" + uuid);
        long l1 = ThreadLocalRandom.current().nextLong(1, 10000);
        long l2 = ThreadLocalRandom.current().nextLong(1, 10000);
        builder.sitePosition(l1);
        builder.positionInAlignment(l2);
        String ft = "rs397507523;Natural variant;Mutagenesis";
        builder.siteType(ft);
        builder.unirefId("uid-" + uuid);
        String mappedSites = "P35235-1|PTN11_MOUSE:507*;P35235|PTN11_MOUSE:503*;P41499-1|PTN11_RAT:507*;Q06124-1|PTN11_HUMAN:507;P41499|PTN11_RAT:503*";
        builder.mappedSite(mappedSites);
        return builder.build();
    }
}
