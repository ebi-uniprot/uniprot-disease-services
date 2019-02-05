/*
 * Created by sahmad on 28/01/19 19:14
 * UniProt Consortium.
 * Copyright (c) 2002-2019.
 *
 */

package uk.ac.ebi.uniprot.ds;

import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.modelmapper.spi.MappingContext;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import uk.ac.ebi.uniprot.ds.controller.dto.DiseaseDTO;
import uk.ac.ebi.uniprot.ds.controller.filter.CorrelationHeaderFilter;
import uk.ac.ebi.uniprot.ds.model.Disease;
import uk.ac.ebi.uniprot.ds.model.Protein;
import uk.ac.ebi.uniprot.ds.model.Synonym;
import uk.ac.ebi.uniprot.ds.model.Variant;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@SpringBootApplication
public class EclipselinkSpringDataApplication {
    public static void main(String[] args) {
        SpringApplication.run(EclipselinkSpringDataApplication.class, args);
    }
    @Bean
    ModelMapper modelMapper(){
        ModelMapper modelMapper = new ModelMapper();
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
        return modelMapper;
    }

    @Bean
    public FilterRegistrationBean correlationHeaderFilter() {
        FilterRegistrationBean filterRegBean = new FilterRegistrationBean();
        filterRegBean.setFilter(new CorrelationHeaderFilter());
        filterRegBean.setUrlPatterns(Arrays.asList("/*"));

        return filterRegBean;
    }
}
