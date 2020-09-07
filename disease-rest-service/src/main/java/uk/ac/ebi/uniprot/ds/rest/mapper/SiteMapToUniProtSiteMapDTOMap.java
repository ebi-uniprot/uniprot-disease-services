package uk.ac.ebi.uniprot.ds.rest.mapper;

import org.apache.commons.lang3.StringUtils;
import org.modelmapper.Converter;
import org.modelmapper.PropertyMap;
import org.modelmapper.spi.MappingContext;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import uk.ac.ebi.uniprot.ds.common.model.SiteMapping;
import uk.ac.ebi.uniprot.ds.rest.dto.FeatureType;
import uk.ac.ebi.uniprot.ds.rest.dto.UniProtSite;
import uk.ac.ebi.uniprot.ds.rest.dto.UniProtSiteMapDTO;

/**
 * @author jluo
 * @date: 03-Sep-2020
 */

public class SiteMapToUniProtSiteMapDTOMap extends PropertyMap<SiteMapping, UniProtSiteMapDTO> {
    private static final String SEMI_COLON = ";";
    private static final String RS = "rs";

    @Override
    protected void configure() {
        map().setAccession(source.getAccession());
        map().setUniProtId(source.getProteinId());
        map().setUnirefId(source.getUnirefId());
        map().setSitePosition(source.getSitePosition());
        map().setPositionInAlignment(source.getPositionInAlignment());
        using(new FeatureTypeConverter()).map(source.getSiteType()).setFeatureTypes(null);
        using(new DbSnpConverter()).map(source.getSiteType()).setDbSnps(null);
        using(new UniProtSiteConverter()).map(source.getMappedSite()).setMappedSites(null);
    }


    private static class FeatureTypeConverter implements Converter<String, List<FeatureType>> {
        @Override
        public List<FeatureType> convert(MappingContext<String, List<FeatureType>> context) {
            String siteType = context.getSource();
            List<FeatureType> featureTypes = null;
            if (StringUtils.isNotBlank(siteType)) {
                String[] tokens = siteType.split(SEMI_COLON);
                featureTypes = Arrays.stream(tokens)
                        .filter(t -> !t.isEmpty())
                        .filter(t -> !t.startsWith(RS))
                        .map(FeatureType::type)
                        .collect(Collectors.toList());
            }
            return featureTypes;
        }
    }

    private static class DbSnpConverter implements Converter<String, List<String>> {
        @Override
        public List<String> convert(MappingContext<String, List<String>> context) {
            String siteType = context.getSource();
            List<String> dbSnps = null;
            if (StringUtils.isNotBlank(siteType)) {
                String[] tokens = siteType.split(SEMI_COLON);
                dbSnps = Arrays.stream(tokens)
                        .filter(t -> !t.isEmpty())
                        .filter(t -> t.startsWith(RS))
                        .collect(Collectors.toList());
            }
            return dbSnps;
        }
    }

    private class UniProtSiteConverter implements Converter<String, List<UniProtSite>> {
        @Override
        public List<UniProtSite> convert(MappingContext<String, List<UniProtSite>> context) {
            String mappedSites = context.getSource();
            List<UniProtSite> uniProtSites = null;
            if (StringUtils.isNotBlank(mappedSites)) {
                String[] tokens = mappedSites.split(SEMI_COLON);
                uniProtSites = Arrays.stream(tokens)
                        .map(UniProtSite::of)
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList());
            }
            return uniProtSites;
        }
    }
}
