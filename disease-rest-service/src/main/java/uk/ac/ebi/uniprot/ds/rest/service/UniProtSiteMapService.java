package uk.ac.ebi.uniprot.ds.rest.service;

import org.springframework.stereotype.Service;

import java.util.List;

import lombok.extern.slf4j.Slf4j;
import uk.ac.ebi.uniprot.ds.common.dao.SiteMappingDAO;
import uk.ac.ebi.uniprot.ds.common.model.SiteMapping;

@Service
@Slf4j
public class UniProtSiteMapService {
    private final SiteMappingDAO siteMappingDAO;

    public UniProtSiteMapService(SiteMappingDAO siteMappingDAO) {
        this.siteMappingDAO = siteMappingDAO;
    }

    public List<SiteMapping> getSiteMappings(String accession){
        return this.siteMappingDAO.findAllByAccession(accession);
    }
}
