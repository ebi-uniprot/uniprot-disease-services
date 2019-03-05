package uk.ac.ebi.uniprot.ds.importer.processor;

import org.apache.commons.lang3.tuple.Pair;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import uk.ac.ebi.uniprot.ds.common.dao.DiseaseDAO;
import uk.ac.ebi.uniprot.ds.common.model.Disease;
import uk.ac.ebi.uniprot.ds.common.model.Synonym;

import java.util.Optional;

public class DOHumToSynonymConverter implements ItemProcessor<Pair<String, String>, Synonym> {

    @Autowired
    private DiseaseDAO diseaseDAO;

    @Override
    public Synonym process(Pair<String, String> item){
        Synonym synonym = null;
        Optional<Disease> optDisease = this.diseaseDAO.findDiseaseByNameIgnoreCase(item.getRight());
        if(optDisease.isPresent()){
            Synonym.SynonymBuilder bldr = Synonym.builder();
            bldr.name(item.getLeft()).source("Disease Ontology");
            bldr.disease(optDisease.get());
            synonym = bldr.build();
        }

        return synonym;
    }
}
