/*
 * Created by sahmad on 29/01/19 12:00
 * UniProt Consortium.
 * Copyright (c) 2002-2019.
 *
 */

package uk.ac.ebi.uniprot.ds.writer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import uk.ac.ebi.kraken.interfaces.uniprot.Gene;
import uk.ac.ebi.kraken.interfaces.uniprot.ProteinDescription;
import uk.ac.ebi.kraken.interfaces.uniprot.UniProtEntry;
import uk.ac.ebi.kraken.interfaces.uniprot.description.FieldType;
import uk.ac.ebi.kraken.interfaces.uniprot.description.Name;
import uk.ac.ebi.uniprot.ds.model.Protein;
import uk.ac.ebi.uniprot.ds.service.ProteinService;

import java.util.List;
import java.util.Map;

public class ProteinWriter implements ItemWriter<UniProtEntry> {
    private static final Logger logger = LoggerFactory.getLogger(ProteinWriter.class);

    @Autowired
    private ProteinService proteinService;
    private Map<String, Protein> proteinIdProteinMap;

    public ProteinWriter(Map<String, Protein> proteinIdProteinMap) {
        this.proteinIdProteinMap = proteinIdProteinMap;
    }

    @Override
    public void write(List<? extends UniProtEntry> entries) throws Exception {

        entries.forEach(entry -> {
            Protein protein = convertToProtein(entry);
            logger.info("Saving protein {}", protein);
            this.proteinService.createProtein(protein);
            this.proteinIdProteinMap.put(protein.getProteinId(), protein);
        });

        logger.info("The size of map is {}", this.proteinIdProteinMap.size());

    }

    private Protein convertToProtein(UniProtEntry entry) {
        Protein.ProteinBuilder builder = Protein.builder();
        builder.proteinId(entry.getUniProtId().getValue());
        builder.name(getDescription(entry.getProteinDescription()));
        builder.accession(entry.getPrimaryUniProtAccession().getValue());
        builder.gene(getGene(entry.getGenes()));
        return builder.build();
    }

    protected String getDescription(ProteinDescription pd) {
        Name name;

        if (pd.hasRecommendedName()) {
            name = pd.getRecommendedName();
        } else {
            name = pd.getSubNames().get(0);
        }

        return name.getFieldsByType(FieldType.FULL).get(0).getValue();
    }

    protected String getGene(List<Gene> genes) {
        String geneName = null;
        String orfName = null;
        String olnName = null;

        for (Gene gene : genes) {
            if (gene.hasGeneName()) {
                geneName = gene.getGeneName().getValue();
                break;
            } else if (!isListEmpty(gene.getOrderedLocusNames())) {
                olnName = gene.getOrderedLocusNames().get(0).getValue();
            } else if (!isListEmpty(gene.getORFNames())) {
                orfName = gene.getORFNames().get(0).getValue();
            }
        }

        return (geneName != null) ? geneName : ((olnName != null) ? olnName : orfName);
    }
    // probably move it to a util class
    protected boolean isListEmpty(List<?> list) {
        return list == null || list.isEmpty();
    }

}
