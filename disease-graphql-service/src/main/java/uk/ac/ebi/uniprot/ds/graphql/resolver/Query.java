package uk.ac.ebi.uniprot.ds.graphql.resolver;

import com.coxautodev.graphql.tools.GraphQLQueryResolver;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.ac.ebi.uniprot.ds.common.dao.DiseaseDAO;
import uk.ac.ebi.uniprot.ds.common.dao.ProteinDAO;
import uk.ac.ebi.uniprot.ds.common.model.Disease;
import uk.ac.ebi.uniprot.ds.common.model.Protein;
import uk.ac.ebi.uniprot.ds.graphql.model.DiseaseType;
import uk.ac.ebi.uniprot.ds.graphql.model.ProteinType;

@Service
public class Query implements GraphQLQueryResolver {

    @Autowired
    private ModelMapper modelMapper;
    private DiseaseDAO diseaseDAO;
    private ProteinDAO proteinDAO;

    public Query(DiseaseDAO diseaseDAO, ProteinDAO proteinDAO) {
        this.diseaseDAO = diseaseDAO;
        this.proteinDAO = proteinDAO;
    }

    public DiseaseType disease(String diseaseId) {
        Disease disease = this.diseaseDAO.findByDiseaseId(diseaseId).orElse(null);
        return disease != null ? modelMapper.map(disease, DiseaseType.class) : null;
    }

    public ProteinType protein(String accession) {
        Protein protein = this.proteinDAO.findProteinByAccession(accession).orElse(null);
        return protein != null ? modelMapper.map(protein, ProteinType.class) : null;
    }
}
