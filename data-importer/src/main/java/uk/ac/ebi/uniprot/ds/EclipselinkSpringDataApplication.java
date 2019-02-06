/*
 * Created by sahmad on 28/01/19 19:14
 * UniProt Consortium.
 * Copyright (c) 2002-2019.
 *
 */

package uk.ac.ebi.uniprot.ds;

import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import uk.ac.ebi.uniprot.ds.controller.dto.DiseaseDTO;
import uk.ac.ebi.uniprot.ds.controller.dto.ProteinDTO;
import uk.ac.ebi.uniprot.ds.controller.filter.CorrelationHeaderFilter;
import uk.ac.ebi.uniprot.ds.controller.mapper.ProteinToProteinPathwaysDTOMap;
import uk.ac.ebi.uniprot.ds.model.*;

import java.util.Arrays;

@SpringBootApplication
public class EclipselinkSpringDataApplication {
    public static void main(String[] args) {
        SpringApplication.run(EclipselinkSpringDataApplication.class, args);
    }

    @Bean
    ModelMapper modelMapper(PropertyMap<Disease, DiseaseDTO> diseaseToDiseaseDTOMap, PropertyMap<Protein, ProteinDTO> proteinToProteinDTOMap) {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.addMappings(diseaseToDiseaseDTOMap);
        modelMapper.addMappings(proteinToProteinDTOMap);
        modelMapper.addMappings(new ProteinToProteinPathwaysDTOMap());
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
