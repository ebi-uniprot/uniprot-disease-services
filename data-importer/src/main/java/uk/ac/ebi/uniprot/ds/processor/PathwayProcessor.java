/*
 * Created by sahmad on 31/01/19 22:48
 * UniProt Consortium.
 * Copyright (c) 2002-2019.
 *
 */

package uk.ac.ebi.uniprot.ds.processor;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import uk.ac.ebi.kraken.interfaces.uniprot.DatabaseCrossReference;
import uk.ac.ebi.kraken.interfaces.uniprot.DatabaseType;
import uk.ac.ebi.kraken.interfaces.uniprot.UniProtEntry;
import uk.ac.ebi.uniprot.ds.dao.PathwayDAO;
import uk.ac.ebi.uniprot.ds.model.Pathway;
import uk.ac.ebi.uniprot.ds.model.Protein;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PathwayProcessor implements ItemProcessor<UniProtEntry, UniProtEntry> {
    private final List<Protein> proteins;

    public PathwayProcessor(List<Protein> proteins) {
        this.proteins = proteins;
    }

    @Override
    public UniProtEntry process(UniProtEntry entry) throws Exception {
        // get the last protein and enrich it
        int size = this.proteins.size();

        Protein protein = this.proteins.get(size - 1);
        List<Pathway> pathways = getPathways(entry, protein);
        protein.addPathways(pathways);

        return entry;
    }

    private List<Pathway> getPathways(UniProtEntry entry, Protein protein) {
        List<DatabaseCrossReference> dbXRefs = getUniProtPathways(entry);
        List<Pathway> pathwayList = new ArrayList<>();
        for (DatabaseCrossReference dbXR : dbXRefs) {
            Pathway.PathwayBuilder builder = Pathway.builder();
            builder.primaryId(dbXR.getPrimaryId().getValue());
            builder.desc(dbXR.getDescription().getValue());
            builder.third(dbXR.getThird() != null ? dbXR.getThird().getValue() : null);
            builder.fourth(dbXR.getFourth() != null ? dbXR.getFourth().getValue() : null);
            builder.dbType(dbXR.getDatabase().getName());
            builder.isoformId(dbXR.getIsoformId().getValue());
            builder.protein(protein);
            Pathway pathway = builder.build();
            pathwayList.add(pathway);
        }

        return pathwayList;
    }

    protected List<DatabaseCrossReference> getUniProtPathways(UniProtEntry entry) {
        List<DatabaseCrossReference> dbXRefs = entry.getDatabaseCrossReferences(DatabaseType.REACTOME);
        return dbXRefs;
    }


}


