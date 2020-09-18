package uk.ac.ebi.uniprot.ds.rest.dto;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import uk.ac.ebi.uniprot.ds.common.model.Drug;
import uk.ac.ebi.uniprot.ds.common.model.Protein;
import uk.ac.ebi.uniprot.ds.common.model.ProteinCrossRef;
import uk.ac.ebi.uniprot.ds.rest.utils.ModelCreationUtils;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@RunWith(SpringRunner.class)
@SpringBootTest
public class DrugToDrugDTOTest {
    private String uuid = UUID.randomUUID().toString();
    @Autowired
    private ModelMapper modelMapper;

    @Test
    public void testDrugToDrugDTO(){
        Drug drug = ModelCreationUtils.createDrugObject(this.uuid);
        DrugDTO drugDTO = modelMapper.map(drug, DrugDTO.class);
        verifyDrug(drug, drugDTO);
    }

    @Test
    public void testDrugToDrugDTOWithDiseasesAndProteins(){
        Drug drug = ModelCreationUtils.createDrugObject(this.uuid);
        Set<Pair<String, Integer>> dNames = new HashSet<>();
        dNames.add(Pair.of("D-" + this.uuid + 1, 1));
        dNames.add(Pair.of("D-" + this.uuid + 2, 2));
        HashSet<String> accessions = new HashSet<>(); accessions.add("A-" + this.uuid);
        drug.setDiseaseProteinCount(dNames);
        drug.setProteins(accessions);
        DrugDTO drugDTO = modelMapper.map(drug, DrugDTO.class);
        verifyDrug(drug, drugDTO);
    }

    private void verifyDrug(Drug drug, DrugDTO drugDTO) {
        Assert.assertEquals(drug.getName(), drugDTO.getName());
        Assert.assertEquals(drug.getMoleculeType(), drugDTO.getMoleculeType());
        Assert.assertEquals(drug.getSourceId(), drugDTO.getSourceId());
        Assert.assertEquals(drug.getSourceType(), drugDTO.getSourceType());
        Assert.assertEquals(drug.getClinicalTrialLink(), drugDTO.getClinicalTrialLink());
        Assert.assertEquals(drug.getClinicalTrialPhase(), drugDTO.getClinicalTrialPhase());
        Assert.assertEquals(drug.getMechanismOfAction(), drugDTO.getMechanismOfAction());
        Assert.assertEquals(drug.getDrugEvidences().size(), drugDTO.getEvidences().size());
//FIXME
//        if(drug.getDiseases() != null){
//            Assert.assertEquals(drug.getDiseases().size(), drugDTO.getDiseases().size());
//        }

        if(drug.getProteins() != null){
            Assert.assertEquals(drug.getProteins().size(), drugDTO.getProteins().size());
        }
    }
}
