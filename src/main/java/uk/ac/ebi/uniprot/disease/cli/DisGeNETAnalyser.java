package uk.ac.ebi.uniprot.disease.cli;

import uk.ac.ebi.uniprot.disease.model.disgenet.GeneDiseaseAssociation;
import uk.ac.ebi.uniprot.disease.service.tsv.GeneDiseaseParser;
import uk.ac.ebi.uniprot.disease.service.tsv.TSVReader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.*;
import java.util.stream.Collectors;

public class DisGeNETAnalyser {
    // get unique disease ids from all gda and all gd pmid association file
    // get unique disease ids from all vda and all vd pmid association file
    public static void main(String[] args) throws FileNotFoundException {
        Set<String> dIds = new HashSet<>();
        Set<String> pmidDIds = new HashSet<>();
        TSVReader r1 = new TSVReader("/Users/sahmad/Downloads/DisGeNET/all_variant_disease_association.tsv");
        // parse disease association WITHOUT pmid
        while(r1.hasMoreRecord()){
            List<String> record = r1.getRecord();
            dIds.add(record.get(2));
        }
        TSVReader r2 = new TSVReader("/Users/sahmad/Downloads/DisGeNET/all_variant_disease_pmid_associations.tsv");
        //// parse disease association with pmid
        int count = 0;
        while(r2.hasMoreRecord()){
            List<String> record = r2.getRecord();
            pmidDIds.add(record.get(1));
            System.out.println("Record id" + count);
            count++;
        }
        System.out.println("Total unique disease ids in disease association file:" + dIds.size());
        System.out.println("Total unique disease ids in disease association with pmid file:" + pmidDIds.size());

    }
    public static void main2(String[] args) throws FileNotFoundException {
        Map<String, List<String>> geneUniProtMap = getMapping("src/main/resources/mapa_geneid_4_uniprot_crossref.tsv");
        // find and print the mapping for gene Id to the uniprot if any
       // printGeneIdToUniProtMapping("/Users/sahmad/Downloads/DisGeNET/befree.tsv", geneUniProtMap, "bda.csv");
        //printGeneIdToUniProtMapping("/Users/sahmad/Downloads/DisGeNET/curated_gene_disease_associations.tsv",
          //     geneUniProtMap, "gda.csv");
        //printGeneIdToUniProtMapping("/Users/sahmad/Downloads/DisGeNET/all_gene_disease_associations.tsv",
                //geneUniProtMap, "all.csv");


        // get curated/automated uniprotId and disease count map
        Map<String, Integer> curatedUPIdCount = getUPIdCountMap("/Users/sahmad/Downloads/DisGeNET/curated_gene_disease_associations.tsv", geneUniProtMap);
        Map<String, Integer> autoUPIdCount = getUPIdCountMap("/Users/sahmad/Downloads/DisGeNET/befree.tsv", geneUniProtMap);
        List<Integer> curatedVals = new ArrayList<>(curatedUPIdCount.values());
        Collections.sort(curatedVals);
        System.out.println("Curated Max : " + curatedVals.get(curatedVals.size()-1));
        System.out.println("Curated Median:" + getMedian(curatedVals));

        List<Integer> autoVals = new ArrayList<>(autoUPIdCount.values());
        Collections.sort(autoVals);
        System.out.println("Auto Max : " + autoVals.get(autoVals.size()-1));
        System.out.println("Auto Median:" + getMedian(autoVals));
        //Set<String> onlyCuratedIds = getOnlyCuratedIds(curatedUPIdCount, autoUPIdCount);
        //Set<String> onlyAutoIds = getOnlyCuratedIds(autoUPIdCount, curatedUPIdCount);
        //Set<String> commonUPIds = getCommonUPIds(autoUPIdCount, curatedUPIdCount);
        //Set<String> uniqueUPIds = new HashSet<>();
        //uniqueUPIds.addAll(curatedUPIdCount.keySet());
        //uniqueUPIds.addAll(autoUPIdCount.keySet());
        System.out.println();

    }

    private static Float getMedian(List<Integer> curatedVals) {
        int size = curatedVals.size();
        if(size % 2 == 0){
            return (curatedVals.get(size/2) + curatedVals.get(size/2-1))/2.0F;
        } else {
            return Float.valueOf(curatedVals.get(size/2));
        }
    }

    private static Set<String> getCommonUPIds(Map<String, Integer> autoUPIdCount, Map<String, Integer> curatedUPIdCount) {
        Set<String> common = new HashSet<>();
        for(Map.Entry<String, Integer> entry : curatedUPIdCount.entrySet()){
            if(autoUPIdCount.containsKey(entry.getKey())){
                common.add(entry.getKey());
            }

        }

        return common;
    }

    private static Set<String> getOnlyCuratedIds(Map<String, Integer> curatedUPIdCount,
                                                 Map<String, Integer> autoUPIdCount) {
        Set<String> onlyCurated = new HashSet<>();
        for(Map.Entry<String, Integer> entry : curatedUPIdCount.entrySet()){
            if(!autoUPIdCount.containsKey(entry.getKey())){
                onlyCurated.add(entry.getKey());
            }

        }

        return onlyCurated;
    }

    private static Map<String, Integer> getUPIdCountMap(String fileName, Map<String, List<String>> geneUniProtMap) throws FileNotFoundException {

        // read the mapping file and create a map
        TSVReader reader = new TSVReader(fileName);

        Map<String, Integer> upIdCountMap = new HashMap<>();
        while(reader.hasMoreRecord()){
            List<String> record = reader.getRecord();
            List<String> upIds = geneUniProtMap.get(record.get(0));
            if(upIds != null && !upIds.isEmpty()){
                for(String upId : upIds){
                    if(upIdCountMap.containsKey(upId)){
                        upIdCountMap.put(upId, upIdCountMap.get(upId) + 1);
                    } else {
                        upIdCountMap.put(upId, 1);
                    }

                }
            }
        }

        return upIdCountMap;
    }


    private static void printGeneIdToUniProtMapping(String fileName, Map<String, List<String>> map, String output) throws FileNotFoundException {
        TSVReader reader = new TSVReader(fileName);
        GeneDiseaseParser parser = new GeneDiseaseParser(reader);
        long count = 0L;
        List<GeneDiseaseAssociation> gdas;

        PrintWriter pw = new PrintWriter(new File(output));
        StringBuilder sb = new StringBuilder();
        sb.append("GeneId");
        sb.append(',');
        sb.append("DiseaseId");
        sb.append(',');
        sb.append("UniProtId");
        sb.append('\n');
        do {
            gdas = parser.parseRecords(200);
            sb.append(getCSV(gdas, map));
            count += gdas.size();

        }while(!gdas.isEmpty());

        pw.write(sb.toString());
        pw.close();
        System.out.println("Total processed " + count);
    }

    private static String getCSV(List<GeneDiseaseAssociation> gdas, Map<String, List<String>> map) throws FileNotFoundException {

        StringBuilder sb = new StringBuilder();

        for(GeneDiseaseAssociation gda : gdas){
            sb.append(gda.getGeneId());
            sb.append(',');
            sb.append(gda.getDiseaseId());
            sb.append(',');
            sb.append(convertToString(map.getOrDefault(gda.getGeneId(), Arrays.asList("NA"))));
            sb.append('\n');
        }
        return sb.toString();
    }

    private static String convertToString(List<String> list) {
        String s = list.stream().map(Object::toString).collect(Collectors.joining(";"));
        return s;

    }

    // create of map with GeneID --> UniProtID
    private static Map<String, List<String>> getMapping(String fileName) throws FileNotFoundException {
        // read the mapping file and create a map
        TSVReader reader = new TSVReader(fileName);

        Map<String, List<String>> geneUniProtMap = new HashMap<>();

        while(reader.hasMoreRecord()){
            List<String> record = reader.getRecord();
            if(geneUniProtMap.containsKey(record.get(1))){
                geneUniProtMap.get(record.get(1)).add(record.get(0));
            } else {
                List<String> list = new ArrayList<>();
                list.add(record.get(0));
                geneUniProtMap.put(record.get(1), list);
            }

        }
        reader.close();
        return geneUniProtMap;
    }
}
