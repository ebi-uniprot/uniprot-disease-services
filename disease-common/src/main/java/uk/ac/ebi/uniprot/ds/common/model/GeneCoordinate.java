package uk.ac.ebi.uniprot.ds.common.model;

import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "ds_gene_coordinate")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GeneCoordinate extends BaseEntity {
    private static final long serialVersionUID = -4930092285247822599L;
    @Column(name="chromosome_number")
    private String chromosomeNumber;
    @Column(name="gene_start", nullable = false)
    private Long startPos;
    @Column(name="gene_end", nullable = false)
    private Long endPos;
    @Column(name="ensembl_gene_id", nullable = false)
    private String enGeneId;
    @Column(name="ensembl_transcript_id", nullable = false)
    private String enTranscriptId;
    @Column(name="ensembl_translation_id", nullable = false)
    private String enTranslationId;
    @OneToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "ds_protein_id", nullable = false)
    private Protein protein;
}
