/*
 * Created by sahmad on 23/01/19 09:16
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
@Table(name = "ds_disease")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Disease extends BaseEntity {

    private static final long serialVersionUID = 924803633810006763L;

    @Column(name = "disease_id", unique = true, nullable = false)
    private String diseaseId;

    @Column(name = "disease_name", nullable = false)
    private String name;

    @Column(name = "description")
    private String desc;

    @Column
    private String acronym;

    @ManyToMany(mappedBy = "diseases")
    private Set<Protein> proteins;

    @OneToMany(mappedBy = "disease", cascade = CascadeType.PERSIST, orphanRemoval = true)
    private List<Synonym> synonyms;

    @OneToMany(mappedBy = "disease", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Variant> variants;

    public void addVariants(List<Variant> variants) {
        if(this.variants == null){
            this.variants = new ArrayList<>();
        }
        this.variants.addAll(variants);
    }

    public void addSynonym(Synonym synonym){
        this.synonyms.add(synonym);
        synonym.setDisease(this);
    }

    public void removeSynonym(Synonym synonym){
        this.synonyms.remove(synonym);
        synonym.setDisease(null);
    }

    @Override
    public boolean equals(Object obj) {
        if(this == obj){
            return true;
        }

        if(obj == null || getClass() != obj.getClass()){
            return false;
        }

        Disease disease = (Disease) obj;
        return Objects.equals(getDiseaseId(), disease.getDiseaseId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getDiseaseId());
    }
}
