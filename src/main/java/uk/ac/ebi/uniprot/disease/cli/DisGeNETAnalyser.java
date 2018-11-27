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
    private static Set<String> sources = new HashSet<>();
    public static void main(String[] args) throws FileNotFoundException {
        Map<String, List<String>> geneUniProtMap = getMapping("src/main/resources/mapa_geneid_4_uniprot_crossref.tsv");
        // find and print the mapping for gene Id to the uniprot if any
        printGeneIdToUniProtMapping("/Users/sahmad/Downloads/DisGeNET/befree.tsv", geneUniProtMap, "bda.csv");
        //printGeneIdToUniProtMapping("/Users/sahmad/Downloads/DisGeNET/curated_gene_disease_associations.tsv",
          //     geneUniProtMap, "gda.csv");
        //printGeneIdToUniProtMapping("/Users/sahmad/Downloads/DisGeNET/all_gene_disease_associations.tsv",
                //geneUniProtMap, "all.csv");

        System.out.println(sources);

        // get curated/automated uniprotId and disease count map
        //Map<String, Integer> curatedUPIdCount = getUPIdCountMap("/Users/sahmad/Downloads/DisGeNET/curated_gene_disease_associations.tsv", geneUniProtMap);
        //Map<String, Integer> autoUPIdCount = getUPIdCountMap("/Users/sahmad/Downloads/DisGeNET/befree.tsv", geneUniProtMap);
        //Set<String> onlyCuratedIds = getOnlyCuratedIds(curatedUPIdCount, autoUPIdCount);
        //Set<String> onlyAutoIds = getOnlyCuratedIds(autoUPIdCount, curatedUPIdCount);
        //Set<String> commonUPIds = getCommonUPIds(autoUPIdCount, curatedUPIdCount);
        //Set<String> uniqueUPIds = new HashSet<>();
        //uniqueUPIds.addAll(curatedUPIdCount.keySet());
        //uniqueUPIds.addAll(autoUPIdCount.keySet());
        System.out.println();

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
            sources.addAll(Arrays.asList(gda.getSource().split(";")));
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
