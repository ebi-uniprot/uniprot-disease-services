package uk.ac.ebi.uniprot.ds.rest.mapper;

import org.modelmapper.Converter;
import org.modelmapper.spi.MappingContext;
import uk.ac.ebi.uniprot.ds.common.model.Variant;

import java.util.List;
import java.util.stream.Collectors;

public class VariantsToFeatureIdsConverter implements Converter<List<Variant>, List<String>> {
    @Override
    public List<String> convert(MappingContext<List<Variant>, List<String>> ctx) {
        List<Variant> variants = ctx.getSource();
        List<String> varsStr = null;
        if (variants != null) {
            varsStr = variants.stream().map(var -> var.getFeatureId()).collect(Collectors.toList());
        }
        return varsStr;
    }
}
