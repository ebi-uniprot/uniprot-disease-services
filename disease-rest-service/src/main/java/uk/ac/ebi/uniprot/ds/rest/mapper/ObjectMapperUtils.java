/*
 * Created by sahmad on 07/02/19 12:22
 * UniProt Consortium.
 * Copyright (c) 2002-2019.
 *
 */

package uk.ac.ebi.uniprot.ds.rest.mapper;

import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.modelmapper.convention.MatchingStrategies;
import org.modelmapper.spi.MappingContext;
import uk.ac.ebi.uniprot.ds.rest.dto.DiseaseDTO;
import uk.ac.ebi.uniprot.ds.common.model.Disease;
import uk.ac.ebi.uniprot.ds.common.model.Protein;
import uk.ac.ebi.uniprot.ds.common.model.Synonym;
import uk.ac.ebi.uniprot.ds.common.model.Variant;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ObjectMapperUtils {
    private ModelMapper modelMapper = new ModelMapper();

    public ObjectMapperUtils() {
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        // Protein to String(Accession) Converter
        Converter<Set<Protein>, List<String>> proteinToAccessionString = new Converter<Set<Protein>, List<String>>() {
            @Override
            public List<String> convert(MappingContext<Set<Protein>, List<String>> context) {
                Set<Protein> proteins = context.getSource();
                return proteins != null ? proteins.stream().map(pr -> pr.getAccession()).collect(Collectors.toList()) : null;
            }
        };

        // List<Synonym> to List<String> converter
        Converter<List<Synonym>, List<String>> synonymToStr = new Converter<List<Synonym>, List<String>>() {
            @Override
            public List<String> convert(MappingContext<List<Synonym>, List<String>> ctx) {
                List<Synonym> syns = ctx.getSource();
                List<String> synsStr = null;
                if (syns != null) {
                    synsStr = syns.stream().map(syn -> syn.getName()).collect(Collectors.toList());
                }
                return synsStr;
            }
        };

        // List<Variant> to List<String>
        Converter<List<Variant>, List<String>> varsToStr = new Converter<List<Variant>, List<String>>() {
            @Override
            public List<String> convert(MappingContext<List<Variant>, List<String>> ctx) {
                List<Variant> variants = ctx.getSource();
                List<String> varsStr = null;
                if (variants != null) {
                    varsStr = variants.stream().map(var -> var.getFeatureId()).collect(Collectors.toList());
                }
                return varsStr;
            }
        };

        PropertyMap<Disease, DiseaseDTO> propertyMap = new PropertyMap<Disease, DiseaseDTO>() {
            @Override
            protected void configure() {
                map().setDescription(source.getDesc());
                map().setDiseaseName(source.getName());
                using(proteinToAccessionString).map(source.getProteins()).setProteins(null);
                using(synonymToStr).map(source.getSynonyms()).setSynonyms(null);
                using(varsToStr).map(source.getVariants()).setVariants(null);
            }
        };
        modelMapper.addMappings(propertyMap);
    }

//
//    private ObjectMapperUtils() {
//    }

    public <D, T> D map(T entity, Class<D> outClass) {
        return modelMapper.map(entity, outClass);
    }

    public <D, T> List<D> mapAll(Collection<T> entityList, Class<D> outCLass) {
        return entityList.stream()
                .map(entity -> map(entity, outCLass))
                .collect(Collectors.toList());
    }
}
