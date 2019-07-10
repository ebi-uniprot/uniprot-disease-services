/*
 * Created by sahmad on 07/02/19 10:36
 * UniProt Consortium.
 * Copyright (c) 2002-2019.
 *
 */

package uk.ac.ebi.uniprot.ds.common.model;

import lombok.*;
import javax.persistence.*;
import java.util.*;

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
    
    @Column
    private String note;

    @Column(name="source_name", nullable = false)
    private String source;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "disease", cascade=CascadeType.ALL)
    private Set<DiseaseProtein> diseaseProteins = new HashSet<>(0);

    @OneToMany(mappedBy = "disease", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Synonym> synonyms;

    @OneToMany(mappedBy = "disease", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Variant> variants;

    @OneToMany(mappedBy = "disease", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CrossRef> crossRefs;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(name = "ds_disease_relation", joinColumns = @JoinColumn(name = "ds_disease_parent_id"),
            inverseJoinColumns = @JoinColumn(name = "ds_disease_id"))
    private List<Disease> children;

    @ManyToMany
    @JoinTable(name = "ds_disease_relation", joinColumns = @JoinColumn(name = "ds_disease_id"),
            inverseJoinColumns = @JoinColumn(name = "ds_disease_parent_id"))
    private List<Disease> parents;

    @OneToMany(mappedBy = "disease", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Keyword> keywords;

    @OneToMany(mappedBy = "disease", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Publication> publications;

    public void addSynonym(Synonym synonym){
        if(this.synonyms == null){
            this.synonyms = new ArrayList<>();
        }
        this.synonyms.add(synonym);
        synonym.setDisease(this);
    }

    public void removeSynonym(Synonym synonym){
        this.synonyms.remove(synonym);
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
