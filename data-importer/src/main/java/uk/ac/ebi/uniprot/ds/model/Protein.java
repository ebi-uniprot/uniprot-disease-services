/*
 * Created by sahmad on 23/01/19 10:21
 * UniProt Consortium.
 * Copyright (c) 2002-2019.
 *
 */

package uk.ac.ebi.uniprot.ds.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "ds_protein")
@Getter
@Setter
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

    @ManyToMany(cascade = CascadeType.PERSIST)
    @JoinTable(name = "ds_disease_protein", joinColumns = @JoinColumn(name = "ds_protein_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "ds_disease_id"))
    private Set<Disease> diseases;

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
