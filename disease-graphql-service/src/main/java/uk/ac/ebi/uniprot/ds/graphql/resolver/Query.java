package uk.ac.ebi.uniprot.ds.graphql.resolver;

import com.coxautodev.graphql.tools.GraphQLQueryResolver;
import graphql.schema.DataFetchingEnvironment;
import org.springframework.stereotype.Component;
import uk.ac.ebi.uniprot.ds.common.dao.DiseaseDAO;
import uk.ac.ebi.uniprot.ds.common.dao.ProteinDAO;
import uk.ac.ebi.uniprot.ds.common.model.Disease;
import uk.ac.ebi.uniprot.ds.common.model.Protein;

@Component
public class Query implements GraphQLQueryResolver {
    private DiseaseDAO diseaseDAO;
    private ProteinDAO proteinDAO;

    public Query(DiseaseDAO diseaseDAO, ProteinDAO proteinDAO){
        this.diseaseDAO = diseaseDAO;
        this.proteinDAO = proteinDAO;
    }

    public Disease getDisease(String diseaseId){
        return this.diseaseDAO.findByDiseaseId(diseaseId).orElse(null);
    }

    public Protein getProtein(String accession){
        return this.proteinDAO.findProteinByAccession(accession).orElse(null);
    }
}
