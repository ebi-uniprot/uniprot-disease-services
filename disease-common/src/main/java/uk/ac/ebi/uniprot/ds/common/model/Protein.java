/*
 * Created by sahmad on 07/02/19 10:36
 * UniProt Consortium.
 * Copyright (c) 2002-2019.
 *
 */

package uk.ac.ebi.uniprot.ds.common.model;

import lombok.*;
import uk.ac.ebi.uniprot.ds.common.model.dataservice.Variation;

import javax.persistence.*;
import java.util.*;

@Entity
@Table(name = "ds_protein")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Protein extends BaseEntity {

    private static final long serialVersionUID = -6896338892189706610L;
    @Column(name = "protein_id", nullable = false, unique = true)
    private String proteinId;

    @Column(name = "protein_name", nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String accession;

    @Column(nullable = false)
    private String gene;

    @Column(name = "description")
    private String desc;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "protein", cascade=CascadeType.ALL)
    private Set<DiseaseProtein> diseaseProteins = new HashSet<>(0);

    @OneToMany(mappedBy = "protein", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Variant> variants;

    @OneToMany(mappedBy = "protein", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProteinCrossRef> proteinCrossRefs;

    @OneToMany(mappedBy = "protein", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Interaction> interactions;

    @OneToMany(mappedBy = "protein", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<GeneCoordinate> geneCoordinates = new ArrayList<>();

    @OneToMany(mappedBy = "protein", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Publication> publications;

    @Transient
    private List<Variation> variations;

    @Transient
    private Boolean isExternallyMapped; // to keep manually mapped protein flag

    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        }

        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        Protein protein = (Protein) obj;
        return Objects.equals(getProteinId(), protein.getProteinId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getProteinId());
    }
}
