package uk.ac.ebi.uniprot.ds.rest.mapper;

import org.modelmapper.Converter;
import org.modelmapper.PropertyMap;
import org.modelmapper.spi.MappingContext;
import uk.ac.ebi.uniprot.ds.common.model.*;
import uk.ac.ebi.uniprot.ds.rest.dto.DrugDTO;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class DrugToDrugDTOMap extends PropertyMap<Drug, DrugDTO> {
    
    @Override
    protected void configure() {
        using(new DrugEvidencesToEvidences()).map(source.getDrugEvidences()).setEvidences(null);

    }

    private static class DrugEvidencesToEvidences implements Converter<List<DrugEvidence>, Set<String>> {
        @Override
        public Set<String> convert(MappingContext<List<DrugEvidence>, Set<String>> context) {

            List<DrugEvidence> drugEvidences = context.getSource();
            Set<String> urls = null;

            if (drugEvidences != null) {
                urls = drugEvidences.stream().map(de -> de.getRefUrl()).collect(Collectors.toSet());
            }

            return urls;
        }

    }

}
