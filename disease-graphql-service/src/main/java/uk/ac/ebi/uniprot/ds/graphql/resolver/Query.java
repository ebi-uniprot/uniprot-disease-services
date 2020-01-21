package uk.ac.ebi.uniprot.ds.graphql.resolver;

import com.coxautodev.graphql.tools.GraphQLQueryResolver;
import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import uk.ac.ebi.uniprot.ds.common.dao.DiseaseDAO;
import uk.ac.ebi.uniprot.ds.common.dao.ProteinDAO;
import uk.ac.ebi.uniprot.ds.common.model.Disease;
import uk.ac.ebi.uniprot.ds.common.model.Protein;
import uk.ac.ebi.uniprot.ds.graphql.model.DiseaseType;
import uk.ac.ebi.uniprot.ds.graphql.model.ProteinType;

import java.util.List;

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

    public Iterable<DiseaseType> diseases(DiseaseFilter filter, Integer page, Integer size){
        String name = null;
        String desc = null;
        if(filter != null){
            name = filter.getNameContains();
            desc = filter.getDescriptionContains();
        }

        Sort sortAsc = new Sort(Sort.Direction.ASC, "id");
        PageRequest pageRequest = PageRequest.of(page, size, sortAsc);
        if(StringUtils.isBlank(name) && StringUtils.isBlank(desc)){ // get without filter
            return this.diseaseDAO.findAll(pageRequest).map(this::fromDisease);
        } else {
            List<Disease> diseases;
            if(StringUtils.isBlank(desc)){
                diseases = this.diseaseDAO.findByNameContainingIgnoreCase(name, pageRequest);
            } else if(StringUtils.isBlank(name)){
                diseases = this.diseaseDAO.findByDescContainingIgnoreCase(desc, pageRequest);
            } else {
                diseases = this.diseaseDAO.findByNameContainingIgnoreCaseOrDescContainingIgnoreCase(name, desc, pageRequest);
            }
            return this.modelMapper.map(diseases, new TypeToken<List<DiseaseType>>(){}.getType());
        }
    }

    private DiseaseType fromDisease(Disease disease) {
        return disease != null ? modelMapper.map(disease, DiseaseType.class) : null;
    }
}
