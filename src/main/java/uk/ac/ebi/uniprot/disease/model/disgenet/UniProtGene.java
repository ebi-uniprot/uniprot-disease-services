package uk.ac.ebi.uniprot.disease.model.disgenet;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(name="uniprot_gene")
@Getter
@Setter
public class UniProtGene {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "uniprot_id")
    @NotNull
    private String uniProtId;

    @Column(name = "gene_id")
    @NotNull
    private int geneId;
}
