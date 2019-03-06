/*
 * Created by sahmad on 29/01/19 16:00
 * UniProt Consortium.
 * Copyright (c) 2002-2019.
 *
 */

package uk.ac.ebi.uniprot.ds.importer.writer;

import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import uk.ac.ebi.kraken.interfaces.uniprot.DatabaseCrossReference;
import uk.ac.ebi.kraken.interfaces.uniprot.DatabaseType;
import uk.ac.ebi.kraken.interfaces.uniprot.UniProtEntry;
import uk.ac.ebi.uniprot.ds.common.dao.ProteinCrossRefDAO;
import uk.ac.ebi.uniprot.ds.common.model.ProteinCrossRef;
import uk.ac.ebi.uniprot.ds.common.model.Protein;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ProteinCrossRefWriter implements ItemWriter<UniProtEntry> {
    private final Map<String, Protein> proteinIdProteinMap;

    @Autowired
    ProteinCrossRefDAO proteinCrossRefDAO;

    public ProteinCrossRefWriter(Map<String, Protein> proteinIdProteinMap) {
        this.proteinIdProteinMap = proteinIdProteinMap;
    }

    @Override
    public void write(List<? extends UniProtEntry> entries) {
        entries.stream().forEach(entry -> {
            Protein protein = this.proteinIdProteinMap.get(entry.getUniProtId().getValue());
            assert protein != null;
            List<ProteinCrossRef> interactions = getProteinCrossRefs(entry, protein);
            this.proteinCrossRefDAO.saveAll(interactions);
        });
    }

    private List<ProteinCrossRef> getProteinCrossRefs(UniProtEntry entry, Protein protein) {
        List<DatabaseCrossReference> dbXRefs = getUniProtProteinCrossRefs(entry);
        List<ProteinCrossRef> proteinCrossRefs = new ArrayList<>();
        for (DatabaseCrossReference dbXR : dbXRefs) {
            ProteinCrossRef.ProteinCrossRefBuilder builder = ProteinCrossRef.builder();
            builder.primaryId(dbXR.getPrimaryId().getValue());
            builder.desc(dbXR.getDescription().getValue());
            builder.third(dbXR.getThird() != null ? dbXR.getThird().getValue() : null);
            builder.fourth(dbXR.getFourth() != null ? dbXR.getFourth().getValue() : null);
            builder.dbType(dbXR.getDatabase().getName());
            builder.isoformId(dbXR.getIsoformId().getValue());
            builder.protein(protein);
            ProteinCrossRef crossRef = builder.build();
            proteinCrossRefs.add(crossRef);
        }

        return proteinCrossRefs;
    }

    protected List<DatabaseCrossReference> getUniProtProteinCrossRefs(UniProtEntry entry) {
        List<DatabaseCrossReference> dbXRefs = entry.getDatabaseCrossReferences(DatabaseType.REACTOME);
        return dbXRefs;
    }
}


