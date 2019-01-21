/*
 * Created by sahmad on 21/01/19 10:55
 * UniProt Consortium.
 * Copyright (c) 2002-2019.
 *
 */

package uk.ac.ebi.diseaseservice.writer;

import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import uk.ac.ebi.diseaseservice.model.Pathway;
import uk.ac.ebi.diseaseservice.model.Protein;
import uk.ac.ebi.kraken.interfaces.uniprot.DatabaseCrossReference;
import uk.ac.ebi.kraken.interfaces.uniprot.UniProtEntry;
import uk.ac.ebi.kraken.interfaces.uniprot.dbx.DatabaseAttribute;

import java.util.ArrayList;
import java.util.List;

public class PathwayWriter extends BaseSwissProtWriter {
    @Override
    protected void doWrite(List<? extends UniProtEntry> entries) {

        MongoOperations mongoOperations = getTemplate();

        for(UniProtEntry entry : entries) {
            Protein protein = findProteinbyProteinId(entry.getUniProtId().getValue());

            List<DatabaseCrossReference> dbXRefs = getPathways(entry);

            for(DatabaseCrossReference dbXR : dbXRefs) {
                // try to get the xref from db first
                Pathway pathway = findPathway(dbXR.getPrimaryId());
                if(pathway == null){ // create a new one
                    Pathway.PathwayBuilder builder = Pathway.builder();
                    builder.primaryId(dbXR.getPrimaryId());
                    builder.description(dbXR.getDescription());
                    builder.third(dbXR.getThird());
                    builder.fourth(dbXR.getFourth());
                    builder.dbType(dbXR.getDatabase());
                    builder.evidenceIds(dbXR.getEvidenceIds());
                    builder.isoformId(dbXR.getIsoformId());
                    List<String> pIds = new ArrayList<>();
                    pIds.add(protein.get_id());
                    builder.proteinIds(pIds);
                    pathway = builder.build();

                } else { // update the existing one
                    pathway.getProteinIds().add(protein.get_id());
                }

                mongoOperations.save(pathway);
                updateProtein(protein, pathway);
            }

        }
    }

    private Pathway findPathway(DatabaseAttribute primaryId) {
        MongoOperations mongoOperations = getTemplate();
        Query query = new Query();
        query.addCriteria(Criteria.where("primaryId").is(primaryId));

        Pathway pathway = mongoOperations.findOne(query, Pathway.class);

        return pathway;
    }
}
