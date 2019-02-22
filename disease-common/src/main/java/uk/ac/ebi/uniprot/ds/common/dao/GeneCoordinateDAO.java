package uk.ac.ebi.uniprot.ds.common.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.ac.ebi.uniprot.ds.common.model.GeneCoordinate;

public interface GeneCoordinateDAO extends JpaRepository<GeneCoordinate, Long> {
}
