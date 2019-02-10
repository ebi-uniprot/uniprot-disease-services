/*
 * Created by sahmad on 07/02/19 15:13
 * UniProt Consortium.
 * Copyright (c) 2002-2019.
 *
 */

package uk.ac.ebi.uniprot.ds.rest.utils;

import uk.ac.ebi.uniprot.ds.common.model.*;

import java.util.Random;

public class ModelCreationUtils {
    public static Disease createDiseaseObject(String random) {
        Disease disease = new Disease();
        String dId = "DID-" + random;
        String dn = "DN-" + random;
        String desc = "DESC-" + random;
        String acr = "ACRONYM-" + random;
        disease.setDiseaseId(dId);
        disease.setName(dn);
        disease.setDesc(desc);
        disease.setAcronym(acr);
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


    public  static Pathway createPathwayObject(String uuid) {
        Pathway pathway = new Pathway();
        String pId = "PID-" + uuid;
        String desc = "DESC-" + uuid;
        String type = "TYPE-" + uuid;
        String iid = "IID-" + uuid;
        String t = "T-" + uuid;
        String f = "F-" + uuid;
        pathway.setPrimaryId(pId);
        pathway.setDesc(desc);
        pathway.setDbType(type);
        pathway.setIsoformId(iid);
        pathway.setThird(t);
        pathway.setFourth(f);
        return pathway;
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
}
