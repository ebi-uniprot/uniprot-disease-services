/*
 * Created by sahmad on 29/01/19 16:00
 * UniProt Consortium.
 * Copyright (c) 2002-2019.
 *
 */

package uk.ac.ebi.uniprot.ds.importer.writer;

import uk.ac.ebi.kraken.interfaces.uniprot.DatabaseCrossReference;
import uk.ac.ebi.kraken.interfaces.uniprot.DatabaseType;
import uk.ac.ebi.kraken.interfaces.uniprot.UniProtEntry;
import uk.ac.ebi.uniprot.ds.common.model.ProteinCrossRef;
import uk.ac.ebi.uniprot.ds.common.model.Protein;

import java.util.ArrayList;
import java.util.List;

public class ProteinCrossRefHelper {

    public List<ProteinCrossRef> getProteinCrossRefs(UniProtEntry entry, Protein protein) {
        List<DatabaseCrossReference> dbXRefs = getUniProtProteinCrossRefs(entry);
        List<ProteinCrossRef> proteinCrossRefs = new ArrayList<>();
        for (DatabaseCrossReference dbXR : dbXRefs) {
            ProteinCrossRef.ProteinCrossRefBuilder builder = ProteinCrossRef.builder();
            builder.primaryId(dbXR.getPrimaryId().getValue());
            builder.description(dbXR.getDescription().getValue());
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


        List<DatabaseCrossReference> reactXrefs = entry.getDatabaseCrossReferences(DatabaseType.REACTOME);
        List<DatabaseCrossReference> chemblXRefs = entry.getDatabaseCrossReferences(DatabaseType.CHEMBL);
        List<DatabaseCrossReference> openTargetsXRefs = entry.getDatabaseCrossReferences(DatabaseType.OPENTARGETS);
        List<DatabaseCrossReference> disgenetXRefs = entry.getDatabaseCrossReferences(DatabaseType.DISGENET);
        List<DatabaseCrossReference> dbXRefs = new ArrayList<>();
        dbXRefs.addAll(reactXrefs);
        dbXRefs.addAll(chemblXRefs);
        dbXRefs.addAll(openTargetsXRefs);
        dbXRefs.addAll(disgenetXRefs);

        return dbXRefs;
    }
}


