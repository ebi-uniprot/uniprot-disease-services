/*
 * Created by sahmad on 29/01/19 12:00
 * UniProt Consortium.
 * Copyright (c) 2002-2019.
 *
 */

package uk.ac.ebi.uniprot.ds.importer.writer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import uk.ac.ebi.kraken.interfaces.uniprot.Gene;
import uk.ac.ebi.kraken.interfaces.uniprot.ProteinDescription;
import uk.ac.ebi.kraken.interfaces.uniprot.UniProtEntry;
import uk.ac.ebi.kraken.interfaces.uniprot.citationsNew.*;
import uk.ac.ebi.kraken.interfaces.uniprot.comments.CommentType;
import uk.ac.ebi.kraken.interfaces.uniprot.comments.FunctionComment;
import uk.ac.ebi.kraken.interfaces.uniprot.description.FieldType;
import uk.ac.ebi.kraken.interfaces.uniprot.description.Name;
import uk.ac.ebi.uniprot.ds.common.common.PublicationType;
import uk.ac.ebi.uniprot.ds.common.dao.ProteinDAO;
import uk.ac.ebi.uniprot.ds.common.model.Protein;
import uk.ac.ebi.uniprot.ds.common.model.Publication;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
public class ProteinWriter implements ItemWriter<UniProtEntry> {

    @Autowired
    private ProteinDAO proteinDAO;
    private Map<String, Protein> proteinIdProteinMap;

    public ProteinWriter(Map<String, Protein> proteinIdProteinMap) {
        this.proteinIdProteinMap = proteinIdProteinMap;
    }

    @Override
    public void write(List<? extends UniProtEntry> entries) throws Exception {
        List<Protein> proteins = entries.stream().map(entry -> convertToProtein(entry)).collect(Collectors.toList());
        this.proteinDAO.saveAll(proteins);
        this.proteinIdProteinMap.putAll(proteins.stream().collect(Collectors.toMap(p -> p.getProteinId(), p -> p)));
    }

    private Protein convertToProtein(UniProtEntry entry) {
        Protein.ProteinBuilder builder = Protein.builder();
        builder.proteinId(entry.getUniProtId().getValue());
        builder.name(getName(entry.getProteinDescription()));
        builder.accession(entry.getPrimaryUniProtAccession().getValue());
        builder.desc(getDescription(entry.getComments(CommentType.FUNCTION)));
        builder.gene(getGene(entry.getGenes()));
        Protein protein = builder.build();

        // get the publications
        List<Publication> pubs = getPublications(entry, protein);
        protein.setPublications(pubs);
        return protein;
    }



    private String getDescription(List<FunctionComment> comments) {
        StringBuilder stringBuilder = new StringBuilder();
        comments.forEach(comment -> stringBuilder.append(comment.getValue()));
        return stringBuilder.toString();
    }

    protected String getName(ProteinDescription pd) {
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


    private List<Publication> getPublications(UniProtEntry entry, Protein protein) {
        List<Citation> citationList = entry.getCitationsNew();

        // get pub from each citation
        List<Publication> pubs = citationList
                .stream()
                .filter(cit -> cit.getCitationXrefs().hasPubmedId())
                .map(cit -> new Publication(PublicationType.PubMed.name(), cit.getCitationXrefs().getPubmedId().getValue(), protein))
                .collect(Collectors.toList());

        return pubs;
    }
}
