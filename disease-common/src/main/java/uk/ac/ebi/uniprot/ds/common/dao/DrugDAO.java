package uk.ac.ebi.uniprot.ds.common.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

import uk.ac.ebi.uniprot.ds.common.model.Disease;
import uk.ac.ebi.uniprot.ds.common.model.Drug;

@Repository
public interface DrugDAO extends JpaRepository<Drug, Long>, DrugDAOCustom {
    List<Drug> findAllByDisease(Disease disease);
}
