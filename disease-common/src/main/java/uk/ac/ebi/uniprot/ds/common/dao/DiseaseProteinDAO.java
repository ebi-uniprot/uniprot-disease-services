package uk.ac.ebi.uniprot.ds.common.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uk.ac.ebi.uniprot.ds.common.model.DiseaseProtein;
import uk.ac.ebi.uniprot.ds.common.model.DiseaseProteinId;
import uk.ac.ebi.uniprot.ds.common.model.Drug;
import uk.ac.ebi.uniprot.ds.common.model.Protein;

import java.util.List;

@Repository
public interface DiseaseProteinDAO extends JpaRepository<DiseaseProtein, DiseaseProteinId> {
    List<DiseaseProtein> findAllByProtein(Protein protein);
}
