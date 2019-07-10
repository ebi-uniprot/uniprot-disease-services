package uk.ac.ebi.uniprot.ds.rest.mapper;

import org.modelmapper.Converter;
import org.modelmapper.PropertyMap;
import org.modelmapper.spi.MappingContext;
import uk.ac.ebi.uniprot.ds.common.model.*;
import uk.ac.ebi.uniprot.ds.rest.dto.DrugDTO;
import java.util.List;
import java.util.stream.Collectors;

public class DrugToDrugDTOMap extends PropertyMap<Drug, DrugDTO> {
    
    @Override
    protected void configure() {
        using(new DrugEvidencesToEvidences()).map(source.getDrugEvidences()).setEvidences(null);

    }

    private static class DrugEvidencesToEvidences implements Converter<List<DrugEvidence>, List<String>> {
        @Override
        public List<String> convert(MappingContext<List<DrugEvidence>, List<String>> context) {

            List<DrugEvidence> drugEvidences = context.getSource();
            List<String> urls = null;

            if (drugEvidences != null) {
                urls = drugEvidences.stream().map(de -> de.getRefUrl()).collect(Collectors.toList());
            }

            return urls;
        }
    }

}
