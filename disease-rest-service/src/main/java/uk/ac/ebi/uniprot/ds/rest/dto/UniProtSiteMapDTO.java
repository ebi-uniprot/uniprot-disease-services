package uk.ac.ebi.uniprot.ds.rest.dto;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

/**
 * @author jluo
 * @date: 03-Sep-2020
 */
@Getter
@Setter
public class UniProtSiteMapDTO {
    private String accession;
    private String uniProtId;
    private Long sitePosition;
    private Long positionInAlignment;
    private List<FeatureType> featureTypes;
//    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<String> dbSnps;
    private List<UniProtSite> mappedSites;
    private String unirefId;
}

