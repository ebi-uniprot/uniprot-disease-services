package uk.ac.ebi.uniprot.ds.common.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.ac.ebi.uniprot.ds.common.model.CrossRef;

public interface CrossRefDAO extends JpaRepository<CrossRef, Long> {
}
