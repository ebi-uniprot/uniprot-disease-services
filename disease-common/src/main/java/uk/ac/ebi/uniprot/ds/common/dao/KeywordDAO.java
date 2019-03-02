package uk.ac.ebi.uniprot.ds.common.dao;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import uk.ac.ebi.uniprot.ds.common.model.CrossRef;
import uk.ac.ebi.uniprot.ds.common.model.Keyword;

import java.util.List;

public interface KeywordDAO extends JpaRepository<Keyword, Long> {

    List<Keyword> findAllByKeyValueContaining(String keyValue, Pageable pageable);

}
