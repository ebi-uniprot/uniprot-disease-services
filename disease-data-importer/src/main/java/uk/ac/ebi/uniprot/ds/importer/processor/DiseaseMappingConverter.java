package uk.ac.ebi.uniprot.ds.importer.processor;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import uk.ac.ebi.uniprot.ds.common.dao.CrossRefDAO;
import uk.ac.ebi.uniprot.ds.common.dao.DiseaseDAO;
import uk.ac.ebi.uniprot.ds.common.model.CrossRef;
import uk.ac.ebi.uniprot.ds.common.model.Disease;
import uk.ac.ebi.uniprot.ds.common.model.Synonym;
import uk.ac.ebi.uniprot.ds.importer.model.DiseaseMapping;

import java.util.List;

public class DiseaseMappingConverter implements ItemProcessor<DiseaseMapping, List<Disease>> {
    private enum XRefMapper{

        OMIM("MIM"),
        DisGeNET("DisGeNET");
        private final String xref;
        XRefMapper(String xref) {
            this.xref = xref;
        }
    }

    @Autowired
    private CrossRefDAO crossRefDAO;
    @Autowired
    private DiseaseDAO diseaseDAO;
    @Override
    public List<Disease> process(DiseaseMapping item) throws Exception {

        // get by vocab and code
        List<CrossRef> xRefs1 = crossRefDAO.findAllByRefTypeAndRefId(XRefMapper.valueOf(item.getVocab()).xref, item.getCode());

        // get by DisGeNET and disease id
        List<CrossRef> xRefs2 = crossRefDAO.findAllByRefTypeAndRefId(XRefMapper.DisGeNET.xref, item.getDiseaseId());

        Synonym.SynonymBuilder sb = Synonym.builder();
        Synonym syn1 = sb.name(item.getName()).source(XRefMapper.DisGeNET.xref).build();
        Synonym syn2 = sb.name(item.getVocabName()).source(XRefMapper.DisGeNET.xref).build();


        // if xRefs is empty then try to find by DisGeNET disease ID
        //  if  it is still empty  it means DisGeNET xref/disease mapping row is not there in HumDisease

        // create a disease row, a synonym from vocab name and a xref  where DisGeNET is source

        // if not empty, the mapping is there
        // create two synonyms for each disease in xref, create one xref source as DisGenET

        return null;
    }
}
