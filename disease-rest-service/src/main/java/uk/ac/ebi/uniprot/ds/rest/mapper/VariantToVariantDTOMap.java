package uk.ac.ebi.uniprot.ds.rest.mapper;

import org.modelmapper.Converter;
import org.modelmapper.PropertyMap;
import org.modelmapper.spi.MappingContext;
import uk.ac.ebi.uniprot.ds.common.model.*;
import uk.ac.ebi.uniprot.ds.rest.dto.VariantDTO;

public class VariantToVariantDTOMap extends PropertyMap<Variant, VariantDTO> {
    @Override
    protected void configure() {
        map().setOrigSeq(source.getOrigSeq());
        map().setAltSeq(source.getAltSeq());
        map().setReport(source.getReport());
        map().setFeatureStatus(source.getFeatureStatus());
        using(new FeatureLocationToFeatureLocationDTO()).map(source.getFeatureLocation()).setFeatureLocation(null);
        map().setProteinAccession(source.getProtein().getAccession());
    }

    private static class FeatureLocationToFeatureLocationDTO implements Converter<FeatureLocation, VariantDTO.FeatureLocationDTO> {

        @Override
        public VariantDTO.FeatureLocationDTO convert(MappingContext<FeatureLocation, VariantDTO.FeatureLocationDTO> context) {
            FeatureLocation fl = context.getSource();
            if(fl != null) {
                VariantDTO.FeatureLocationDTO.FeatureLocationDTOBuilder bldr = VariantDTO.FeatureLocationDTO.builder();
                bldr.startModifier(fl.getStartModifier()).endModifier(fl.getEndModifier());
                bldr.startId(fl.getStartId()).endId(fl.getEndId());

                return bldr.build();
            } else {
                return null;
            }
        }
    }

}
