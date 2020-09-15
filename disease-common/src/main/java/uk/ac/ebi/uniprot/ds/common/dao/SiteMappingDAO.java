package uk.ac.ebi.uniprot.ds.common.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

import uk.ac.ebi.uniprot.ds.common.model.SiteMapping;

@Repository
public interface SiteMappingDAO extends JpaRepository<SiteMapping, Long>{
    List<SiteMapping> findAllByAccession(String accession);
}
