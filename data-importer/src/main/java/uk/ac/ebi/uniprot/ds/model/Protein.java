/*
 * Created by sahmad on 23/01/19 10:21
 * UniProt Consortium.
 * Copyright (c) 2002-2019.
 *
 */

package uk.ac.ebi.uniprot.ds.model;

import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

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

    @ManyToMany(cascade = {CascadeType.PERSIST})
    @JoinTable(name = "ds_disease_protein", joinColumns = @JoinColumn(name = "ds_protein_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "ds_disease_id"))
    private Set<Disease> diseases;

    @OneToMany(mappedBy = "protein", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Pathway> pathways;

    @OneToMany(mappedBy = "protein", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Interaction> interactions;

    @OneToMany(mappedBy = "protein", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Variant> variants;

    public void addVariants(List<Variant> variants) {
        if(this.variants == null){
            this.variants = new ArrayList<>();
        }
        this.variants.addAll(variants);
    }

    public void addInteractions(List<Interaction> interactions) {
        if(this.interactions == null){
            this.interactions = new ArrayList<>();
        }
        this.interactions.addAll(interactions);
    }

    public void addPathway(Pathway pathway) {
        if(this.pathways == null){
            this.pathways = new ArrayList<>();
        }
        this.pathways.add(pathway);
        pathway.setProtein(this);
    }

    public void addPathways(List<Pathway> pathways) {
        if(this.pathways == null){
            this.pathways = new ArrayList<>();
        }
        this.pathways.addAll(pathways);
    }

    public void removePathway(Pathway pathway) {
        if(this.pathways != null) {
            this.pathways.remove(pathway);
            pathway.setProtein(null);
        }
    }

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
