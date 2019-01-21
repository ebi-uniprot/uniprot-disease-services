/*
 * Created by sahmad on 1/18/19 9:36 AM
 * UniProt Consortium.
 * Copyright (c) 2002-2019.
 *
 */

package uk.ac.ebi.diseaseservice.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import uk.ac.ebi.kraken.interfaces.uniprot.DatabaseType;
import uk.ac.ebi.kraken.interfaces.uniprot.UniProtIsoformId;
import uk.ac.ebi.kraken.interfaces.uniprot.dbx.DatabaseAttribute;
import uk.ac.ebi.kraken.interfaces.uniprot.evidences.EvidenceId;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@Builder
public class Pathway implements Serializable {

    private static final long serialVersionUID = -6295486276730133608L;
    private String _id;
    private DatabaseAttribute primaryId;
    private DatabaseAttribute description;
    private DatabaseAttribute third;
    private DatabaseAttribute fourth;
    private final DatabaseType dbType;
    private List<EvidenceId> evidenceIds;
    private UniProtIsoformId isoformId;
    private List<String> diseaseIds;
    private List<String> proteinIds;

    @Override
    public String toString() {
        return "Pathway{" +
                "_id='" + _id + '\'' +
                ", primaryId=" + primaryId +
                ", description=" + description +
                ", third=" + third +
                ", fourth=" + fourth +
                ", dbType=" + dbType +
                ", evidenceIds=" + evidenceIds +
                ", isoformId=" + isoformId +
                ", diseaseIds=" + diseaseIds +
                ", proteinIds=" + proteinIds +
                '}';
    }
}
