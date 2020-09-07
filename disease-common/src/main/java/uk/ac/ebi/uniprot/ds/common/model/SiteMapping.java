package uk.ac.ebi.uniprot.ds.common.model;

import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "ds_site_mapping")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SiteMapping extends BaseEntity {

    private static final long serialVersionUID = 5524371894844073567L;

    @Column(nullable = false)
    private String accession;

    @Column(name = "protein_id", nullable = false)
    private String proteinId;

    @Column(name = "site_position", nullable = false)
    private Long sitePosition;

    @Column(name = "position_in_alignment", nullable = false)
    private Long positionInAlignment;

    @Column(name = "site_type")
    private String siteType;

    @Column(name = "uniref_id", nullable = false)
    private String unirefId;

    @Column(name = "mapped_site")
    private String mappedSite;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SiteMapping that = (SiteMapping) o;
        return accession.equals(that.accession) &&
                proteinId.equals(that.proteinId) &&
                sitePosition.equals(that.sitePosition) &&
                positionInAlignment.equals(that.positionInAlignment) &&
                Objects.equals(siteType, that.siteType) &&
                unirefId.equals(that.unirefId) &&
                Objects.equals(mappedSite, that.mappedSite);
    }

    @Override
    public int hashCode() {
        return Objects.hash(accession, proteinId, sitePosition, positionInAlignment, siteType, unirefId, mappedSite);
    }
}
