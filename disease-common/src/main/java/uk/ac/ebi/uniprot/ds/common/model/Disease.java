/*
 * Created by sahmad on 07/02/19 10:36
 * UniProt Consortium.
 * Copyright (c) 2002-2019.
 *
 */

package uk.ac.ebi.uniprot.ds.common.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import uk.ac.ebi.uniprot.ds.common.model.dataservice.Variation;

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

    @JsonIgnore// To keep SpringBatch happy. SpringBatch in data importer uses Jackson to serialize object and recursive object causing StackOverflow
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "disease", cascade=CascadeType.ALL)
    private Set<DiseaseProtein> diseaseProteins = new HashSet<>(0);

    @JsonIgnore// To keep SpringBatch happy. SpringBatch in data importer uses Jackson to serialize object and recursive object causing StackOverflow
    @OneToMany(mappedBy = "disease", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Synonym> synonyms;

    @JsonIgnore// To keep SpringBatch happy. SpringBatch in data importer uses Jackson to serialize object and recursive object causing StackOverflow
    @OneToMany(mappedBy = "disease", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Variant> variants;

    @JsonIgnore// To keep SpringBatch happy. SpringBatch in data importer uses Jackson to serialize object and recursive object causing StackOverflow
    @OneToMany(mappedBy = "disease", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CrossRef> crossRefs;


    @ManyToMany(cascade = {CascadeType.PERSIST})
    @JoinTable(name = "ds_disease_relation", joinColumns = @JoinColumn(name = "ds_disease_parent_id"),
            inverseJoinColumns = @JoinColumn(name = "ds_disease_id"))
    private List<Disease> children;

    @ManyToMany
    @JoinTable(name = "ds_disease_relation", joinColumns = @JoinColumn(name = "ds_disease_id"),
            inverseJoinColumns = @JoinColumn(name = "ds_disease_parent_id"))
    private List<Disease> parents;

    @JsonIgnore// To keep SpringBatch happy. SpringBatch in data importer uses Jackson to serialize object and recursive object causing StackOverflow
    @OneToMany(mappedBy = "disease", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Keyword> keywords;

    @JsonIgnore// To keep SpringBatch happy. SpringBatch in data importer uses Jackson to serialize object and recursive object causing StackOverflow
    @OneToMany(mappedBy = "disease", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Publication> publications;

    @Transient
    private List<Variation> variations;

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
